package yancey.chelper.core;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.util.List;
import java.util.Objects;

import yancey.chelper.android.common.util.ToastUtil;

public class CHelperCore implements Closeable {

    static {
        System.loadLibrary("CHelper");
    }

    private static boolean isOld2NewInit = false;
    private final boolean isAssets;
    private final String path;
    private long pointer;

    private CHelperCore(@Nullable AssetManager assetManager, @NotNull String path) {
        this.isAssets = assetManager != null;
        this.path = path;
        try {
            pointer = create0(assetManager, path);
        } catch (Exception e) {
            pointer = 0;
        }
        if (pointer == 0) {
            throw new RuntimeException("fail to init CHelper Core: " + path);
        }
    }

    public static CHelperCore fromAssets(@NotNull AssetManager assetManager, String path) {
        return new CHelperCore(Objects.requireNonNull(assetManager), path);
    }

    public static CHelperCore fromFile(String path) {
        return new CHelperCore(null, path);
    }

    public void onTextChanged(@NonNull String text, int index) {
        if (pointer == 0) {
            return;
        }
        onTextChanged0(pointer, text, index);
    }

    public void onSelectionChanged(int index) {
        if (pointer == 0) {
            return;
        }
        onSelectionChanged0(pointer, index);
    }

    public String getDescription() {
        if (pointer == 0) {
            return null;
        }
        return getDescription0(pointer);
    }

    public String getErrorReasons() {
        if (pointer == 0) {
            return null;
        }
        return getErrorReasons0(pointer);
    }

    public int getSuggestionsSize() {
        if (pointer == 0) {
            return 0;
        }
        return getSuggestionsSize0(pointer);
    }

    public Suggestion getSuggestion(int which) {
        if (pointer == 0) {
            return null;
        }
        return getSuggestion0(pointer, which);
    }

    public List<Suggestion> getSuggestions() {
        if (pointer == 0) {
            return null;
        }
        return getSuggestions0(pointer);
    }

    public String getStructure() {
        if (pointer == 0) {
            return null;
        }
        return getStructure0(pointer);
    }

    public String onSuggestionClick(int which) {
        if (pointer == 0) {
            return null;
        }
        return onSuggestionClick0(pointer, which);
    }

    @Override
    public void close() {
        if (pointer == 0) {
            return;
        }
        release0(pointer);
        pointer = 0;
    }

    public static @NonNull String old2new(Context context, String old) {
        if (old == null) {
            return "";
        }
        if (!isOld2NewInit) {
            if (old2newInit0(context.getAssets(), "old2new/old2new.dat")) {
                isOld2NewInit = true;
            }
            if (!isOld2NewInit) {
                ToastUtil.show(context, "旧版命令转新版命令初始化失败");
                return old;
            }
        }
        return old2new0(old);
    }

    public boolean isAssets() {
        return isAssets;
    }

    public String getPath() {
        return path;
    }

    private static native long create0(@Nullable AssetManager assetManager, @NonNull String cpackPath);

    private static native void release0(long pointer);

    private static native void onTextChanged0(long pointer, @NonNull String text, int index);

    private static native void onSelectionChanged0(long pointer, int index);

    private static native String getDescription0(long pointer);

    private static native String getErrorReasons0(long pointer);

    private static native int getSuggestionsSize0(long pointer);

    private static native Suggestion getSuggestion0(long pointer, int which);

    public static native List<Suggestion> getSuggestions0(long pointer);

    private static native String getStructure0(long pointer);

    private static native String onSuggestionClick0(long pointer, int which);

    private static native boolean old2newInit0(@NonNull AssetManager assetManager, @NonNull String path);

    private static native String old2new0(@NonNull String old);


}
