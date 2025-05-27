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

package yancey.chelper.android.favorites.dialog;

import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.common.dialog.FixedDialog;
import yancey.chelper.android.favorites.adapter.FavoriteListAdapter;
import yancey.chelper.android.favorites.data.DataFavorite;

/**
 * 修改收藏命令的对话框
 */
public class EditFavoriteDialog extends FixedDialog {

    private final DataFavorite dataFavorite;
    private final int position;
    private final FavoriteListAdapter adapter;
    private final boolean isCreating;
    private EditText mEd_command, mEd_description;

    public EditFavoriteDialog(@NonNull Context context, FavoriteListAdapter adapter, DataFavorite dataFavorite, int position, boolean isCreating) {
        super(context);
        this.adapter = adapter;
        this.dataFavorite = dataFavorite;
        this.position = position;
        this.isCreating = isCreating;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_favorite);
        mEd_command = findViewById(R.id.ed_command);
        mEd_description = findViewById(R.id.ed_description);
        TextView tv_title = findViewById(R.id.tv_title);
        TextView tv_content = findViewById(R.id.tv_content);
        TextView btn_confirm = findViewById(R.id.btn_confirm);
        TextView btn_cancel = findViewById(R.id.btn_cancel);
        Objects.requireNonNull(tv_title);
        Objects.requireNonNull(tv_content);
        Objects.requireNonNull(btn_confirm);
        Objects.requireNonNull(btn_cancel);
        if (isCreating) {
            tv_title.setText(R.string.tv_create);
        } else {
            tv_title.setText(R.string.tv_edit);
            mEd_command.setText(dataFavorite.title);
            mEd_description.setText(dataFavorite.description);
        }
        tv_content.setText(dataFavorite.dataFavoriteList == null ? R.string.tv_command_name : R.string.tv_filename);
        btn_confirm.setOnClickListener(v -> {
            dataFavorite.title = mEd_command.getText().toString();
            dataFavorite.description = mEd_description.getText().toString();
            if (isCreating) {
                adapter.add(dataFavorite);
            } else {
                adapter.notifyItemChanged(position);
            }
            dismiss();
        });
        btn_cancel.setOnClickListener(v -> dismiss());
    }
}
