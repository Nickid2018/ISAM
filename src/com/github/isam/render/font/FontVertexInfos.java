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

package com.github.isam.render.font;

public class FontVertexInfos {

    public FontAtlas atlas;

    /* Font Texture Position in [0,1] */
    public float minU;
    public float minV;
    public float maxU;
    public float maxV;

    /* Offset in pixel */
    public float leftSide;
    public float advanceWidth;
    public float topSide;

}
