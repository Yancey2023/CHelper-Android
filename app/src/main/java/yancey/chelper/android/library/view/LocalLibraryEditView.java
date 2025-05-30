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

package yancey.chelper.android.library.view;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.stream.Collectors;

import yancey.chelper.R;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.view.BaseView;
import yancey.chelper.android.library.util.OnEditListener;
import yancey.chelper.network.library.data.LibraryFunction;

/**
 * 命令库编辑视图
 */
@SuppressLint("ViewConstructor")
public class LocalLibraryEditView extends BaseView {

    private final EditText ed_name;
    private final EditText ed_version;
    private final EditText ed_author;
    private final EditText ed_description;
    private final EditText ed_tags;
    private final EditText ed_commands;

    public LocalLibraryEditView(
            @NonNull CustomContext customContext,
            @NonNull OnEditListener onEditListener,
            @Nullable Integer position,
            @Nullable LibraryFunction before
    ) {
        super(customContext, R.layout.layout_library_edit);
        view.findViewById(R.id.back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        TextView tv_title = view.findViewById(R.id.title);
        ed_name = view.findViewById(R.id.name);
        ed_version = view.findViewById(R.id.version);
        ed_author = view.findViewById(R.id.author);
        ed_description = view.findViewById(R.id.description);
        ed_tags = view.findViewById(R.id.tags);
        ed_commands = view.findViewById(R.id.commands);
        TextView btn_preview = view.findViewById(R.id.btn_preview);
        TextView btn_save = view.findViewById(R.id.btn_save);
        TextView btn_upload = view.findViewById(R.id.btn_upload);
        TextView btn_update = view.findViewById(R.id.btn_update);
        TextView btn_delete = view.findViewById(R.id.btn_delete);
        btn_preview.setOnClickListener(view1 -> {
            LibraryFunction after = getLibrary();
            if (after != null) {
                openView(customContext1 -> new PreviewLibraryShowView(customContext1, after));
            }
        });
        if (before == null) {
            tv_title.setText(R.string.library_add);
        } else {
            tv_title.setText(R.string.library_edit);
            ed_name.setText(before.name);
            ed_version.setText(before.version);
            ed_author.setText(before.author);
            ed_description.setText(before.note);
            ed_tags.setText(before.tags == null ? "" : String.join(",", before.tags));
            ed_commands.setText(before.content);
        }
        btn_save.setOnClickListener(v -> {
            LibraryFunction after = getLibrary();
            if (after != null) {
                if (before == null) {
                    onEditListener.onCreate(after);
                } else {
                    onEditListener.onUpdate(position, before, after);
                }
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        btn_upload.setVisibility(View.GONE);
        btn_update.setVisibility(View.GONE);
        if (before == null) {
            btn_delete.setVisibility(View.GONE);
        } else {
            btn_delete.setOnClickListener(view2 -> new IsConfirmDialog(context, false)
                    .title(context.getString(R.string.library_delete))
                    .message("删除后将无法找回，是否确认删除？")
                    .onConfirm(() -> {
                        onEditListener.onDelete(position, before);
                        getOnBackPressedDispatcher().onBackPressed();
                    }).show());
        }
    }

    @Override
    protected String gePageName() {
        return "LocalLibraryEdit";
    }

    @Nullable
    LibraryFunction getLibrary() {
        LibraryFunction library = new LibraryFunction();
        library.name = ed_name.getText().toString();
        if (library.name.isEmpty()) {
            new IsConfirmDialog(getContext(), false).message("名字未填写").show();
            return null;
        }
        library.version = ed_version.getText().toString();
        if (library.version.isEmpty()) {
            new IsConfirmDialog(getContext(), false).message("版本未填写").show();
            return null;
        }
        library.author = ed_author.getText().toString();
        if (library.author.isEmpty()) {
            new IsConfirmDialog(getContext(), false).message("作者未填写").show();
            return null;
        }
        library.note = ed_description.getText().toString();
        if (library.note.isEmpty()) {
            new IsConfirmDialog(getContext(), false).message("介绍未填写").show();
            return null;
        }
        String tags = ed_tags.getText().toString();
        if (tags.isEmpty()) {
            new IsConfirmDialog(getContext(), false).message("标签未填写").show();
            return null;
        }
        library.tags = Arrays.stream(tags.split(",")).collect(Collectors.toList());
        library.content = ed_commands.getText().toString();
        if (library.content.isEmpty()) {
            new IsConfirmDialog(getContext(), false).message("命令未填写").show();
            return null;
        }
        return library;
    }

}
