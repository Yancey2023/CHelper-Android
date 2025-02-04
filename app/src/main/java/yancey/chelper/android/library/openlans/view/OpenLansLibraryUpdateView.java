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
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.network.api.library.openlans.CommandLabAPI;
import yancey.chelper.network.data.openlans.LibraryFunction;

/**
 * OpenLANS的命令库
 */
@SuppressLint("ViewConstructor")
public class OpenLansLibraryUpdateView extends CustomView {

    private EditText ed_name;
    private EditText ed_version;
    private EditText ed_author;
    private EditText ed_description;
    private EditText ed_tags;
    private EditText ed_commands;
    //    private List<RawJsonFunction> rawJsonFunctions;
    private Handler handler;
    private final String auth_key;
    private final Runnable setDirty;

    public OpenLansLibraryUpdateView(
            @NonNull Context context,
            @NonNull Consumer<CustomView> openView,
            @NonNull Supplier<Boolean> backView,
            @NonNull Environment environment,
            @NonNull String auth_key,
            @NonNull Runnable setDirty,
            @NonNull LibraryFunction libraryFunction
    ) {
        super(context, openView, backView, environment, R.layout.layout_openlans_library_update, libraryFunction);
        this.auth_key = auth_key;
        this.setDirty = setDirty;
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        LibraryFunction libraryFunction = (LibraryFunction) Objects.requireNonNull(privateData);
        handler = new Handler(Looper.getMainLooper());
        ed_name = view.findViewById(R.id.name);
        ed_name.setText(libraryFunction.name);
        ed_version = view.findViewById(R.id.version);
        ed_version.setText(libraryFunction.version);
        ed_author = view.findViewById(R.id.author);
        ed_author.setText(libraryFunction.author);
        ed_description = view.findViewById(R.id.description);
        ed_description.setText(libraryFunction.note);
        ed_tags = view.findViewById(R.id.tags);
        ed_tags.setText(libraryFunction.tags == null ? "" : String.join(",", libraryFunction.tags));
        ed_commands = view.findViewById(R.id.commands);
        ed_commands.setText(libraryFunction.content);
        view.findViewById(R.id.btn_preview).setOnClickListener(view1 -> {
            LibraryFunction library = getLibrary();
            if (library != null) {
                openView((context1, openView, backView, environment) ->
                        new OpenLansLibraryPreviewView(context1, openView, backView, environment, library));
            }
        });
        view.findViewById(R.id.btn_update).setOnClickListener(view2 -> {
            LibraryFunction library = getLibrary();
            if (library != null) {
                library.id = libraryFunction.id;
                new IsConfirmDialog(context, false)
                        .title("更新")
                        .message("是否确认更新？更新后，您提交的命令将被送往审核，审核通过后才会出现在公有命令库中。如果没有特殊说明，您提交的命令将以CC BY-SA 4.0（署名-相同方式共享 4.0）协议授权给本命令库使用。")
                        .onConfirm(() -> CommandLabAPI.update(library, auth_key, result -> handler.post(() -> {
                            if (result.first != null) {
                                ToastUtil.show(context, result.first);
                            }
                            if (result.second != null) {
                                if (Objects.equals(result.second.status, "success")) {
                                    if (result.second.data != null && result.second.data.id != null) {
                                        ToastUtil.show(context, "更改成功");
                                        setDirty.run();
                                        backView.get();
                                    }
                                } else {
                                    ToastUtil.show(context, result.second.message);
                                }
                            }
                        })))
                        .show();
            }
        });
        view.findViewById(R.id.btn_delete).setOnClickListener(view2 -> {
            LibraryFunction library = getLibrary();
            if (library != null) {
                library.id = libraryFunction.id;
                if (library.id != null) {
                    new IsConfirmDialog(context, false)
                            .title("删除")
                            .message("删除后将无法找回，是否确认删除？")
                            .onConfirm(() -> {
                                CommandLabAPI.deleteFunction(library.id, auth_key, result -> handler.post(() -> ToastUtil.show(context, result)));
                                backView.get();
                            })
                            .show();
                }
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
//        if (rawJsonFunctions != null && rawJsonFunctions.stream().anyMatch(rawJsonFunction ->
//                Objects.equals(rawJsonFunction.name, library.name))) {
//            new IsConfirmDialog(getContext(), false).message("名字与已有命令库重复").show();
//            return null;
//        }
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
