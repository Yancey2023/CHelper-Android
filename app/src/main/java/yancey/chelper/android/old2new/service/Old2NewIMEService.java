/**
 * It is part of CHelper. CHelper a command helper for Minecraft Bedrock Edition.
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

package yancey.chelper.android.old2new.service;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private @Nullable InputConnection inputConnection;
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
        // 兼容：QQ的输入框在文字改变后会立刻尝试弹出输入法，onWindowShown()被再次调用，撤回的内容会再次被替换成新版命令
        // 所以检测距离上次设置文字的时间间隔小于0.1s，就不再替换成新版命令
        if (System.currentTimeMillis() - time < 100) {
            return;
        }
        String oldCommand = getText();
        // 如果与前一次命令转换后的结果相同，说明用户再次点了文本框，不作处理
        if (Objects.equals(oldCommand, this.newCommand)) {
            return;
        }
        this.oldCommand = oldCommand;
        // 转换为新版命令
        newCommand = CHelperCore.old2new(this, oldCommand);
        // 如果与转换后新版指令与旧版一样，也不作处理
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
     * 获取输入框文本
     *
     * @return 输入框的文本
     */
    private @NonNull String getText() {
        if (inputConnection == null) {
            return "";
        }
        // 这里使用Short.MAX_VALUE，因为使用Integer.MAX_VALUE太大，QQ会崩
        CharSequence textBeforeCursor = inputConnection.getTextBeforeCursor(Short.MAX_VALUE, 0);
        CharSequence selectedText = inputConnection.getSelectedText(Short.MAX_VALUE);
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
        // 这里使用Short.MAX_VALUE，因为使用Integer.MAX_VALUE太大，QQ会崩
        CharSequence before = inputConnection.getTextBeforeCursor(Short.MAX_VALUE, 0);
        CharSequence selected = inputConnection.getSelectedText(Short.MAX_VALUE);
        CharSequence after = inputConnection.getTextAfterCursor(Short.MAX_VALUE, 0);
        int beforeLength = before == null ? 0 : before.length();
        int selectedLength = selected == null ? 0 : selected.length();
        int afterLength = after == null ? 0 : after.length();
        inputConnection.setSelection(0, beforeLength + selectedLength + afterLength);
        inputConnection.commitText(text, text.length());
    }

}
