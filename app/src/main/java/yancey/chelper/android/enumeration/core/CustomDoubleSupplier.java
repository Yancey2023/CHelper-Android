/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
 * Copyright (C) 2025  Yancey
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package yancey.chelper.android.enumeration.core;

import java.util.function.DoubleSupplier;

/**
 * 穷举表达式的参数提供器
 */
public class CustomDoubleSupplier implements DoubleSupplier {

    private final double interval;
    private double current;

    public CustomDoubleSupplier(double start, double interval) {
        this.interval = interval;
        this.current = start;
    }

    @Override
    public double getAsDouble() {
        return current;
    }

    public void nextTime() {
        current += interval;
    }
}
