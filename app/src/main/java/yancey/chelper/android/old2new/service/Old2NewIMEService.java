package yancey.chelper.android.old2new.service;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import yancey.chelper.R;
import yancey.chelper.core.CHelperCore;

/**
 * 旧命令转新命令输入法服务
 */
public class Old2NewIMEService extends InputMethodService {

    private long time = 0;
    private InputConnection inputConnection;
    private String oldCommand, newCommand;
    private boolean lastIsUndo;

    @Override
    @SuppressLint("InflateParams")
    public View onCreateInputView() {
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_old2new_ime, null);
        linearLayout.findViewById(R.id.btn_undo).setOnClickListener(v -> {
            if (lastIsUndo) {
                setText(oldCommand);
                lastIsUndo = false;
            }
        });
        return linearLayout;
    }

    @Override
    public void onWindowShown() {
        super.onWindowShown();
        if (inputConnection == null) {
            inputConnection = getCurrentInputConnection();
            if (inputConnection == null) {
                return;
            }
        }
        // 兼容：QQ的输入框在文字改变后会立刻尝试呼出输入法，onWindowShown()被再次调用，撤回的内容会再次被替换成新版命令
        // 所以检测距离上次设置文字的时间间隔小于0.1s，就不再替换成新版命令
        if (System.currentTimeMillis() - time < 100) {
            return;
        }
        if (inputConnection == null) {
            return;
        }
        String oldCommand = getText();
        if (Objects.equals(oldCommand, this.newCommand)) {
            return;
        }
        this.oldCommand = oldCommand;
        newCommand = CHelperCore.old2new(this, oldCommand);
        lastIsUndo = !Objects.equals(oldCommand, newCommand);
        if (lastIsUndo) {
            setText(newCommand);
        }
    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
        inputConnection = null;
    }

    /**
     * 获取数据框文本
     *
     * @return 数据框的文本
     */
    private @NonNull String getText() {
        if (inputConnection == null) {
            return "";
        }
        // 这里使用Short.MAX_VALUE，因为使用Integer.MAX_VALUE太大，QQ会崩
        CharSequence textBeforeCursor = inputConnection.getTextBeforeCursor(Short.MAX_VALUE, 0);
        CharSequence selectedText = inputConnection.getSelectedText(0);
        CharSequence textAfterCursor = inputConnection.getTextAfterCursor(Short.MAX_VALUE, 0);
        return Stream.of(textBeforeCursor, selectedText, textAfterCursor)
                .filter(Objects::nonNull)
                .collect(Collectors.joining());
    }

    /**
     * 设置输入框文本
     *
     * @param text 要设置的文本
     */
    private void setText(String text) {
        if (inputConnection == null) {
            return;
        }
        time = System.currentTimeMillis();
        CharSequence before = inputConnection.getTextBeforeCursor(Short.MAX_VALUE, 0);
        CharSequence after = inputConnection.getTextAfterCursor(Short.MAX_VALUE, 0);
        int beforeLength = before == null ? 0 : before.length();
        int afterLength = after == null ? 0 : after.length();
        inputConnection.deleteSurroundingText(beforeLength, afterLength);
        inputConnection.commitText(text, text.length());
    }

}
