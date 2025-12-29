/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
 * Copyright (C) 2025  Yancey
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package yancey.chelper.android.library.view

import android.annotation.SuppressLint
import android.view.View
import android.widget.EditText
import android.widget.TextView
import yancey.chelper.R
import yancey.chelper.android.common.dialog.IsConfirmDialog
import yancey.chelper.android.common.view.BaseView
import yancey.chelper.android.library.util.OnEditListener
import yancey.chelper.network.library.data.LibraryFunction
import java.lang.String
import java.util.Arrays
import java.util.stream.Collectors
import kotlin.Int

/**
 * 命令库编辑视图
 */
@SuppressLint("ViewConstructor")
class LocalLibraryEditView(
    fwsContext: FWSContext,
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

    init {
        view.findViewById<View>(R.id.back)
            .setOnClickListener({ v -> onBackPressedDispatcher.onBackPressed() })
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
        btn_preview.setOnClickListener({
            val after = this.library
            if (after != null) {
//                openView({ context: FWSContext? ->
//                    LocalLibraryShowView(
//                        context!!,
//                        after
//                    )
//                })
            }
        })
        if (before == null) {
            tv_title.setText(R.string.layout_library_edit_title_add)
        } else {
            tv_title.setText(R.string.layout_library_edit_title_edit)
            name.setText(before.name)
            version.setText(before.version)
            author.setText(before.author)
            description.setText(before.note)
            tags.setText(if (before.tags == null) "" else String.join(",", before.tags!!))
            commands.setText(before.content)
        }
        btn_save.setOnClickListener({
            val after = this.library
            if (after != null) {
                if (before == null) {
                    onEditListener.onCreate(after)
                } else {
                    onEditListener.onUpdate(position, before, after)
                }
                getOnBackPressedDispatcher().onBackPressed()
            }
        })
        btn_upload.setVisibility(GONE)
        btn_update.setVisibility(GONE)
        if (before == null) {
            btn_delete.setVisibility(GONE)
        } else {
            btn_delete.setOnClickListener({
                IsConfirmDialog(context, false)
                    .title(context.getString(R.string.layout_library_edit_delete))
                    .message("删除后将无法找回，是否确认删除？")
                    .onConfirm({
                        onEditListener.onDelete(position, before)
                        onBackPressedDispatcher.onBackPressed()
                    }).show()
            })
        }
    }

    override fun gePageName(): kotlin.String {
        return "LocalLibraryEdit"
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
            library.tags = Arrays.stream<kotlin.String?>(
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
