/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
 * Copyright (C) 2025  Yancey
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https:></https:>//www.gnu.org/licenses/>.
 */

package yancey.chelper.android.library.view

import android.annotation.SuppressLint
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.hjq.toast.Toaster
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import yancey.chelper.R
import yancey.chelper.android.common.dialog.IsConfirmDialog
import yancey.chelper.android.common.util.ClipboardUtil
import yancey.chelper.android.common.view.BaseView
import yancey.chelper.android.library.util.OnEditListener
import yancey.chelper.fws.view.FWSView
import yancey.chelper.network.ServiceManager
import yancey.chelper.network.library.data.LibraryFunction
import yancey.chelper.network.library.service.CommandLabPublicService
import yancey.chelper.network.library.util.libraryContentMaxLength
import yancey.chelper.network.library.util.libraryToStr
import java.util.Arrays
import java.util.Objects
import java.util.stream.Collectors

/**
 * 命令库编辑视图
 */
@SuppressLint("ViewConstructor")
class PublicLibraryEditView(
    fwsContext: FWSView.FWSContext,
    authKey: String?,
    onEditListener: OnEditListener,
    position: Int?,
    before: LibraryFunction?
) : BaseView(fwsContext, R.layout.layout_library_edit) {
    private val name: EditText
    private val version: EditText
    private val author: EditText
    private val description: EditText
    private val tags: EditText
    private val commands: EditText
    private var upload: Disposable? = null
    private var update: Disposable? = null
    private var delete: Disposable? = null

    init {
        view.findViewById<View>(R.id.back)
            .setOnClickListener({ onBackPressedDispatcher.onBackPressed() })
        val tv_title = view.findViewById<TextView>(R.id.title)
        name = view.findViewById<EditText>(R.id.name)
        version = view.findViewById<EditText>(R.id.version)
        author = view.findViewById<EditText>(R.id.author)
        description = view.findViewById<EditText>(R.id.description)
        tags = view.findViewById<EditText>(R.id.tags)
        commands = view.findViewById<EditText>(R.id.commands)
        val btn_preview = view.findViewById<TextView>(R.id.btn_preview)
        val btn_save = view.findViewById<TextView>(R.id.btn_save)
        val btn_upload = view.findViewById<TextView>(R.id.btn_upload)
        val btn_update = view.findViewById<TextView>(R.id.btn_update)
        val btn_delete = view.findViewById<TextView>(R.id.btn_delete)
        btn_preview.setOnClickListener {
//            val after: LibraryFunction? = this.library
//            if (after != null) {
//                openView({ context -> PublicLibraryShowView(context, after) })
//            }
        }
        if (before == null) {
            tv_title.setText(R.string.layout_library_edit_title_upload_with_need_review)
        } else {
            tv_title.setText(R.string.layout_library_edit_title_update_with_need_review)
            name.setText(before.name)
            version.setText(before.version)
            author.setText(before.author)
            description.setText(before.note)
            tags.setText(if (before.tags == null) "" else before.tags!!.joinToString("\n"))
            commands.setText(before.content)
        }
        btn_save.visibility = GONE
        if (before != null) {
            btn_upload.visibility = GONE
        } else {
            btn_upload.setOnClickListener({
                val after: LibraryFunction? = this.library
                if (after == null) {
                    return@setOnClickListener
                }
                IsConfirmDialog(context, false)
                    .title(context.getString(R.string.layout_library_edit_upload))
                    .message("是否确认上传？上传后，您提交的命令将被送往审核，审核通过后才会出现在公有命令库中。如果没有特殊说明，您提交的命令将以CC BY-SA 4.0（署名-相同方式共享 4.0）协议授权给本命令库使用。")
                    .onConfirm({
                        val content: kotlin.String = libraryToStr(after)
                        if (content.length > libraryContentMaxLength) {
                            Toaster.show("内容长度过长，请减少字数")
                            return@onConfirm
                        }
                        upload?.dispose()
                        val request: CommandLabPublicService.UploadFunctionRequest =
                            CommandLabPublicService.UploadFunctionRequest()
                        request.content = content
//                        upload = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE!!
//                            .uploadFunction(request)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe({ result ->
//                                if (result.status != "success") {
//                                    Toaster.show(result.message)
//                                    return@subscribe
//                                }
//                                IsConfirmDialog(context, false)
//                                    .message(
//                                        "上传成功，您的密钥为：" + Objects.requireNonNull<Any?>(
//                                            Objects.requireNonNull<Any?>(result.data).functions
//                                        )
//                                            .get(0).user_key + "。本密钥只显示一次，用于后续更新或删除本次上传的内容，请妥善保存。"
//                                    )
//                                    .onConfirm("复制密钥") {
//                                        ClipboardUtil.setText(
//                                            context, Objects.requireNonNull<Any?>(
//                                                Objects.requireNonNull<Any?>(result.data).functions
//                                            ).get(0).user_key
//                                        )
//                                    }
//                                    .onDismiss({ onBackPressedDispatcher.onBackPressed() })
//                                    .show()
//                                onEditListener.onCreate(after)
//                            }, { throwable -> Toaster.show(throwable.getMessage()) })
                    })
                    .show()
            })
        }
        if (before == null) {
            btn_update.visibility = GONE
        } else {
            btn_update.setOnClickListener({
                val after: LibraryFunction? = this.library
                if (after == null) {
                    return@setOnClickListener
                }
                IsConfirmDialog(context, false)
                    .title(context.getString(R.string.layout_library_edit_update))
                    .message("是否确认更新？更新后，您提交的命令将被送往审核，审核通过后才会出现在公有命令库中。如果没有特殊说明，您提交的命令将以CC BY-SA 4.0（署名-相同方式共享 4.0）协议授权给本命令库使用。")
                    .onConfirm({
                        val content: kotlin.String = libraryToStr(after)
                        if (content.length > libraryContentMaxLength) {
                            Toaster.show("内容长度过长，请减少字数")
                            return@onConfirm
                        }
                        update?.dispose()
                        val request: CommandLabPublicService.UpdateFunctionRequest =
                            CommandLabPublicService.UpdateFunctionRequest()
                        request.auth_key = authKey
                        request.content = content
//                        update = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE!!
//                            .updateFunction(before.id!!, request)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe({ result ->
//                                if (result.status != "success") {
//                                    Toaster.show(result.message)
//                                    return@subscribe
//                                }
//                                Toaster.show("更改成功")
//                                onEditListener.onUpdate(position, before, after)
//                                getOnBackPressedDispatcher().onBackPressed()
//                            }, { throwable -> Toaster.show(throwable.getMessage()) })
                    }).show()
            })
        }
        if (before == null) {
            btn_delete.visibility = GONE
        } else {
            btn_delete.setOnClickListener {
                IsConfirmDialog(context, false)
                    .title(context.getString(R.string.layout_library_edit_delete))
                    .message("删除后将无法找回，是否确认删除？")
                    .onConfirm {
                        delete?.dispose()
                        val request: CommandLabPublicService.DeleteFunctionRequest =
                            CommandLabPublicService.DeleteFunctionRequest()
                        request.auth_key = authKey
//                        delete = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE!!
//                            .deleteFunction(before.id!!, request)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe({ result ->
//                                if (result.status != "success") {
//                                    Toaster.show(result.message)
//                                    return@subscribe
//                                }
//                                Toaster.show("删除成功")
//                                onEditListener.onDelete(position, before)
//                                getOnBackPressedDispatcher().onBackPressed()
//                            }, { throwable -> Toaster.show(throwable.getMessage()) })
                    }.show()
            }
        }
    }

    override fun gePageName(): String {
        return "PublicLibraryEdit"
    }

    override fun onDestroy() {
        super.onDestroy()
        upload?.dispose()
        update?.dispose()
        delete?.dispose()
    }

    val library: LibraryFunction?
        get() {
            val library = LibraryFunction()
            library.name = name.getText().toString()
            if (library.name!!.isEmpty()) {
                IsConfirmDialog(getContext(), false).message("名字未填写").show()
                return null
            }
            library.version = version.getText().toString()
            if (library.version!!.isEmpty()) {
                IsConfirmDialog(getContext(), false).message("版本未填写").show()
                return null
            }
            library.author = author.getText().toString()
            if (library.author!!.isEmpty()) {
                IsConfirmDialog(getContext(), false).message("作者未填写").show()
                return null
            }
            library.note = description.getText().toString()
            if (library.note!!.isEmpty()) {
                IsConfirmDialog(getContext(), false).message("介绍未填写").show()
                return null
            }
            val rawTags = tags.getText().toString()
            if (rawTags.isEmpty()) {
                IsConfirmDialog(getContext(), false).message("标签未填写").show()
                return null
            }
            library.tags = Arrays.stream(
                rawTags.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            )
                .collect(Collectors.toList())
            library.content = commands.getText().toString()
            if (library.content!!.isEmpty()) {
                IsConfirmDialog(getContext(), false).message("命令未填写").show()
                return null
            }
            return library
        }
}
