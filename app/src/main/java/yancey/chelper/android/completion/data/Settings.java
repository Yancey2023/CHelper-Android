package yancey.chelper.android.completion.data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;

import yancey.chelper.android.common.util.FileUtil;

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
    public static final String VERSION_RELEASE = "1.21.20.03";
    /**
     * 测试版的版本号
     */
    public static final String VERSION_BETA = "1.21.30.22";
    /**
     * 中国版的版本号
     */
    public static final String VERSION_NETEASE = "1.20.10.25";
    /**
     * 设置实例
     */
    private static Settings INSTANCE;
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
     * 根据光标位置提供补全提示
     */
    public Boolean isCrowed;
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
            case "netease-vanilla" -> "netease-vanilla-" + VERSION_RELEASE + ".cpack";
            case "netease-experiment" -> "netease-experiment-" + VERSION_RELEASE + ".cpack";
            default -> getRealFileName(DEFAULT_CPACK);
        };
    }

    public String getCpackPath() {
        return DIR_NAME + "/" + getRealFileName(cpackPath);
    }

    public String getCpackBranch() {
        return cpackPath;
    }

    private static File getFileByContext(Context context) {
        return FileUtil.getFile(context.getFilesDir().getAbsolutePath(), "settings", "settings.json");
    }

    /**
     * 保存设置到文件
     *
     * @param context 上下文
     */
    public void save(Context context) {
        FileUtil.writeString(getFileByContext(context), new Gson().toJson(this));
    }

    /**
     * 获取设置，如果不存在就从文件读取设置
     *
     * @param context 上下文
     * @return 设置
     */
    public static Settings getInstance(Context context) {
        if (INSTANCE == null) {
            try {
                INSTANCE = new Gson().fromJson(FileUtil.readString(getFileByContext(context)), Settings.class);
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
                    case "netease-vanilla-1.20.10.25.cpack" ->
                            INSTANCE.cpackPath = "netease-vanilla";
                    case "netease-experiment-1.20.10.25.cpack" ->
                            INSTANCE.cpackPath = "netease-experiment";
                    default -> isOldVersion = false;
                }
                if (isOldVersion) {
                    isDirty = true;
                }
            }
            if (isDirty) {
                INSTANCE.save(context);
            }
        }
        return INSTANCE;
    }

}
