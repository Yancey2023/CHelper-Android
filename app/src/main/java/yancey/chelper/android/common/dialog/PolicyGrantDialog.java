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
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import yancey.chelper.R;

/**
 * 隐私政策授权对话框
 */
public class PolicyGrantDialog extends FixedDialog {

    // 标题，内容，取消按钮文本，确认按钮文本
    @Nullable
    private String title, message, confirm, cancel;
    // 确认按钮按下后的事件，取消按钮按下后的事件，对话框消失的事件
    @Nullable
    private Runnable onConfirm, onRead, onDismiss;

    /**
     * @param context 上下文
     */
    public PolicyGrantDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pilocy_grant);
        setCancelable(false);
        TextView tv_title = findViewById(R.id.title);
        TextView tv_message = findViewById(R.id.message);
        TextView btn_confirm = findViewById(R.id.confirm);
        TextView btn_read = findViewById(R.id.read);
        CheckBox cb_confirm_read = findViewById(R.id.confirm_read);
        Objects.requireNonNull(tv_title);
        Objects.requireNonNull(tv_message);
        Objects.requireNonNull(btn_confirm);
        Objects.requireNonNull(btn_read);
        Objects.requireNonNull(cb_confirm_read);
        tv_title.setText(Objects.requireNonNullElse(title, getContext().getString(R.string.title)));
        tv_message.setText(message);
        btn_confirm.setText(Objects.requireNonNullElse(confirm, getContext().getString(R.string.confirm)));
        btn_read.setText(Objects.requireNonNullElse(cancel, getContext().getString(R.string.read_privacy_policy)));
        if (cb_confirm_read.isChecked()) {
            btn_confirm.setTextColor(getContext().getColor(R.color.main_color));
        } else {
            btn_confirm.setTextColor(getContext().getColor(R.color.text_secondary));
        }
        cb_confirm_read.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                btn_confirm.setTextColor(getContext().getColor(R.color.main_color));
            } else {
                btn_confirm.setTextColor(getContext().getColor(R.color.text_secondary));
            }
        });
        btn_confirm.setOnClickListener(view -> {
            if (!cb_confirm_read.isChecked()) {
                return;
            }
            if (onConfirm != null) {
                onConfirm.run();
            }
            dismiss();
        });
        btn_read.setOnClickListener(view -> {
            if (onRead != null) {
                onRead.run();
            }
            dismiss();
        });
        setOnDismissListener(dialog -> {
            if (onDismiss != null) {
                onDismiss.run();
            }
        });
    }

    public PolicyGrantDialog title(String title) {
        this.title = title;
        return this;
    }

    public PolicyGrantDialog message(String message) {
        this.message = message;
        return this;
    }

    public PolicyGrantDialog onConfirm(String confirm) {
        this.confirm = confirm;
        return this;
    }

    public PolicyGrantDialog onConfirm(Runnable onConfirm) {
        this.onConfirm = onConfirm;
        return this;
    }

    public PolicyGrantDialog onConfirm(String confirm, Runnable onConfirm) {
        return onConfirm(confirm).onConfirm(onConfirm);
    }

    public PolicyGrantDialog onRead(String cancel) {
        this.cancel = cancel;
        return this;
    }

    public PolicyGrantDialog onRead(Runnable onRead) {
        this.onRead = onRead;
        return this;
    }

    public PolicyGrantDialog onRead(String cancel, Runnable onRead) {
        return onRead(cancel).onRead(onRead);
    }

    public PolicyGrantDialog onDismiss(Runnable onDismiss) {
        this.onDismiss = onDismiss;
        return this;
    }

}
