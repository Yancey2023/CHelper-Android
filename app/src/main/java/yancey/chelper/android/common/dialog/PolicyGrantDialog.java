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
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.common.util.PolicyGrantManager;
import yancey.chelper.android.showtext.activity.ShowTextActivity;

/**
 * 隐私政策授权对话框
 */
public class PolicyGrantDialog extends FixedDialog {

    @Nullable
    private PolicyGrantManager.State state;
    @Nullable
    private Runnable onConfirm;

    public PolicyGrantDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_policy_grant);
        setCancelable(false);
        TextView tv_message = findViewById(R.id.message);
        TextView btn_confirm = findViewById(R.id.confirm);
        TextView btn_read = findViewById(R.id.read);
        CheckBox cb_confirm_read = findViewById(R.id.confirm_read);
        Objects.requireNonNull(tv_message);
        Objects.requireNonNull(btn_confirm);
        Objects.requireNonNull(btn_read);
        Objects.requireNonNull(cb_confirm_read);
        tv_message.setText(state != PolicyGrantManager.State.UPDATED ? R.string.dialog_policy_grant_message_if_unread : R.string.dialog_policy_grant_message_if_updated);
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
            btn_confirm.setClickable(false);
            if (onConfirm != null) {
                onConfirm.run();
            }
            dismiss();
        });
        btn_read.setOnClickListener(view -> {
            btn_read.setClickable(false);
            Context context = getContext();
            Intent intent = new Intent(context, ShowTextActivity.class);
            intent.putExtra(ShowTextActivity.TITLE, context.getString(R.string.layout_about_privacy_policy));
            intent.putExtra(ShowTextActivity.CONTENT, PolicyGrantManager.INSTANCE.getPrivatePolicy());
            context.startActivity(intent);
            PolicyGrantManager.INSTANCE.agree();
            dismiss();
        });
    }

    public PolicyGrantDialog state(PolicyGrantManager.State state) {
        this.state = state;
        return this;
    }

    public PolicyGrantDialog onConfirm(Runnable onConfirm) {
        this.onConfirm = onConfirm;
        return this;
    }

}
