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

package yancey.chelper.android.common.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.common.view.MainView;

/**
 * 使用单个View组成的界面
 * 悬浮窗只能使用单个View显示界面，所以为了方便在悬浮窗模式和应用模式共享界面代码，所以设计了CustomActivity
 *
 * @param <T> View的内容
 */
public abstract class CustomActivity<T extends CustomView> extends AppCompatActivity {

    private MainView<T> view;

    protected abstract T createView(@NonNull CustomView.CustomContext customContext);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MainView<>(
                this,
                CustomView.Environment.APPLICATION,
                this::createView,
                super::onBackPressed
        );
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets stateBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(stateBars.left, stateBars.top, stateBars.right, stateBars.bottom);
            return insets;
        });
        setContentView(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (view != null) {
            view.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (view != null) {
            view.onResume();
        }
    }

    @Override
    public void onBackPressed() {
        if (view == null) {
            super.onBackPressed();
        } else {
            view.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (view != null) {
            view.onDestroy();
        }
    }
}