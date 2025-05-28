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

import com.hjq.toast.Toaster;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import yancey.chelper.R;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.util.ClipboardUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.network.ServiceManager;
import yancey.chelper.network.library.data.LibraryFunction;
import yancey.chelper.network.library.service.CommandLabPublicService;
import yancey.chelper.network.library.util.CommandLabUtil;

/**
 * 命令库编辑视图
 */
@SuppressLint("ViewConstructor")
public class LibraryEditView extends CustomView {

    private final EditText ed_name;
    private final EditText ed_version;
    private final EditText ed_author;
    private final EditText ed_description;
    private final EditText ed_tags;
    private final EditText ed_commands;
    private final boolean isLocal;
    private Disposable upload, update, delete;

    public interface OnEditListener {
        void onCreate(@NonNull LibraryFunction libraryFunction);

        void onUpdate(@Nullable Integer position, @NonNull LibraryFunction before, @NonNull LibraryFunction after);

        void onDelete(@Nullable Integer position, @NonNull LibraryFunction libraryFunction);
    }

    public LibraryEditView(
            @NonNull CustomContext customContext,
            @Nullable String authKey,
            @NonNull OnEditListener onEditListener,
            @Nullable Integer position,
            @Nullable LibraryFunction before,
            boolean isLocal
    ) {
        super(customContext, R.layout.layout_library_edit);
        this.isLocal = isLocal;
        view.findViewById(R.id.back).setOnClickListener(v -> backView());
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
        if (isLocal) {
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
        } else {
            if (before == null) {
                tv_title.setText(R.string.library_upload_with_need_review);
            } else {
                tv_title.setText(R.string.library_update_with_need_review);
                ed_name.setText(before.name);
                ed_version.setText(before.version);
                ed_author.setText(before.author);
                ed_description.setText(before.note);
                ed_tags.setText(before.tags == null ? "" : String.join(",", before.tags));
                ed_commands.setText(before.content);
            }
        }
        btn_preview.setOnClickListener(view1 -> {
            LibraryFunction after = getLibrary();
            if (after != null) {
                openView(customContext1 -> new LibraryShowView(customContext1, after, isLocal));
            }
        });
        if (isLocal) {
            btn_save.setOnClickListener(v -> {
                LibraryFunction after = getLibrary();
                if (after != null) {
                    if (before == null) {
                        onEditListener.onCreate(after);
                    } else {
                        onEditListener.onUpdate(position, before, after);
                    }
                    backView();
                }
            });
        } else {
            btn_save.setVisibility(View.GONE);
        }
        if (isLocal || before != null) {
            btn_upload.setVisibility(View.GONE);
        } else {
            btn_upload.setOnClickListener(view2 -> {
                LibraryFunction after = getLibrary();
                if (after == null) {
                    return;
                }
                new IsConfirmDialog(context, false)
                        .title(context.getString(R.string.library_upload))
                        .message("是否确认上传？上传后，您提交的命令将被送往审核，审核通过后才会出现在公有命令库中。如果没有特殊说明，您提交的命令将以CC BY-SA 4.0（署名-相同方式共享 4.0）协议授权给本命令库使用。")
                        .onConfirm(() -> {
                            String content = CommandLabUtil.libraryToStr(after);
                            if (content.length() > CommandLabUtil.getMaxLength()) {
                                Toaster.show("内容长度过长，请减少字数");
                                return;
                            }
                            if (upload != null) {
                                upload.dispose();
                            }
                            CommandLabPublicService.UploadFunctionRequest request = new CommandLabPublicService.UploadFunctionRequest();
                            request.content = content;
                            upload = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE
                                    .uploadFunction(request)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(result -> {
                                        if (!Objects.equals(result.status, "success")) {
                                            Toaster.show(result.message);
                                            return;
                                        }
                                        new IsConfirmDialog(context, false)
                                                .message("上传成功，您的密钥为：" + Objects.requireNonNull(Objects.requireNonNull(result.data).functions).get(0).user_key + "。本密钥只显示一次，用于后续更新或删除本次上传的内容，请妥善保存。")
                                                .onConfirm("复制密钥", () -> ClipboardUtil.setText(context, Objects.requireNonNull(Objects.requireNonNull(result.data).functions).get(0).user_key))
                                                .onDismiss(this::backView)
                                                .show();
                                        onEditListener.onCreate(after);
                                    }, throwable -> Toaster.show(throwable.getMessage()));
                        })
                        .show();
            });
        }
        if (isLocal || before == null) {
            btn_update.setVisibility(View.GONE);
        } else {
            btn_update.setOnClickListener(view2 -> {
                LibraryFunction after = getLibrary();
                if (after == null) {
                    return;
                }
                new IsConfirmDialog(context, false)
                        .title(context.getString(R.string.library_update))
                        .message("是否确认更新？更新后，您提交的命令将被送往审核，审核通过后才会出现在公有命令库中。如果没有特殊说明，您提交的命令将以CC BY-SA 4.0（署名-相同方式共享 4.0）协议授权给本命令库使用。")
                        .onConfirm(() -> {
                            String content = CommandLabUtil.libraryToStr(after);
                            if (content.length() > CommandLabUtil.getMaxLength()) {
                                Toaster.show("内容长度过长，请减少字数");
                                return;
                            }
                            if (update != null) {
                                update.dispose();
                            }
                            CommandLabPublicService.UpdateFunctionRequest request = new CommandLabPublicService.UpdateFunctionRequest();
                            request.auth_key = authKey;
                            request.content = content;
                            update = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE
                                    .updateFunction(Objects.requireNonNull(Objects.requireNonNull(before).id), request)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(result -> {
                                        if (!Objects.equals(result.status, "success")) {
                                            Toaster.show(result.message);
                                            return;
                                        }
                                        Toaster.show("更改成功");
                                        onEditListener.onUpdate(position, before, after);
                                        backView();
                                    }, throwable -> Toaster.show(throwable.getMessage()));
                        }).show();
            });
        }
        if (before == null) {
            btn_delete.setVisibility(View.GONE);
        } else {
            btn_delete.setOnClickListener(view2 -> new IsConfirmDialog(context, false)
                    .title(context.getString(R.string.library_delete))
                    .message("删除后将无法找回，是否确认删除？")
                    .onConfirm(() -> {
                        if (isLocal) {
                            onEditListener.onDelete(position, before);
                            backView();
                        } else {
                            if (delete != null) {
                                delete.dispose();
                            }
                            CommandLabPublicService.DeleteFunctionRequest request = new CommandLabPublicService.DeleteFunctionRequest();
                            request.auth_key = authKey;
                            delete = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE
                                    .deleteFunction(Objects.requireNonNull(Objects.requireNonNull(before).id), request)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(result -> {
                                        if (!Objects.equals(result.status, "success")) {
                                            Toaster.show(result.message);
                                            return;
                                        }
                                        Toaster.show("删除成功");
                                        onEditListener.onDelete(position, before);
                                        backView();
                                    }, throwable -> Toaster.show(throwable.getMessage()));
                        }
                    }).show());
        }
    }

    @Override
    protected String gePageName() {
        return isLocal ? "LocalLibraryEdit" : "PublicLibraryEdit";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (upload != null) {
            upload.dispose();
        }
        if (update != null) {
            update.dispose();
        }
        if (delete != null) {
            delete.dispose();
        }
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
