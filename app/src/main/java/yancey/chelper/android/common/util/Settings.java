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

package yancey.chelper.android.common.util;

import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;

/**
 * 软件设置
 */
public class Settings {

    /**
     * 内置资源包的文件夹名称
     */
    public static final String DIR_NAME = "cpack";
    /**
     * 默认命令分支
     */
    public static final String DEFAULT_CPACK = "release-experiment";
    /**
     * 正式版的版本号
     */
    public static final String VERSION_RELEASE = "1.21.81.2";
    /**
     * 测试版的版本号
     */
    public static final String VERSION_BETA = "1.21.90.26";
    /**
     * 中国版的版本号
     */
    public static final String VERSION_NETEASE = "1.20.10.25";
    /**
     * 配置文件路径
     */
    private static File file;
    /**
     * 设置实例
     */
    public static Settings INSTANCE;
    /**
     * 当前主题
     */
    public String themeId;
    /**
     * 根据光标位置提供补全提示
     */
    public Boolean isCheckingBySelection;
    /**
     * 复制后隐藏悬浮窗
     */
    public Boolean isHideWindowWhenCopying;
    /**
     * 是否在进入界面时保留上次输入的内容
     */
    public Boolean isSavingWhenPausing;
    /**
     * 紧凑模式
     */
    public Boolean isCrowed;
    /**
     * 语法高亮
     */
    public Boolean isSyntaxHighlight;
    /**
     * 选择的资源包分支
     */
    private String cpackPath;

    public void setCpackPath(String cpackPath) {
        this.cpackPath = cpackPath;
    }

    private static String getRealFileName(String cpackPath) {
        return switch (cpackPath) {
            case "release-vanilla" -> "release-vanilla-" + VERSION_RELEASE + ".cpack";
            case "release-experiment" -> "release-experiment-" + VERSION_RELEASE + ".cpack";
            case "beta-vanilla" -> "beta-vanilla-" + VERSION_BETA + ".cpack";
            case "beta-experiment" -> "beta-experiment-" + VERSION_BETA + ".cpack";
            case "netease-vanilla" -> "netease-vanilla-" + VERSION_NETEASE + ".cpack";
            case "netease-experiment" -> "netease-experiment-" + VERSION_NETEASE + ".cpack";
            default -> getRealFileName(DEFAULT_CPACK);
        };
    }

    public String getCpackPath() {
        return DIR_NAME + "/" + getRealFileName(cpackPath);
    }

    public String getCpackBranch() {
        return cpackPath;
    }

    /**
     * 保存设置到文件
     */
    public void save() {
        FileUtil.writeString(file, new Gson().toJson(this));
    }

    /**
     * 初始化
     *
     * @param file 配置文件路径
     */
    public static void init(File file) {
        Settings.file = file;
        try {
            INSTANCE = new Gson().fromJson(FileUtil.readString(file), Settings.class);
        } catch (Exception e) {
            Log.e("Settings", "fail to read settings", e);
        }
        boolean isDirty = false;
        if (INSTANCE == null) {
            INSTANCE = new Settings();
            isDirty = true;
        }
        if (INSTANCE.isCheckingBySelection == null) {
            INSTANCE.isCheckingBySelection = true;
            isDirty = true;
        }
        if (INSTANCE.isHideWindowWhenCopying == null) {
            INSTANCE.isHideWindowWhenCopying = false;
            isDirty = true;
        }
        if (INSTANCE.isSavingWhenPausing == null) {
            INSTANCE.isSavingWhenPausing = true;
            isDirty = true;
        }
        if (INSTANCE.isCrowed == null) {
            INSTANCE.isCrowed = false;
            isDirty = true;
        }
        if (INSTANCE.isSyntaxHighlight == null) {
            INSTANCE.isSyntaxHighlight = true;
            isDirty = true;
        }
        if (INSTANCE.cpackPath == null) {
            INSTANCE.cpackPath = DEFAULT_CPACK;
            isDirty = true;
        } else {
            boolean isOldVersion = true;
            // 为了对软件旧版本兼容，需要把一些旧版本的内容转为新版本内容
            switch (INSTANCE.cpackPath) {
                case "release-vanilla-1.20.80.05.cpack", "release-vanilla-1.21.1.03.cpack" ->
                        INSTANCE.cpackPath = "release-vanilla";
                case "release-experiment-1.20.80.05.cpack",
                     "release-experiment-1.21.1.03.cpack" ->
                        INSTANCE.cpackPath = "release-experiment";
                case "beta-vanilla-1.21.0.23.cpack", "beta-vanilla-1.21.20.21.cpack" ->
                        INSTANCE.cpackPath = "beta-vanilla";
                case "beta-experiment-1.21.0.23.cpack", "beta-experiment-1.21.20.21.cpack" ->
                        INSTANCE.cpackPath = "beta-experiment";
                case "netease-vanilla-1.20.10.25.cpack" -> INSTANCE.cpackPath = "netease-vanilla";
                case "netease-experiment-1.20.10.25.cpack" ->
                        INSTANCE.cpackPath = "netease-experiment";
                default -> isOldVersion = false;
            }
            if (isOldVersion) {
                isDirty = true;
            }
            if (INSTANCE.themeId == null) {
                INSTANCE.themeId = "MODE_NIGHT_FOLLOW_SYSTEM";
                isDirty = true;
            }
        }
        if (isDirty) {
            INSTANCE.save();
        }
    }

}
