package yancey.chelper.android.settings;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;

import yancey.chelper.util.FileUtil;

public class Settings {

    private static Settings INSTANCE;

    public boolean isCheckingBySelection;
    public boolean isHideWindowWhenCopying;
    public boolean isSavingWhenPausing;
    public boolean isCrowed;

    public Settings() {

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
            if (INSTANCE == null) {
                INSTANCE = new Settings();
                INSTANCE.isCheckingBySelection = true;
                INSTANCE.isHideWindowWhenCopying = false;
                INSTANCE.isSavingWhenPausing = true;
                INSTANCE.isCrowed = false;
                INSTANCE.save(context);
            }
        }
        return INSTANCE;
    }

}
