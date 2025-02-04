/**
 * It is part of CHelper. CHelper a command helper for Minecraft Bedrock Edition.
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

package yancey.chelper.network.data.averychims;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Library {

    @Nullable
    public String name;
    @Nullable
    public String description;
    @Nullable
    public String author;
    @Nullable
    public List<Command> commands;

    public static class Command {
        @Nullable
        public String content;
        @Nullable
        public String description;
    }

}
