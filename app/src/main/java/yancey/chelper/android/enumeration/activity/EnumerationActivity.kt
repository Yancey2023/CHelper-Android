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

package yancey.chelper.android.enumeration.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.hjq.toast.Toaster
import redempt.crunch.data.Pair
import redempt.crunch.exceptions.ExpressionCompilationException
import yancey.chelper.android.common.activity.BaseComposeActivity
import yancey.chelper.android.common.dialog.IsConfirmDialog
import yancey.chelper.android.common.util.ClipboardUtil
import yancey.chelper.android.common.util.MonitorUtil
import yancey.chelper.android.enumeration.util.CustomDoubleSupplier
import yancey.chelper.android.enumeration.util.EnumerationUtil
import yancey.chelper.ui.enumeration.EnumerationScreen
import yancey.chelper.ui.enumeration.EnumerationViewModel

/**
 * 穷举界面
 */
class EnumerationActivity : BaseComposeActivity() {

    override val pageName = "Enumeration"

    val viewModel: EnumerationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EnumerationScreen(viewModel = viewModel, run = this::run)
        }
    }

    private fun run() {
        val input = viewModel.expression.text.toString()
        val times: Int
        try {
            times = viewModel.times.text.toString().toInt()
        } catch (_: NumberFormatException) {
            Toaster.show("运行次数不是整数")
            return
        }
        val pairs: List<Pair<String, CustomDoubleSupplier>>
        try {
            pairs =
                viewModel.variableList.map { dataVariable ->
                    Pair(
                        dataVariable.name.text.toString(),
                        CustomDoubleSupplier(
                            dataVariable.start.text.toString().toDouble(),
                            dataVariable.interval.text.toString().toDouble()
                        )
                    )
                }.toList()
        } catch (_: NumberFormatException) {
            Toaster.show("变量数据的获取出错")
            return
        }
        val output: String
        try {
            output = EnumerationUtil.run(input, pairs, times)
        } catch (_: NumberFormatException) {
            Toaster.show("运行时出错：文字转数字时失败")
            return
        } catch (_: ExpressionCompilationException) {
            Toaster.show("运行时出错：表达式结构有问题")
            return
        } catch (throwable: Throwable) {
            Toaster.show("运行时出错")
            MonitorUtil.generateCustomLog(throwable, "ExpressionException")
            return
        }
        IsConfirmDialog(this, true)
            .title("输出预览")
            .message(output)
            .onConfirm("复制") {
                if (ClipboardUtil.setText(this, output)) {
                    Toaster.show("已复制")
                }
            }
            .show()
    }

}