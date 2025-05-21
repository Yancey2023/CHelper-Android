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

package yancey.chelper.network.library.util;

import yancey.chelper.network.library.data.LibraryFunction;

public class CommandLabUtil {

    public static String libraryToStr(LibraryFunction library) {
        return "@author = " + library.author + '\n' +
               "@name = " + library.name + '\n' +
               "@note = " + library.note + '\n' +
               "@version = " + library.version + '\n' +
               "@tags = " + (library.tags == null ? null : String.join(",", library.tags) + '\n') +
               "###Function###\n" +
               library.content + '\n' +
               "###End###";
    }

    public static int getMaxLength() {
        return 1000000;
    }

}
