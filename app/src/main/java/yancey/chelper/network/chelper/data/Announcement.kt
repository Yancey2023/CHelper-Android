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

package yancey.chelper.network.chelper.data;

import androidx.annotation.Nullable;

import java.util.Objects;

public class Announcement {
    @Nullable
    public Boolean isEnable;
    @Nullable
    public Boolean isForce;
    @Nullable
    public Boolean isBigDialog;
    @Nullable
    public String title;
    @Nullable
    public String message;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Announcement that = (Announcement) o;
        return Objects.equals(isEnable, that.isEnable) && Objects.equals(isForce, that.isForce) && Objects.equals(isBigDialog, that.isBigDialog) && Objects.equals(title, that.title) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isEnable, isForce, isBigDialog, title, message);
    }
}
