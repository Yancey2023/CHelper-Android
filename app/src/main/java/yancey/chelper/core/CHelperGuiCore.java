package yancey.chelper.core;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import yancey.chelper.android.common.view.CommandEditText;
import yancey.chelper.android.settings.Settings;
import yancey.chelper.util.SelectedString;

public class CHelperGuiCore implements Closeable {

    private @Nullable CHelperCore core;
    private @Nullable Runnable updateList;
    private @Nullable Consumer<String> updateStructure, updateDescription;
    private @Nullable CommandEditText commandEditText;
    private @Nullable TextView mtv_errorReasons;

    public CHelperGuiCore(@Nullable CHelperCore core) {
        this.core = core;
    }

    public CHelperGuiCore() {
        this(null);
    }

    public void setCommandEditText(@NotNull CommandEditText commandEditText) {
        this.commandEditText = commandEditText;
    }

    public void setUpdateList(@NotNull Runnable updateList) {
        this.updateList = updateList;
    }

    public void setUpdateStructure(@NotNull Consumer<String> updateStructure) {
        this.updateStructure = updateStructure;
    }

    public void setUpdateDescription(@NotNull Consumer<String> updateDescription) {
        this.updateDescription = updateDescription;
    }

    public void setTvErrorReasons(@NotNull TextView mtv_errorReasons) {
        this.mtv_errorReasons = mtv_errorReasons;
    }

    private String lastInput = "";

    public void onSelectionChanged() {
        if (commandEditText == null) {
            return;
        }
        String text = Objects.requireNonNull(commandEditText.getText()).toString();
        if (text.isEmpty()) {
            lastInput = text;
            if (updateStructure != null) {
                updateStructure.accept("欢迎使用CHelper");
            }
            if (updateDescription != null) {
                updateDescription.accept("作者：Yancey");
            }
            if (mtv_errorReasons != null) {
                mtv_errorReasons.setVisibility(View.GONE);
            }
            if (core != null) {
                core.onTextChanged(text, 0);
            }
            if (updateList != null) {
                updateList.run();
            }
            return;
        }
        if (core == null) {
            return;
        }
        if (text.equals(lastInput)) {
            if (!Settings.getInstance(commandEditText.getContext()).isCheckingBySelection) {
                return;
            }
            int selectionStart = text.substring(0, commandEditText.getSelectionStart()).getBytes(StandardCharsets.UTF_8).length;
            core.onSelectionChanged(selectionStart);
        } else {
            int selectionStart;
            if (Settings.getInstance(commandEditText.getContext()).isCheckingBySelection) {
                selectionStart = text.substring(0, commandEditText.getSelectionStart()).getBytes(StandardCharsets.UTF_8).length;
            } else {
                selectionStart = text.getBytes(StandardCharsets.UTF_8).length;
            }
            core.onTextChanged(text, selectionStart);
            if (updateStructure != null) {
                updateStructure.accept(core.getStructure());
            }
            if (mtv_errorReasons != null) {
                String errorReasons = core.getErrorReasons();
                if (errorReasons == null || errorReasons.isEmpty()) {
                    mtv_errorReasons.setVisibility(View.GONE);
                } else {
                    mtv_errorReasons.setText(errorReasons);
                    mtv_errorReasons.setVisibility(View.VISIBLE);
                }
            }
            lastInput = text;
        }
        if (updateDescription != null) {
            updateDescription.accept(core.getDescription());
        }
        if (updateList != null) {
            updateList.run();
        }
    }

    public void onItemClick(int which) {
        if (core == null || commandEditText == null) {
            return;
        }
        String string = core.onSuggestionClick(which);
        if (string != null) {
            commandEditText.setSelectedString(new SelectedString(string, string.length(), string.length()));
        }
    }

    public int getSuggestionsSize() {
        if (core == null) {
            return 0;
        }
        return core.getSuggestionsSize();
    }

    public @Nullable Suggestion getSuggestion(int which) {
        if (core == null) {
            return null;
        }
        return core.getSuggestion(which);
    }

    public @Nullable List<Suggestion> getSuggestions() {
        if (core == null) {
            return null;
        }
        return core.getSuggestions();
    }

    @Nullable
    public CHelperCore getCore() {
        return core;
    }

    public void setCore(@Nullable CHelperCore core) {
        if (this.core != null) {
            this.core.close();
        }
        this.core = core;
        lastInput = "";
        onSelectionChanged();
    }

    public void reset() {
        updateList = null;
        updateStructure = null;
        updateDescription = null;
        commandEditText = null;
        mtv_errorReasons = null;
        lastInput = "";
    }

    @Override
    public void close() {
        if (core != null) {
            core.close();
            core = null;
        }
        reset();
    }

}
