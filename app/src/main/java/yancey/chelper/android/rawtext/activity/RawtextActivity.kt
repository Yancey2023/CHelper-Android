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

package yancey.chelper.android.rawtext.activity

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.activity.viewModels
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.AnnotatedString.Range
import androidx.compose.ui.text.SpanStyle
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import yancey.chelper.android.common.activity.BaseComposeActivity
import yancey.chelper.ui.rawtext.RawtextScreen
import yancey.chelper.ui.rawtext.RawtextViewModel

/**
 * Json文本组件生成器界面
 */
class RawtextActivity : BaseComposeActivity() {

    override val pageName = "Rawtext"

    val viewModel: RawtextViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RawtextScreen(
                viewModel = viewModel,
                getRawJson = this::getRawJson
            )
        }
    }

    private fun getRawJson(): String {
        // 获取文本
        val annotatedString = viewModel.text.annotatedString
        // 获取颜色
        val colors = Array(annotatedString.length) { Color.White }
        for (range in annotatedString.spanStyles) {
            colors.fill(range.item.color, range.start, range.end)
        }
        // 输出Json
        val result = JsonObject()
        val rawText = JsonArray()
        val text = JsonObject()
        text.addProperty("text", buildString {
            var indexStart = 0
            for (i in colors.indices) {
                if (i == colors.size - 1) {
                    append(RawtextViewModel.colorFormats.find { it.color == colors[indexStart] }?.format)
                    append(annotatedString.text.substring(indexStart, colors.size))
                    break
                }
                if (colors[i] == colors[indexStart]) {
                    continue
                }
                append(RawtextViewModel.colorFormats.find { it.color == colors[indexStart] }?.format)
                append(annotatedString.text.substring(indexStart, i))
                indexStart = i
            }
        })
        rawText.add(text)
        result.add("rawtext", rawText)
        return result.toString()
    }
}
