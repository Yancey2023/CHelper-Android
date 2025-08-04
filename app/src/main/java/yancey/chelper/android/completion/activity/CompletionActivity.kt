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

package yancey.chelper.android.completion.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.text.TextRange
import com.hjq.toast.Toaster
import yancey.chelper.android.common.activity.BaseComposeActivity
import yancey.chelper.android.common.util.FileUtil
import yancey.chelper.android.common.util.MonitorUtil
import yancey.chelper.android.common.util.Settings
import yancey.chelper.core.CHelperCore
import yancey.chelper.core.CHelperGuiCore
import yancey.chelper.core.CommandGuiCoreInterface
import yancey.chelper.core.ErrorReason
import yancey.chelper.core.SelectedString
import yancey.chelper.ui.completion.CompletionScreen
import yancey.chelper.ui.completion.CompletionViewModel
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.min

/**
 * 补全提示界面
 */
class CompletionActivity : BaseComposeActivity() {

    override val pageName = "Completion"

    private val viewModel: CompletionViewModel by viewModels()
    private val core = CHelperGuiCore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 加载上次的输入内容
        if (Settings.INSTANCE.isSavingWhenPausing) {
            val file: File =
                FileUtil.getFile(filesDir.absolutePath, "cache", "lastInput.dat")
            if (file.exists()) {
                try {
                    DataInputStream(BufferedInputStream(FileInputStream(file))).use { dataInputStream ->
                        viewModel.command = TextFieldState(
                            dataInputStream.readUTF(),
                            TextRange(
                                dataInputStream.readInt(),
                                dataInputStream.readInt()
                            )
                        )
                    }
                } catch (e: IOException) {
                    Log.e(pageName, "fail to save file : " + file.absolutePath, e)
                    MonitorUtil.generateCustomLog(e, "IOException")
                }
            }
        }
        core.setCommandGuiCoreInterface(object : CommandGuiCoreInterface {

            override fun isUpdateStructure() = true

            override fun isUpdateDescription() = true

            override fun isUpdateErrorReason() =
                Settings.INSTANCE.isShowErrorReason || isSyntaxHighlight()

            override fun isCheckingBySelection() = Settings.INSTANCE.isCheckingBySelection

            override fun isSyntaxHighlight() =
                Settings.INSTANCE.isSyntaxHighlight && viewModel.command.text.length < 200

            override fun updateStructure(structure: String?) {
                viewModel.structure = structure
            }

            override fun updateDescription(description: String?) {
                viewModel.paramHint = description
            }

            override fun updateErrorReason(errorReasons: Array<ErrorReason?>?) {
                viewModel.errorReasons = errorReasons
            }

            override fun updateSuggestions() {
                viewModel.suggestionsSize = core.getSuggestionsSize()
            }

            override fun getSelectedString(): SelectedString? {
                val selectionStart =
                    min(viewModel.command.selection.start, viewModel.command.selection.end)
                val selectionEnd =
                    max(viewModel.command.selection.start, viewModel.command.selection.end)
                return SelectedString(
                    viewModel.command.text.toString(),
                    selectionStart,
                    selectionEnd
                )
            }

            override fun setSelectedString(selectedString: SelectedString?) {
                viewModel.command.edit {
                    replace(0, length, selectedString?.text ?: "")
                    selection = TextRange(
                        selectedString?.selectionStart ?: 0,
                        selectedString?.selectionEnd ?: 0
                    )
                }
            }

            override fun updateSyntaxHighlight(tokens: IntArray?) {
                viewModel.syntaxHighlightTokens = tokens
            }
        })
        setContent {
            CompletionScreen(
                viewModel = viewModel,
                core = core,
            )
        }
    }

    override fun onPause() {
        super.onPause()
        // 保存上次的输入内容
        val file: File =
            FileUtil.getFile(filesDir.absolutePath, "cache", "lastInput.dat")
        if (!FileUtil.createParentFile(file)) {
            Log.e(pageName, "fail to create parent file : " + file.absolutePath)
            return
        }
        try {
            DataOutputStream(BufferedOutputStream(FileOutputStream(file))).use { dataOutputStream ->
                dataOutputStream.writeUTF(viewModel.command.text.toString())
                dataOutputStream.writeInt(viewModel.command.selection.start)
                dataOutputStream.writeInt(viewModel.command.selection.end)
            }
        } catch (e: IOException) {
            Log.e(pageName, "fail to save file : " + file.absolutePath, e)
            MonitorUtil.generateCustomLog(e, "IOException")
        }
    }

    override fun onResume() {
        super.onResume()
        val cpackPath = Settings.INSTANCE.cpackPath
        if (core.core == null || core.core!!.path != cpackPath) {
            var core1: CHelperCore? = null
            try {
                core1 = CHelperCore.fromAssets(assets, cpackPath)
            } catch (throwable: Throwable) {
                Toaster.show("资源包加载失败")
                Log.w(pageName, "fail to load resource pack", throwable)
                MonitorUtil.generateCustomLog(throwable, "LoadResourcePackException")
            }
            core.setCore(core1)
        }
    }

}