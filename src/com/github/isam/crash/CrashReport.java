/*
 * Copyright 2021 ISAM
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.isam.crash;

import com.github.isam.Constants;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import oshi.SystemInfo;
import oshi.hardware.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CrashReport {

    public static final List<String> WITTY_COMMENT = Arrays.asList("That can run well on my computer!", "Oh no");
    private final List<CrashReportSession> sessions = Lists.newArrayList();
    private final String detail;
    private final Throwable throwable;

    public CrashReport(String detail, Throwable error) {
        this.detail = detail;
        throwable = error;
    }

    public static Stream<String> getVmArguments() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getInputArguments().stream().filter(string -> string.startsWith("-X"));
    }

    public static Stream<String> getUserArguments() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return runtimeMXBean.getInputArguments().stream().filter(string -> !string.startsWith("-X"));
    }

    public static String getWittyComment() {
        return "// " + WITTY_COMMENT.get(Constants.RANDOM.nextInt(WITTY_COMMENT.size()));
    }

    @Nullable
    private static <T> T getOrNull(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            return null;
        }
    }

    @Nonnull
    public String populateReport() {
        fillSystemDetails();
        StringWriter s = new StringWriter();
        PrintWriter writer = new PrintWriter(s);
        writer.println(" ----- ISAM Crash Report");
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

    public void writeToFile() {
        try {
            File file = new File(
                    "crash-report/crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + ".txt");
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

    public void addSession(@Nonnull CrashReportSession session) {
        sessions.add(session);
    }

    public void addSession(@Nonnull CrashReportSession session, int pos) {
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
        system.addDetail("CPU Count", Runtime.getRuntime().availableProcessors());
        system.addDetail("VM Arguments", () -> {
            StringBuilder sb = new StringBuilder();
            List<String> vmArgs = getVmArguments().collect(Collectors.toList());
            sb.append("Total count ");
            sb.append(vmArgs.size());
            sb.append(";");
            vmArgs.forEach(s -> sb.append(" ").append(s));
            return sb.toString();
        });
        system.addDetail("User Arguments", () -> {
            StringBuilder sb = new StringBuilder();
            List<String> userArgs = getUserArguments().collect(Collectors.toList());
            sb.append("Total count ");
            sb.append(userArgs.size());
            sb.append(";");
            userArgs.forEach(s -> sb.append(" ").append(s));
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
            return String.format("Vendor=%s; Name=%s; Identifier=%s; Micro-architecture=%s;" +
                    " Frequency=%.2fGHz; Physical packages=%d; Physical CPUs=%d; Logical CPUs=%d"
                    , id.getVendor(), id.getName(), id.getIdentifier(), id.getMicroarchitecture(),
                    (float) id.getVendorFreq() / 1.0E9F,
                    processor.getPhysicalPackageCount(), processor.getPhysicalProcessorCount(), processor.getLogicalProcessorCount());
        });
        List<GraphicsCard> gcards = hardware == null ? Lists.newArrayList() : hardware.getGraphicsCards();
        for (int i = 0; i < gcards.size(); i++) {
            GraphicsCard card = gcards.get(i);
            system.addDetail("Graphics Card #" + i, () -> {
                if (card == null)
                    return "Unknown";
                return String.format("Name=%s; Vendor=%s; VRAM=%.2fMB; DeviceId=%s; VersionInfo=%s",
                        card.getName(), card.getVendor(),
                        (float) card.getVRam() / 1048576.0F, card.getDeviceId(), card.getVersionInfo());
            });
        }
        GlobalMemory memory = hardware == null ? null : hardware.getMemory();
        List<PhysicalMemory> phymems = memory == null ? Lists.newArrayList() : memory.getPhysicalMemory();
        for (int i = 0; i < phymems.size(); i++) {
            PhysicalMemory mem = phymems.get(i);
            system.addDetail("Memory Slot #" + i, () -> {
                if (mem == null)
                    return "Unknown";
                return String.format("Capacity=%.2fMB; ClockSpeed=%.2fGHz; Type=%s",
                        (float) mem.getCapacity() / 1048576.0F,
                        (float) mem.getClockSpeed() / 1.0E9F, mem.getMemoryType());
            });
        }
        system.addDetail("Virtual Memory", () -> {
            if (memory == null)
                return "Unknown";
            VirtualMemory mem = memory.getVirtualMemory();
            return String.format("Max=%.2fMB; Used=%.2fMB; Swap Total=%.2fMB; Swap Used=%.2fMB",
                    (float) mem.getVirtualMax() / 1048576.0F,
                    (float) mem.getVirtualInUse() / 1048576.0F,
                    (float) mem.getSwapTotal() / 1048576.0F,
                    (float) mem.getSwapUsed() / 1048576.0F);
        });
    }
}
