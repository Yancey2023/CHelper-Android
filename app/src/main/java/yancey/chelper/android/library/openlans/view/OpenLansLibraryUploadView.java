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

package yancey.chelper.android.library.openlans.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import yancey.chelper.R;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.util.ClipboardUtil;
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.network.api.library.openlans.CommandLabAPI;
import yancey.chelper.network.data.openlans.LibraryFunction;

/**
 * OpenLANS的命令库
 */
@SuppressLint("ViewConstructor")
public class OpenLansLibraryUploadView extends CustomView {

    private EditText ed_name;
    private EditText ed_version;
    private EditText ed_author;
    private EditText ed_description;
    private EditText ed_tags;
    private EditText ed_commands;
    private Handler handler;
    private final Runnable setDirty;

    public OpenLansLibraryUploadView(
            @NonNull Context context,
            @NonNull Consumer<CustomView> openView,
            @NonNull Supplier<Boolean> backView,
            @NonNull Environment environment,
            @NonNull Runnable setDirty
    ) {
        super(context, openView, backView, environment, R.layout.layout_openlans_library_upload);
        this.setDirty = setDirty;
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        handler = new Handler(Looper.getMainLooper());
        ed_name = view.findViewById(R.id.name);
        ed_version = view.findViewById(R.id.version);
        ed_author = view.findViewById(R.id.author);
        ed_description = view.findViewById(R.id.description);
        ed_tags = view.findViewById(R.id.tags);
        ed_commands = view.findViewById(R.id.commands);
        view.findViewById(R.id.btn_preview).setOnClickListener(view1 -> {
            LibraryFunction library = getLibrary();
            if (library != null) {
                openView((context1, openView, backView, environment) ->
                        new OpenLansLibraryPreviewView(context1, openView, backView, environment, library));
            }
        });
        view.findViewById(R.id.btn_upload).setOnClickListener(view2 -> {
            LibraryFunction library = getLibrary();
            if (library != null) {
                new IsConfirmDialog(context, false)
                        .title("上传")
                        .message("是否确认上传？上传后，您提交的命令将被送往审核，审核通过后才会出现在公有命令库中。如果没有特殊说明，您提交的命令将以CC BY-SA 4.0（署名-相同方式共享 4.0）协议授权给本命令库使用。")
                        .onConfirm(() -> CommandLabAPI.upload(library, result -> handler.post(() -> {
                            if (result.first != null) {
                                ToastUtil.show(context, result.first);
                            }
                            if (result.second != null) {
                                if (Objects.equals(result.second.status, "success")) {
                                    if (result.second.data != null && result.second.data.functions != null && result.second.data.functions.size() == 1) {
                                        setDirty.run();
                                        new IsConfirmDialog(context, false)
                                                .message("上传成功，您的密钥为：" + result.second.data.functions.get(0).user_key + "。本密钥只显示一次，用于后续更新或删除本次上传的内容，请妥善保存。")
                                                .onConfirm("复制密钥", () -> ClipboardUtil.setText(context, result.second.data.functions.get(0).user_key))
                                                .onDismiss(backView::get)
                                                .show();
                                    }
                                } else {
                                    ToastUtil.show(context, result.second.message);
                                }
                            }
                        })))
                        .show();
            }
        });
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
