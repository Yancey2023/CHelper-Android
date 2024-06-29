package yancey.chelper.android.settings;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import yancey.chelper.util.FileUtil;

public class Settings {

    private static Settings INSTANCE;

    public Boolean isCheckingBySelection;
    public Boolean isHideWindowWhenCopying;
    public Boolean isSavingWhenPausing;
    public Boolean isCrowed;
    private String cpackPath;

    public Settings() {

    }

    public void setCpackPath(Context context, String cpackPath) {
        this.cpackPath = cpackPath;
        save(context);
    }

    public String getCpackPath(Context context) {
        try {
            if (!Arrays.asList(context.getAssets().list("cpack")).contains(cpackPath)) {
                cpackPath = "release-experiment-1.20.80.05.cpack";
                save(context);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return "cpack/" + cpackPath;
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
                INSTANCE.cpackPath = "release-experiment-1.20.80.05.cpack";
                isDirty = true;
            }
            if (isDirty) {
                INSTANCE.save(context);
            }
        }
        return INSTANCE;
    }

}
