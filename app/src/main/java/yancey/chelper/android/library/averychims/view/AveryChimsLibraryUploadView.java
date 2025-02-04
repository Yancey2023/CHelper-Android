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

package yancey.chelper.android.library.averychims.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import yancey.chelper.R;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.util.MyInputFilter;
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.network.api.library.averychims.CommandLibraryAPI;
import yancey.chelper.network.data.averychims.Library;

/**
 * Avery Chims的命令库
 */
@SuppressLint("ViewConstructor")
public class AveryChimsLibraryUploadView extends CustomView {

    private EditText ed_name;
    private EditText ed_author;
    private EditText ed_description;
    private EditText ed_commands;
    private List<String> libraryNames;
    private Handler handler;

    public AveryChimsLibraryUploadView(
            @NonNull Context context,
            @NonNull Consumer<CustomView> openView,
            @NonNull Supplier<Boolean> backView,
            @NonNull Environment environment,
            @Nullable List<String> libraryNames
    ) {
        super(context, openView, backView, environment, R.layout.layout_averychims_library_upload);
        this.libraryNames = libraryNames;
        if (this.libraryNames == null) {
            CommandLibraryAPI.getAllLibraryNames(result -> handler.post(() -> this.libraryNames = result));
        }
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        handler = new Handler(Looper.getMainLooper());
        ed_name = view.findViewById(R.id.name);
        ed_author = view.findViewById(R.id.author);
        ed_description = view.findViewById(R.id.description);
        ed_commands = view.findViewById(R.id.commands);
        ed_name.setFilters(new InputFilter[]{new MyInputFilter("+#∅")});
        ed_author.setFilters(new InputFilter[]{new MyInputFilter("+#∅")});
        ed_description.setFilters(new InputFilter[]{new MyInputFilter("+#∅")});
        ed_commands.setFilters(new InputFilter[]{new MyInputFilter("+∅")});
        view.findViewById(R.id.btn_preview).setOnClickListener(view1 -> {
            Library library = getLibrary();
            if (library != null) {
                openView((context1, openView, backView, environment) ->
                        new AveryChimsLibraryPreviewView(context1, openView, backView, environment, library));
            }
        });
        view.findViewById(R.id.btn_upload).setOnClickListener(view2 -> {
            Library library = getLibrary();
            if (library != null) {
                new IsConfirmDialog(context, false)
                        .title("上传")
                        .message("是否确认上传？上传后，您提交的命令将被送往审核，审核通过后才会出现在公有命令库中。如果没有特殊说明，您提交的命令将以CC BY-SA 4.0（署名-相同方式共享 4.0）协议授权给本命令库使用。")
                        .onConfirm(() -> CommandLibraryAPI.upload(library, s -> handler.post(() -> ToastUtil.show(context, s))))
                        .show();
            }
        });
    }

    @Nullable
    Library getLibrary() {
        Library library = new Library();
        library.name = ed_name.getText().toString();
        if (library.name.isEmpty()) {
            new IsConfirmDialog(getContext(), false).message("名字未填写").show();
            return null;
        }
        if (library.name.contains("---")) {
            new IsConfirmDialog(getContext(), false).message("名字不能包含---").show();
            return null;
        }
        if (libraryNames != null && libraryNames.contains(library.name)) {
            new IsConfirmDialog(getContext(), false).message("名字与已有命令库重复").show();
            return null;
        }
        library.author = ed_author.getText().toString();
        if (library.author.isEmpty()) {
            new IsConfirmDialog(getContext(), false).message("作者未填写").show();
            return null;
        }
        if (library.author.contains("---")) {
            new IsConfirmDialog(getContext(), false).message("名字不能包含---").show();
            return null;
        }
        library.description = ed_description.getText().toString();
        if (library.description.isEmpty()) {
            new IsConfirmDialog(getContext(), false).message("介绍未填写").show();
            return null;
        }
        if (library.description.contains("---")) {
            new IsConfirmDialog(getContext(), false).message("名字不能包含---").show();
            return null;
        }
        String commands = ed_commands.getText().toString();
        if (commands.isEmpty()) {
            new IsConfirmDialog(getContext(), false).message("命令未填写").show();
            return null;
        }
        library.commands = Arrays.stream(commands.split("\n"))
                .filter(s -> !s.isEmpty())
                .map(s -> {
                    Library.Command command = new Library.Command();
                    int index = s.lastIndexOf('#');
                    if (index == -1) {
                        command.content = s;
                    } else {
                        command.content = s.substring(0, index).strip();
                        command.description = s.substring(index + 1).strip();
                    }
                    return command;
                }).collect(Collectors.toList());
        for (Library.Command command : library.commands) {
            //noinspection DataFlowIssue
            if (command.content.contains("---")) {
                new IsConfirmDialog(getContext(), false).message("---不能出现再在命令中：" + command.content).show();
                return null;
            }
            if (command.description != null) {
                if (command.description.contains("---")) {
                    new IsConfirmDialog(getContext(), false).message("---不能出现再在命令介绍中：" + command.description).show();
                    return null;
                }
                if (command.content.indexOf('#') != -1) {
                    new IsConfirmDialog(getContext(), false).message("#字符不能出现在命令中：" + command.content).show();
                    return null;
                }
            }
        }
        return library;
    }

}
