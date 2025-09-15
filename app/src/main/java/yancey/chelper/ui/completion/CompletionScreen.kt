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

package yancey.chelper.ui.completion

import android.content.ClipData
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import yancey.chelper.R
import yancey.chelper.core.CHelperGuiCore
import yancey.chelper.core.ErrorReason
import yancey.chelper.core.Suggestion
import yancey.chelper.ui.common.CHelperTheme
import yancey.chelper.ui.common.layout.RootView
import yancey.chelper.ui.common.widget.Icon
import yancey.chelper.ui.common.widget.Text

@Composable
fun CommandTextField(
    value: TextFieldState,
    modifier: Modifier = Modifier,
    hint: String? = null,
    errorReasons: Array<ErrorReason?>?,
    syntaxHighlightTokens: IntArray?,
) {
    var textLayoutResult: TextLayoutResult? = null
    val scrollState = rememberScrollState()
    val syntaxHighlightBoolean = CHelperTheme.colors.syntaxHighlightBoolean
    val syntaxHighlightFloat = CHelperTheme.colors.syntaxHighlightFloat
    val syntaxHighlightInteger = CHelperTheme.colors.syntaxHighlightInteger
    val syntaxHighlightSymbol = CHelperTheme.colors.syntaxHighlightSymbol
    val syntaxHighlightId = CHelperTheme.colors.syntaxHighlightId
    val syntaxHighlightTargetSelector = CHelperTheme.colors.syntaxHighlightTargetSelector
    val syntaxHighlightCommand = CHelperTheme.colors.syntaxHighlightCommand
    val syntaxHighlightBrackets1 = CHelperTheme.colors.syntaxHighlightBrackets1
    val syntaxHighlightBrackets2 = CHelperTheme.colors.syntaxHighlightBrackets2
    val syntaxHighlightBrackets3 = CHelperTheme.colors.syntaxHighlightBrackets3
    val syntaxHighlightString = CHelperTheme.colors.syntaxHighlightString
    val syntaxHighlightNull = CHelperTheme.colors.syntaxHighlightNull
    val syntaxHighlightRange = CHelperTheme.colors.syntaxHighlightRange
    val syntaxHighlightLiteral = CHelperTheme.colors.syntaxHighlightLiteral
    val outputTransform = remember(
        syntaxHighlightTokens,
        syntaxHighlightBoolean,
        syntaxHighlightFloat,
        syntaxHighlightInteger,
        syntaxHighlightSymbol,
        syntaxHighlightId,
        syntaxHighlightTargetSelector,
        syntaxHighlightCommand,
        syntaxHighlightBrackets1,
        syntaxHighlightBrackets2,
        syntaxHighlightBrackets3,
        syntaxHighlightString,
        syntaxHighlightNull,
        syntaxHighlightRange,
        syntaxHighlightLiteral,
    ) {
        OutputTransformation {
            if (syntaxHighlightTokens == null || syntaxHighlightTokens.isEmpty()) {
                return@OutputTransformation
            }
            val getColorByToken = { token: Int ->
                when (token) {
                    1 -> syntaxHighlightBoolean
                    2 -> syntaxHighlightFloat
                    3 -> syntaxHighlightInteger
                    4 -> syntaxHighlightSymbol
                    5 -> syntaxHighlightId
                    6 -> syntaxHighlightTargetSelector
                    7 -> syntaxHighlightCommand
                    8 -> syntaxHighlightBrackets1
                    9 -> syntaxHighlightBrackets2
                    10 -> syntaxHighlightBrackets3
                    11 -> syntaxHighlightString
                    12 -> syntaxHighlightNull
                    13 -> syntaxHighlightRange
                    14 -> syntaxHighlightLiteral
                    else -> null
                }
            }
            var lastIndex = 0
            var lastColor = getColorByToken(syntaxHighlightTokens[0])
            for (i in 1..<syntaxHighlightTokens.size) {
                val color = getColorByToken(syntaxHighlightTokens[i])
                if (color != lastColor) {
                    if (lastColor != null) {
                        addStyle(SpanStyle(color = lastColor), lastIndex, i)
                    }
                    lastIndex = i
                    lastColor = color
                }
            }
            if (lastColor != null) {
                addStyle(SpanStyle(color = lastColor), lastIndex, syntaxHighlightTokens.size)
            }
        }
    }
    val underlineErrorReason = CHelperTheme.colors.underlineErrorReason
    val density = LocalDensity.current
    val strokeWidth: Float = remember(density) {
        with(density) {
            1.dp.toPx()
        }
    }
    val underlineDeltaY: Float = remember(density) {
        with(density) {
            10.sp.toPx()
        }
    }
    BasicTextField(
        state = value,
        modifier = modifier.drawWithContent {
            drawContent()
            val length = value.text.length
            if (errorReasons != null && textLayoutResult != null) {
                clipRect {
                    for (errorReason in errorReasons) {
                        var start = errorReason!!.start
                        var end = errorReason.end
                        if (start < 0 || end > length) {
                            continue
                        }
                        if (start == end && length != 0) {
                            if (start == length) {
                                start--
                            } else {
                                end++
                            }
                        }
                        val lineStart = textLayoutResult!!.getLineForOffset(start)
                        val lineEnd = textLayoutResult!!.getLineForOffset(end)
                        if (lineStart == lineEnd) {
                            val y = (textLayoutResult!!.getLineBottom(lineStart) + underlineDeltaY)
                            drawLine(
                                color = underlineErrorReason,
                                start = Offset(
                                    x = textLayoutResult!!.getHorizontalPosition(
                                        start,
                                        true
                                    ),
                                    y = y
                                ),
                                end = Offset(
                                    x = textLayoutResult!!.getHorizontalPosition(
                                        end,
                                        false
                                    ),
                                    y = y
                                ),
                                strokeWidth = strokeWidth,
                            )
                        }
                    }
                }
            }
        },
        onTextLayout = { getResult ->
            textLayoutResult = getResult()
        },
        textStyle = TextStyle(
            color = CHelperTheme.colors.textMain,
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
        ),
        outputTransformation = outputTransform,
        cursorBrush = SolidColor(CHelperTheme.colors.mainColor),
        lineLimits = TextFieldLineLimits.SingleLine,
        decorator = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart
            ) {
                innerTextField()
                if (value.text.isEmpty() && hint != null) {
                    Text(
                        text = hint,
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            color = CHelperTheme.colors.textHint,
                        ),
                    )
                }
            }
        },
        scrollState = scrollState
    )
}

@Composable
fun ToolbarItem(@DrawableRes id: Int, description: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(5.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            id = id,
            modifier = Modifier.size(26.dp),
            contentDescription = description
        )
        Text(
            text = description,
            style = TextStyle(fontSize = 14.sp)
        )
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun CompletionScreen(viewModel: CompletionViewModel, core: CHelperGuiCore) {
    val clipboard = LocalClipboard.current
    RootView {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CHelperTheme.colors.backgroundComponent)
        ) {
            Text(
                text = viewModel.structure ?: "欢迎使用CHelper",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
            )
            Text(
                text = viewModel.paramHint ?: "作者：Yancey",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                style = TextStyle(
                    color = CHelperTheme.colors.textSecondary,
                )
            )
            val errorReason = remember(viewModel.errorReasons) {
                if (viewModel.errorReasons == null || viewModel.errorReasons!!.isEmpty()) {
                    return@remember null
                } else {
                    if (viewModel.errorReasons!!.size == 1) {
                        return@remember viewModel.errorReasons!![0]!!.errorReason
                    } else {
                        val errorReasonStr = StringBuilder("可能的错误原因：")
                        for (i in viewModel.errorReasons!!.indices) {
                            errorReasonStr.append("\n").append(i + 1).append(". ")
                                .append(viewModel.errorReasons!![i]!!.errorReason)
                        }
                        return@remember errorReasonStr.toString()
                    }
                }
            }
            errorReason?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp),
                    style = TextStyle(
                        color = CHelperTheme.colors.textErrorReason,
                    )
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(CHelperTheme.colors.line)
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(viewModel.suggestionsSize) { suggestionIndex ->
                    Column(
                        modifier = Modifier
                            .clickable(onClick = {
                                core.onItemClick(suggestionIndex)
                                core.onSelectionChanged()
                            })
                            .padding(5.dp)
                    ) {
                        val suggestion = remember(viewModel.suggestionsSize, suggestionIndex) {
                            core.getSuggestion(suggestionIndex)
                        }
                        suggestion?.name?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                style = TextStyle(
                                    fontSize = 14.sp
                                )
                            )
                        }
                        suggestion?.description?.let {
                            Text(
                                text = it,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                style = TextStyle(
                                    color = CHelperTheme.colors.textSecondary,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }
            if (viewModel.isShowMenu) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CHelperTheme.colors.background)
                ) {
                    ToolbarItem(
                        id = R.drawable.arrow_back_up,
                        description = stringResource(R.string.layout_completion_undo),
                        onClick = {
                            viewModel.command.undoState.undo()
                        }
                    )
                    ToolbarItem(
                        id = R.drawable.arrow_forward_up,
                        description = stringResource(R.string.layout_completion_redo),
                        onClick = {
                            viewModel.command.undoState.redo()
                        }
                    )
                    ToolbarItem(
                        id = R.drawable.x,
                        description = stringResource(R.string.layout_completion_clear),
                        onClick = {
                            viewModel.command.clearText()
                        }
                    )
                    ToolbarItem(
                        id = R.drawable.history,
                        description = stringResource(R.string.layout_completion_history),
                        onClick = {

                        }
                    )
                    ToolbarItem(
                        id = R.drawable.box,
                        description = stringResource(R.string.layout_completion_local_library),
                        onClick = {

                        }
                    )
                    ToolbarItem(
                        id = R.drawable.power,
                        description = stringResource(R.string.layout_completion_shut_down),
                        onClick = {

                        }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(CHelperTheme.colors.background),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    id = if (viewModel.isShowMenu) R.drawable.chevron_down else R.drawable.chevron_up,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            viewModel.isShowMenu = !viewModel.isShowMenu
                        }
                        .padding(8.dp),
                    contentDescription = stringResource(R.string.layout_completion_icon_show_menu_content_description)
                )
                CommandTextField(
                    value = viewModel.command,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    hint = stringResource(R.string.layout_completion_command_hint),
                    errorReasons = viewModel.errorReasons,
                    syntaxHighlightTokens = viewModel.syntaxHighlightTokens
                )
                Icon(
                    id = R.drawable.copy,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            viewModel.viewModelScope.launch {
                                clipboard.setClipEntry(
                                    ClipEntry(
                                        ClipData.newPlainText(
                                            null,
                                            viewModel.command.text
                                        )
                                    )
                                )
                            }
                        }
                        .padding(8.dp),
                    contentDescription = stringResource(R.string.common_icon_copy_content_description)
                )
            }
        }
        LaunchedEffect(viewModel.command.text, viewModel.command.selection) {
            core.onSelectionChanged()
        }
    }
}

@Preview
@Composable
fun CompletionScreenLightThemePreview() {
    val viewModel = remember {
        CompletionViewModel().apply {
            isShowMenu = true
            suggestionsSize = 20
        }
    }
    CHelperTheme(theme = CHelperTheme.Theme.Light, backgroundBitmap = null) {
        CompletionScreen(
            viewModel = viewModel,
            core = object : CHelperGuiCore() {
                override fun getSuggestion(index: Int): Suggestion? {
                    return Suggestion().apply {
                        name = "name$index"
                        description = "description$index"
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun CompletionScreenDarkThemePreview() {
    val viewModel = remember {
        CompletionViewModel().apply {
            isShowMenu = true
            suggestionsSize = 20
        }
    }
    CHelperTheme(theme = CHelperTheme.Theme.Dark, backgroundBitmap = null) {
        CompletionScreen(
            viewModel = viewModel,
            core = object : CHelperGuiCore() {
                override fun getSuggestion(index: Int): Suggestion? {
                    return Suggestion().apply {
                        name = "name$index"
                        description = "description$index"
                    }
                }
            }
        )
    }
}