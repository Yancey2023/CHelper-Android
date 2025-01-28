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

package yancey.chelper.android.old2new.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Consumer;

import yancey.chelper.R;
import yancey.chelper.android.common.util.ClipboardUtil;
import yancey.chelper.android.common.util.TextWatcherUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.core.CHelperCore;

/**
 * 旧命令转新命令界面
 */
@SuppressLint("ViewConstructor")
public class Old2NewView extends CustomView {

    private EditText mEd_oldCommand;
    private TextView mTv_newCommand;

    public Old2NewView(@NonNull Context context, @NonNull Consumer<CustomView> openView, @NonNull Environment environment) {
        super(context, openView, environment, R.layout.layout_old2new);
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        mEd_oldCommand = view.findViewById(R.id.ed_old_command);
        mTv_newCommand = view.findViewById(R.id.tv_new_command);
        view.findViewById(R.id.btn_paste).setOnClickListener(v -> {
            CharSequence charSequence = ClipboardUtil.getText(context);
            if (charSequence != null) {
                mEd_oldCommand.setText(charSequence);
                mEd_oldCommand.setSelection(charSequence.length());
            }
        });
        view.findViewById(R.id.btn_copy).setOnClickListener(v ->
                ClipboardUtil.setText(getContext(), mTv_newCommand.getText()));
        mEd_oldCommand.addTextChangedListener(TextWatcherUtil.onTextChanged(charSequence ->
                mTv_newCommand.setText(CHelperCore.old2new(getContext(), mEd_oldCommand.getText().toString()))));
    }

}
