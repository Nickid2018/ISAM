package com.github.isam.crash;

import oshi.*;
import java.io.*;
import java.util.*;
import java.text.*;
import oshi.hardware.*;
import java.util.stream.*;
import java.util.function.*;
import com.github.isam.*;
import java.lang.management.*;
import org.apache.commons.io.*;
import com.google.common.collect.*;

public class CrashReport {

	public static void main(String[] args) {
		System.out.println(new CrashReport("..", new Error(".....")).populateReport());
		new CrashReport("..", new Error(".....")).writeToFile("client");
	}

	private List<CrashReportSession> sessions = Lists.newArrayList();
	private String detail;
	private Throwable throwable;

	public CrashReport(String detail, Throwable error) {
		this.detail = detail;
		throwable = error;
	}

	public String populateReport() {
		fillSystemDetails();
		StringWriter s = new StringWriter();
		PrintWriter writer = new PrintWriter(s);
		writer.println(" ----- Chemistry Lab Crash Report");
		writer.println(getWittyComment());
		writer.println("Time: " + (new SimpleDateFormat()).format(new Date()));
		writer.println("Description: " + detail);
		writer.println("Stack Trace:");
		throwable.printStackTrace(writer);
		writer.println();
		writer.println(" ----- The details are as follows: ");
		for (CrashReportSession session : sessions) {
			writer.println();
			writer.println(session.toString());
		}
		writer.println();
		writer.println(
				"// REM: If you want to use the crash report to report a bug, please delete all of private information!");
		return s.toString();
	}

	public void writeToFile(String side) {
		try {
			File file = new File("crash-report/crash-"
					+ (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-" + side + ".txt");
			File dir = file.getParentFile();
			if (!dir.isDirectory())
				dir.mkdirs();
			FileWriter fw;
			fw = new FileWriter(file);
			IOUtils.write(populateReport(), fw);
			fw.close();
		} catch (IOException e) {
			System.err.println("Can't save crash report!");
			e.printStackTrace();
		}
	}

	public void addSession(CrashReportSession session) {
		sessions.add(session);
	}

	public void addSession(CrashReportSession session, int pos) {
		sessions.add(pos, session);
	}

	private void fillSystemDetails() {
		CrashReportSession system = new CrashReportSession("System & Runtime Details");
		sessions.add(system);
		system.addDetail("ISAM version", Constants.VERSION_IN_STRING);
		system.addDetail("Operating System", () -> System.getProperty("os.name") + " (" + System.getProperty("os.arch")
				+ ") version " + System.getProperty("os.version"));
		system.addDetail("Java Version",
				() -> System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
		system.addDetail("Java VM Version", () -> System.getProperty("java.vm.name") + " ("
				+ System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
		system.addDetail("Memory", () -> {
			Runtime runtime = Runtime.getRuntime();
			long l = runtime.maxMemory();
			long l1 = runtime.totalMemory();
			long l2 = runtime.freeMemory();
			long l3 = l / 1024L / 1024L;
			long l4 = l1 / 1024L / 1024L;
			long l5 = l2 / 1024L / 1024L;
			return l2 + " bytes (" + l5 + " MB) / " + l1 + " bytes (" + l4 + " MB) up to " + l + " bytes (" + l3
					+ " MB)";
		});
		system.addDetail("CPU Count", Integer.valueOf(Runtime.getRuntime().availableProcessors()));
		system.addDetail("VM Arguments", () -> {
			StringBuilder sb = new StringBuilder();
			List<String> vmArgs = getVmArguments().collect(Collectors.toList());
			sb.append("Total count ");
			sb.append(vmArgs.size());
			sb.append(";");
			vmArgs.forEach(s -> sb.append(" " + s));
			return sb.toString();
		});
		system.addDetail("User Arguments", () -> {
			StringBuilder sb = new StringBuilder();
			List<String> userArgs = getUserArguments().collect(Collectors.toList());
			sb.append("Total count ");
			sb.append(userArgs.size());
			sb.append(";");
			userArgs.forEach(s -> sb.append(" " + s));
			return sb.toString();
		});
		HardwareAbstractionLayer hardware = getOrNull(() -> new SystemInfo().getHardware());
		system.addDetail("Processor", () -> {
			if (hardware == null)
				return "Unknown";
			CentralProcessor processor = hardware.getProcessor();
			CentralProcessor.ProcessorIdentifier id = processor.getProcessorIdentifier();
			if (id == null)
				return "Unknown";
			StringBuilder sb = new StringBuilder();
			sb.append("Vendor=");
			sb.append(id.getVendor());
			sb.append("; Name=");
			sb.append(id.getName());
			sb.append("; Identifier=");
			sb.append(id.getIdentifier());
			sb.append("; Microarchitecture=");
			sb.append(id.getMicroarchitecture());
			sb.append("; Frequency=");
			sb.append(String.format("%.2f", ((float) id.getVendorFreq()) / 1.0E9F));
			sb.append("GHz; Physical packages=");
			sb.append(processor.getPhysicalPackageCount());
			sb.append("; Physical CPUs=");
			sb.append(processor.getPhysicalProcessorCount());
			sb.append("; Logical CPUs=");
			sb.append(processor.getLogicalProcessorCount());
			return sb.toString();
		});
		List<GraphicsCard> gcards = hardware == null ? Lists.newArrayList() : hardware.getGraphicsCards();
		for (int i = 0; i < gcards.size(); i++) {
			GraphicsCard card = gcards.get(i);
			system.addDetail("Graphics Card #" + i, () -> {
				if (card == null)
					return "Unknown";
				StringBuilder sb = new StringBuilder();
				sb.append("Name=");
				sb.append(card.getName());
				sb.append("; Vendor=");
				sb.append(card.getVendor());
				sb.append("; VRAM=");
				sb.append(String.format("%.2f", ((float) card.getVRam()) / 1048576.0F));
				sb.append("MB; DeviceId=");
				sb.append(card.getDeviceId());
				sb.append("; VersionInfo=");
				sb.append(card.getVersionInfo());
				return sb.toString();
			});
		}
		GlobalMemory memory = hardware == null ? null : hardware.getMemory();
		List<PhysicalMemory> phymems = memory == null ? Lists.newArrayList() : memory.getPhysicalMemory();
		for (int i = 0; i < phymems.size(); i++) {
			PhysicalMemory mem = phymems.get(i);
			system.addDetail("Memory Slot #" + i, () -> {
				if (mem == null)
					return "Unknown";
				StringBuilder sb = new StringBuilder();
				sb.append("Capacity=");
				sb.append(String.format("%.2f", ((float) mem.getCapacity()) / 1048576.0F));
				sb.append("MB; Clockspeed=");
				sb.append(String.format("%.2f", ((float) mem.getClockSpeed()) / 1.0E9F));
				sb.append("GHz; Type=");
				sb.append(mem.getMemoryType());
				return sb.toString();
			});
		}
		system.addDetail("Virtual Memory", () -> {
			if (memory == null)
				return "Unknown";
			VirtualMemory mem = memory.getVirtualMemory();
			StringBuilder sb = new StringBuilder();
			sb.append("Max=");
			sb.append(String.format("%.2f", ((float) mem.getVirtualMax()) / 1048576.0F));
			sb.append("MB; Used=");
			sb.append(String.format("%.2f", ((float) mem.getVirtualInUse()) / 1048576.0F));
			sb.append("MB; Swap Total=");
			sb.append(String.format("%.2f", ((float) mem.getSwapTotal()) / 1048576.0F));
			sb.append("MB; Swap Used=");
			sb.append(String.format("%.2f", ((float) mem.getSwapUsed()) / 1048576.0F));
			sb.append("MB");
			return sb.toString();
		});
	}

	public static Stream<String> getVmArguments() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMXBean.getInputArguments().stream().filter(string -> string.startsWith("-X"));
	}

	public static Stream<String> getUserArguments() {
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		return runtimeMXBean.getInputArguments().stream().filter(string -> !string.startsWith("-X"));
	}

	public static final List<String> WITTY_COMMENT = Arrays.asList("To be or not to be, this is up to Archibald Lee",
			"O, I am slain!", "===FBI WARNING===", "This bug must be created by mmc1234!", "A-W-S-L",
			"Why the sp3 minus the sp gets the sp2?", "I have a bad feeling about this.",
			"Make the copper multiplied by alumium and then divided by chlorine, and you can get the gold.",
			"The polar bear can't dissolve in benzene, for it is polar.",
			"Don't let the Fluoride Hydroxide into the lightbulb, or the liquid will make it broken.",
			"As we know, Mercaptan and Mermaid have the same origin.",
			"P2O5 is an excellent chemical, for it can turn the mercury into the silver.",
			"Shout at the Hg-198:\"The new gay! Hand out your proton!\", and you can gain much gold.");

	public static String getWittyComment() {
		return "// " + WITTY_COMMENT.get(Constants.RANDOM.nextInt(WITTY_COMMENT.size()));
	}

	private static <T> T getOrNull(Supplier<T> supplier) {
		try {
			return supplier.get();
		} catch (Exception e) {
			return null;
		}
	}
}
