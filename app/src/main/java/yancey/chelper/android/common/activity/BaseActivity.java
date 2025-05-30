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
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.common.style.CustomTheme;
import yancey.chelper.android.common.util.MonitorUtil;

/**
 * 作为Activity基类，提供通用的代码
 */
public abstract class BaseActivity extends AppCompatActivity {

    private int backgroundUpdateTimes = 0;

    protected abstract String gePageName();

    public abstract @LayoutRes int getLayoutId();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        View mainView = findViewById(R.id.main);
        Objects.requireNonNull(mainView);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets stateBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(stateBars.left, stateBars.top, stateBars.right, stateBars.bottom);
            return insets;
        });
        mainView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (right - left != oldRight - oldLeft || bottom - top != oldBottom - oldTop) {
                backgroundUpdateTimes = CustomTheme.INSTANCE.invokeBackgroundForce(mainView);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        MonitorUtil.onPageStart(gePageName());
        backgroundUpdateTimes = CustomTheme.INSTANCE.invokeBackground(findViewById(R.id.main), backgroundUpdateTimes);
    }

    protected void onPause() {
        super.onPause();
        MonitorUtil.onPageEnd(gePageName());
    }

}
