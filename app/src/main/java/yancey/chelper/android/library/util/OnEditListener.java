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

package yancey.chelper.android.library.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import yancey.chelper.network.library.data.LibraryFunction;

public interface OnEditListener {
    void onCreate(@NonNull LibraryFunction libraryFunction);

    void onUpdate(@Nullable Integer position, @NonNull LibraryFunction before, @NonNull LibraryFunction after);

    void onDelete(@Nullable Integer position, @NonNull LibraryFunction libraryFunction);
}
