package yancey.chelper.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import yancey.chelper.android.common.view.CommandEditText;
import yancey.chelper.android.settings.Settings;
import yancey.chelper.util.DataComplete;
import yancey.chelper.util.SelectedString;

public class CHelperCore {

    static {
        System.loadLibrary("CHelper");
    }

    @SuppressLint("StaticFieldLeak")
    public static final CHelperCore INSTANCE = new CHelperCore();

    private Runnable updateList;
    private Consumer<String> updateStructure, updateDescription;
    private CommandEditText commandEditText;
    private TextView mtv_errorReasons;

    private CHelperCore() {

    }

    public boolean initCPack(Context context) {
        return init(context.getAssets(), "beta-experiment-1.21.0.23.cpack");
    }

    public void setCommandEditText(CommandEditText commandEditText) {
        this.commandEditText = commandEditText;
    }

    public void setUpdateList(Runnable updateList) {
        this.updateList = updateList;
    }

    public void setUpdateStructure(Consumer<String> updateStructure) {
        this.updateStructure = updateStructure;
    }

    public void setUpdateDescription(Consumer<String> updateDescription) {
        this.updateDescription = updateDescription;
    }

    public void setTvErrorReasons(TextView mtv_errorReasons) {
        this.mtv_errorReasons = mtv_errorReasons;
    }

    private String lastInput = "";

    public void onSelectionChanged() {
        String text = Objects.requireNonNull(commandEditText.getText()).toString();
        if (text.isEmpty()) {
            lastInput = text;
            updateStructure.accept("欢迎使用CHelper");
            updateDescription.accept("作者：Yancey");
            if (mtv_errorReasons != null) {
                mtv_errorReasons.setVisibility(View.GONE);
            }
            onTextChanged(text, 0);
            updateList.run();
            return;
        }
        if (text.equals(lastInput)) {
            if (!Settings.getInstance(commandEditText.getContext()).isCheckingBySelection) {
                return;
            }
            int selectionStart = text.substring(0, commandEditText.getSelectionStart()).getBytes(StandardCharsets.UTF_8).length;
            onSelectionChanged(selectionStart);
        } else {
            int selectionStart;
            if (Settings.getInstance(commandEditText.getContext()).isCheckingBySelection) {
                selectionStart = text.substring(0, commandEditText.getSelectionStart()).getBytes(StandardCharsets.UTF_8).length;
            } else {
                selectionStart = text.getBytes(StandardCharsets.UTF_8).length;
            }
            onTextChanged(text, selectionStart);
            updateStructure.accept(getStructure());
            if (mtv_errorReasons != null) {
                String errorReasons = getErrorReasons();
                if (errorReasons == null || errorReasons.isEmpty()) {
                    mtv_errorReasons.setVisibility(View.GONE);
                } else {
                    mtv_errorReasons.setText(errorReasons);
                    mtv_errorReasons.setVisibility(View.VISIBLE);
                }
            }
            lastInput = text;
        }
        updateDescription.accept(getDescription());
        updateList.run();
    }

    public void onItemClick(int which) {
        String string = onSuggestionClick(which);
        if (string != null) {
            commandEditText.setSelectedString(new SelectedString(string, string.length(), string.length()));
        }
    }

    private native boolean init(@NonNull AssetManager assetManager, String cpackPath);

    private native void onTextChanged(@NonNull String text, int index);

    private native void onSelectionChanged(int index);

    private native String getDescription();

    private native String getErrorReasons();

    public native int getSuggestionsSize();

    public native DataComplete getSuggestion(int which);

    public native List<DataComplete> getSuggestions();

    private native String getStructure();

    private native String onSuggestionClick(int which);

}
