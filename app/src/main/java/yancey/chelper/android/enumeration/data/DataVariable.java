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

package yancey.chelper.android.enumeration.data;

import redempt.crunch.data.Pair;
import yancey.chelper.android.enumeration.core.CustomDoubleSupplier;

/**
 * 穷举界面列表中每个视图的数据
 */
public class DataVariable {

    public String name, start, interval;

    public DataVariable() {
        this("", "", "");
    }

    public DataVariable(String name, String start, String interval) {
        this.name = name;
        this.start = start;
        this.interval = interval;
    }

    public Pair<String, CustomDoubleSupplier> getValue() throws NumberFormatException {
        return new Pair<>(name, new CustomDoubleSupplier(Double.parseDouble(start), Double.parseDouble(interval)));
    }
}
