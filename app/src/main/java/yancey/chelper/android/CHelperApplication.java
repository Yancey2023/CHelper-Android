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
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;

import com.hjq.toast.Toaster;

import java.io.File;

import yancey.chelper.android.common.style.CustomTheme;
import yancey.chelper.android.common.util.AssetsUtil;
import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.android.common.util.MonitorUtil;
import yancey.chelper.android.common.util.PolicyGrantManager;
import yancey.chelper.android.common.util.Settings;
import yancey.chelper.android.completion.util.CompletionWindowManager;
import yancey.chelper.android.library.util.LocalLibraryManager;
import yancey.chelper.network.ServiceManager;
import yancey.chelper.network.library.util.LoginUtil;

public class CHelperApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 隐私政策管理初始化
        PolicyGrantManager.init(
                AssetsUtil.readStringFromAssets(this, "about/privacy_policy.txt"),
                new File(getDataDir(), "lastReadContent.txt")
        );
        // 用于数据分析和性能监控的第三方库初始化
        MonitorUtil.init(this);
        // Toast初始化
        Toaster.init(this);
        Toaster.setGravity(Gravity.BOTTOM, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
        // 网络服务初始化
        ServiceManager.init();
        LoginUtil.init(FileUtil.getFile(getDataDir(), "library", "user.json"), throwable -> {
            Log.e("LoginUtil", "fail to read user from json", throwable);
            MonitorUtil.generateCustomLog(throwable, "ReadUserException");
        });
        // 设置初始化
        Settings.init(FileUtil.getFile(getDataDir(), "settings", "settings.json"), throwable -> {
            Log.e("Settings", "fail to read settings from json", throwable);
            MonitorUtil.generateCustomLog(throwable, "ReadSettingException");
        });
        // 悬浮窗管理初始化
        CompletionWindowManager.init(this, FileUtil.getFile(getDataDir(), "xiaomi_clipboard_permission_no_tips.txt"));
        // 自定义主题初始化
        CustomTheme.init(new File(getDataDir(), "theme"));
        // 本地命令库初始化
        LocalLibraryManager.init(FileUtil.getFile(getDataDir(), "localLibrary", "data.json"));
    }

}
