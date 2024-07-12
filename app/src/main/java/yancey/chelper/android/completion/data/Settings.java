package yancey.chelper.android.completion.data;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import yancey.chelper.android.common.util.FileUtil;

public class Settings {

    public static final String DIR_NAME = "cpack";
    public static final String DEFAULT_CPACK = "release-experiment-1.21.1.03.cpack";
    private static Settings INSTANCE;

    private List<String> cpackPaths;
    public Boolean isCheckingBySelection;
    public Boolean isHideWindowWhenCopying;
    public Boolean isSavingWhenPausing;
    public Boolean isCrowed;
    private String cpackPath;

    public void init(Context context) {
        try {
            cpackPaths = Arrays.asList(context.getAssets().list(DIR_NAME));
        } catch (IOException ignored) {
            cpackPaths = List.of(DEFAULT_CPACK);
        }
    }

    public void setCpackPath(String cpackPath) {
        this.cpackPath = cpackPath;
    }

    public String getCpackPath(Context context) {
        if (!cpackPaths.contains(cpackPath)) {
            cpackPath = DEFAULT_CPACK;
            save(context);
        }
        return DIR_NAME + "/" + cpackPath;
    }

    private static File getFileByContext(Context context) {
        return FileUtil.getFile(context.getFilesDir().getAbsolutePath(), "settings", "settings.json");
    }

    public void save(Context context) {
        FileUtil.writeString(getFileByContext(context), new Gson().toJson(this));
    }

    public static Settings getInstance(Context context) {
        if (INSTANCE == null) {
            try {
                INSTANCE = new Gson().fromJson(FileUtil.readString(getFileByContext(context)), Settings.class);
                INSTANCE.init(context);
            } catch (Exception e) {
                Log.e("Settings", "fail to read settings", e);
            }
            boolean isDirty = false;
            if (INSTANCE == null) {
                INSTANCE = new Settings();
                INSTANCE.init(context);
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
                switch (INSTANCE.cpackPath) {
                    case "release-vanilla-1.20.80.05.cpack" ->
                            INSTANCE.cpackPath = "release-vanilla-1.21.1.03.cpack";
                    case "release-experiment-1.20.80.05.cpack" ->
                            INSTANCE.cpackPath = "release-experiment-1.21.1.03.cpack";
                    case "beta-vanilla-1.21.0.23.cpack" ->
                            INSTANCE.cpackPath = "beta-vanilla-1.21.20.21.cpack";
                    case "beta-experiment-1.21.0.23.cpack" ->
                            INSTANCE.cpackPath = "beta-experiment-1.21.20.21.cpack";
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
