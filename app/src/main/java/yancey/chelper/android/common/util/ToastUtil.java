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

package yancey.chelper.android.common.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.IntDef;
import androidx.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 显示通知
 */
public class ToastUtil {

    /**
     * 通知的显示时长
     * Toast.LENGTH_SHORT 长时间
     * Toast.LENGTH_LONG  短时间
     */
    @IntDef({Toast.LENGTH_SHORT, Toast.LENGTH_LONG})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {

    }

    /**
     * 不允许创建实例
     */
    private ToastUtil() {

    }

    public static void show(Context context, @StringRes int text) {
        show(context, context.getString(text), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, @StringRes int text, @Duration int duration) {
        show(context, context.getString(text), duration);
    }

    public static void show(Context context, String text, @Duration int duration) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        Toast.makeText(context, text, duration).show();
    }

}