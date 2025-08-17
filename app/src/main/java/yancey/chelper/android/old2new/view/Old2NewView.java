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

package yancey.chelper.android.old2new.view;

import android.annotation.SuppressLint;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import yancey.chelper.R;
import yancey.chelper.android.common.util.ClipboardUtil;
import yancey.chelper.android.common.util.TextWatcherUtil;
import yancey.chelper.android.common.view.BaseView;
import yancey.chelper.core.CHelperCore;

/**
 * 旧命令转新命令界面
 */
@SuppressLint("ViewConstructor")
public class Old2NewView extends BaseView {

    public Old2NewView(@NonNull FWSContext fwsContext) {
        super(fwsContext, R.layout.layout_old2new);
        EditText mEd_oldCommand = view.findViewById(R.id.old_command);
        TextView mTv_newCommand = view.findViewById(R.id.new_command);
        findViewById(R.id.back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        view.findViewById(R.id.btn_clear_and_paste_old_command).setOnClickListener(v -> {
            CharSequence charSequence = ClipboardUtil.getText(context);
            if (charSequence != null) {
                mEd_oldCommand.setText(charSequence);
                mEd_oldCommand.setSelection(charSequence.length());
            }
        });
        view.findViewById(R.id.btn_copy_new_command).setOnClickListener(v ->
                ClipboardUtil.setText(getContext(), mTv_newCommand.getText()));
        mEd_oldCommand.addTextChangedListener(TextWatcherUtil.onTextChanged(charSequence ->
                mTv_newCommand.setText(CHelperCore.old2new(getContext(), mEd_oldCommand.getText().toString()))));
    }

    @Override
    protected String gePageName() {
        return "Old2New";
    }

}
