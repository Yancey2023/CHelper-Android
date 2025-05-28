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

import androidx.appcompat.app.AppCompatActivity;

import yancey.chelper.R;
import yancey.chelper.android.common.style.CustomTheme;
import yancey.chelper.android.common.util.MonitorUtil;
import yancey.chelper.android.common.view.CustomView;

/**
 * 作为Activity基类，提供通用的代码
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected abstract String gePageName();

    protected void onResume() {
        super.onResume();
        MonitorUtil.onPageStart(gePageName());
        CustomTheme.INSTANCE.invokeBackground(findViewById(R.id.main), CustomView.Environment.APPLICATION);

    }

    protected void onPause() {
        super.onPause();
        MonitorUtil.onPageEnd(gePageName());
    }

}
