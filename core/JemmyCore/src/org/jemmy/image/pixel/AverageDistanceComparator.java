/*
 * Copyright (c) 2007, 2017 Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.jemmy.image.pixel;

import org.jemmy.Dimension;

/**
 *
 * @author shura
 */
public class AverageDistanceComparator extends ThresholdComparator {

    public AverageDistanceComparator(double threshold) {
        super(0, Math.sqrt(3));
        setThreshold(threshold);
    }

    public boolean compare(Raster image1, Raster image2) {
        Dimension size = PixelImageComparator.computeDiffSize(image1, image2);
        if (size == null) {
            return false;
        }
        int totalPixels = size.width * size.height;
        double distance = 0;
        double[] colors1 = new double[image1.getSupported().length];
        double[] colors2 = new double[image2.getSupported().length];
        for (int x = 0; x < size.width; x++) {
            for (int y = 0; y < size.height; y++) {
                image1.getColors(x, y, colors1);
                image2.getColors(x, y, colors2);
                distance += distance(image1.getSupported(), colors1, image2.getSupported(), colors2) / totalPixels;
            }
        }
        return distance < getThreshold();
    }

    public static final Raster.Component[] DISTANCE_COMPONENTS = {
        Raster.Component.RED, Raster.Component.BLUE, Raster.Component.GREEN
    };

    static double distance(Raster.Component[] comps1, double[] colors1, Raster.Component[] comps2, double[] colors2) {
        double res = 0;
        double diff;
        for (Raster.Component c : DISTANCE_COMPONENTS) {
            diff = colors2[PixelImageComparator.arrayIndexOf(comps2, c)] -
                    colors1[PixelImageComparator.arrayIndexOf(comps1, c)];
            res += diff * diff;
        }
        return Math.sqrt(res);
    }

    public String getID() {
        return AverageDistanceComparator.class.getName() + ":" + getThreshold();
    }
}
