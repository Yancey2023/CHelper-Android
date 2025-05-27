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

package yancey.chelper.android.common.style;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import yancey.chelper.R;
import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.android.common.util.Settings;
import yancey.chelper.android.common.view.CustomView;

public class CustomTheme {

    private static final String TAG = "CustomTheme";

    public static CustomTheme INSTANCE;

    @NonNull
    private final File file;
    @Nullable
    private Bitmap backgroundBitmap;

    public CustomTheme(@NonNull File file) {
        this.file = file;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            backgroundBitmap = BitmapFactory.decodeStream(fileInputStream);
        } catch (IOException e) {
            Log.w(TAG, "fail to load background drawable", e);
        }
    }

    public void invokeBackground(@Nullable View view, @NonNull CustomView.Environment environment) {
        if (view == null) {
            Log.w(TAG, "fail to draw background beacause view is null");
            return;
        }
        if (backgroundBitmap == null) {
            view.setBackgroundResource(R.color.background);
            return;
        }
        if (view.getWidth() == 0 || view.getHeight() == 0) {
            view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    invokeBackground0(view, environment);
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
            return;
        }
        invokeBackground0(view, environment);

    }

    private void invokeBackground0(@NonNull View view, @NonNull CustomView.Environment environment) {
        if (backgroundBitmap == null) {
            return;
        }
        int sourceWidth = backgroundBitmap.getWidth();
        int sourceHeight = backgroundBitmap.getHeight();
        int targetWidth = view.getWidth();
        int targetHeight = view.getHeight();
        if (sourceWidth == 0 || sourceHeight == 0) {
            Log.w(TAG, "fail to draw background beacause bitmap is bad");
            return;
        }
        if (targetWidth == 0 || targetHeight == 0) {
            Log.w(TAG, "fail to draw background beacause view is not ready");
            return;
        }
        Bitmap tagetBitmap;
        try {
            float scaleX = (float) targetWidth / sourceWidth;
            float scaleY = (float) targetHeight / sourceHeight;
            float scale = Math.max(scaleX, scaleY);
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
            float skipX = sourceWidth - targetWidth / scale;
            float skipY = sourceHeight - targetHeight / scale;
            tagetBitmap = Bitmap.createBitmap(
                    backgroundBitmap,
                    (int) (skipX / 2),
                    (int) (skipY / 2),
                    (int) (sourceWidth - skipX),
                    (int) (sourceHeight - skipY),
                    matrix,
                    false
            );
        } catch (Exception e) {
            Log.e(TAG, "fail to scale background", e);
            return;
        }
        view.setBackground(new BitmapDrawable(view.getResources(), tagetBitmap));
    }

    public void setBackGroundDrawable(@Nullable Bitmap backGroundBitmap) throws IOException {
        this.backgroundBitmap = backGroundBitmap;
        if (backGroundBitmap == null) {
            if (file.exists() && !file.delete()) {
                throw new IOException("fail to delete file");
            }
            return;
        }
        if (!FileUtil.createParentFile(file)) {
            throw new IOException("fail to create parent directory");
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            backGroundBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        }
    }

    public static void init(File file) {
        INSTANCE = new CustomTheme(new File(file, "background.png"));
        refreshTheme();
    }

    public static void refreshTheme() {
        switch (Settings.INSTANCE.themeId) {
            case "MODE_NIGHT_NO" ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            case "MODE_NIGHT_YES" ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            case "MODE_NIGHT_FOLLOW_SYSTEM" ->
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            default -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                Settings.INSTANCE.themeId = "MODE_NIGHT_FOLLOW_SYSTEM";
                Settings.INSTANCE.save();
            }
        }
    }

}
