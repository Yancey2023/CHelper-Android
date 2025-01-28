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

package yancey.chelper.android.common.span;

import android.os.Build;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class UnderlineSpan extends CharacterStyle implements UpdateAppearance {

    private final int underlineColor;

    public UnderlineSpan(@ColorInt int underlineColor) {
        this.underlineColor = underlineColor;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ds.underlineColor = underlineColor;
        }
        ds.setUnderlineText(true);
    }

    @NonNull
    @Override
    public String toString() {
        return "UnderlineSpan{}";
    }
}
