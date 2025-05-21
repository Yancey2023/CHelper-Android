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

package yancey.chelper.android.common.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;
import java.util.function.Consumer;

import yancey.chelper.R;

/**
 * 输入文字的对话框
 */
public class InputStringDialog extends FixedDialog {

    // 标题，取消按钮文本，确认按钮文本，默认文本
    @Nullable
    private String title, confirm, cancel, defaultInput;
    // 取消按钮按下后的事件
    @Nullable
    private Consumer<String> onCancel;
    @Nullable
    private Consumer<String> onConfirm;

    public InputStringDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_string);
        TextView tv_title = findViewById(R.id.title);
        EditText et_title = findViewById(R.id.input);
        TextView btn_confirm = findViewById(R.id.confirm);
        TextView btn_cancel = findViewById(R.id.cancel);
        Objects.requireNonNull(tv_title);
        Objects.requireNonNull(et_title);
        Objects.requireNonNull(btn_confirm);
        Objects.requireNonNull(btn_cancel);
        tv_title.setText(Objects.requireNonNullElse(title, getContext().getString(R.string.title)));
        et_title.setText(defaultInput);
        btn_confirm.setText(Objects.requireNonNullElse(confirm, getContext().getString(R.string.confirm)));
        btn_cancel.setText(Objects.requireNonNullElse(cancel, getContext().getString(R.string.cancel)));
        btn_confirm.setOnClickListener(view -> {
            if (onConfirm != null) {
                onConfirm.accept(et_title.getText().toString());
            }
            dismiss();
        });
        btn_cancel.setOnClickListener(view -> {
            if (onCancel != null) {
                onCancel.accept(et_title.getText().toString());
            }
            dismiss();
        });
    }

    public InputStringDialog title(String title) {
        this.title = title;
        return this;
    }

    public InputStringDialog defaultInput(String defaultInput) {
        this.defaultInput = defaultInput;
        return this;
    }

    public InputStringDialog onConfirm(String confirm) {
        this.confirm = confirm;
        return this;
    }

    public InputStringDialog onConfirm(Consumer<String> onConfirm) {
        this.onConfirm = onConfirm;
        return this;
    }

    @SuppressWarnings("unused")
    public InputStringDialog onConfirm(String confirm, Consumer<String> onConfirm) {
        return onConfirm(confirm).onConfirm(onConfirm);
    }

    public InputStringDialog onCancel(String cancel) {
        this.cancel = cancel;
        return this;
    }

    public InputStringDialog onCancel(Consumer<String> onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    @SuppressWarnings("unused")
    public InputStringDialog onCancel(String cancel, Consumer<String> onCancel) {
        return onCancel(cancel).onCancel(onCancel);
    }

}
