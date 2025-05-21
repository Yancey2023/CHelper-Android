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

package yancey.chelper.android;

import android.app.Application;
import android.util.TypedValue;
import android.view.Gravity;

import com.hjq.toast.Toaster;

import java.io.File;

import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.network.ServiceManager;
import yancey.chelper.network.library.util.LoginUtil;

public class CHelperApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Toaster.init(this);
        Toaster.setGravity(Gravity.BOTTOM, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        ServiceManager.init();
        LoginUtil.init(FileUtil.getFile(getDataDir(), "library", "user.json"));
    }

}
