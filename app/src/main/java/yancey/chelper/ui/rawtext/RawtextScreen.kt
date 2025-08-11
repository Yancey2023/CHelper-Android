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

package yancey.chelper.ui.rawtext

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import yancey.chelper.R
import yancey.chelper.android.common.util.ClipboardUtil
import yancey.chelper.ui.Button
import yancey.chelper.ui.CHelperTheme
import yancey.chelper.ui.RootViewWithHeaderAndCopyright
import yancey.chelper.ui.Surface
import yancey.chelper.ui.Text
import kotlin.math.max
import kotlin.math.min

@Composable
fun RawJsonTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
) {
    BasicTextField(
        value = value,
        onValueChange = {
            onValueChange(
                it.copy(
                    annotatedString = if (value.text == it.text) value.annotatedString else buildAnnotatedString {
                        append(it.text)
                        val oldSelectionStart = min(
                            value.selection.start,
                            value.selection.end
                        )
                        val oldSelectionEnd = max(
                            value.selection.start,
                            value.selection.end
                        )
                        val newSelectionEnd = max(
                            it.selection.start,
                            it.selection.end
                        )
                        val insertLength = newSelectionEnd - oldSelectionEnd
                        val mapOffset = { offset: Int ->
                            if (offset < oldSelectionStart)
                                offset
                            else if (offset <= oldSelectionEnd)
                                newSelectionEnd
                            else
                                offset + insertLength
                        }
                        value.annotatedString.spanStyles.forEach { spanStyle ->
                            val spanStyleStart = min(spanStyle.start, spanStyle.end)
                            val spanStyleEnd = max(spanStyle.start, spanStyle.end)
                            if (spanStyleStart <= oldSelectionStart || spanStyleEnd >= oldSelectionEnd) {
                                addStyle(
                                    style = spanStyle.item,
                                    start = mapOffset(spanStyleStart),
                                    end = mapOffset(spanStyleEnd)
                                )
                            }
                        }
                    }
                )
            )
        },
        modifier = modifier,
        textStyle = TextStyle(
            color = Color(0xFFFFFFFF),
            fontSize = 16.sp,
            textAlign = TextAlign.Start,
        ),
        cursorBrush = SolidColor(CHelperTheme.colors.mainColor),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(color = Color(0xFF141414))
                    .padding(10.dp),
                contentAlignment = Alignment.TopStart
            ) {
                innerTextField()
                if (value.text.isEmpty() && hint != null) {
                    Text(
                        text = hint,
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            color = Color(0xFFAAAAAA),
                        ),
                    )
                }
            }
        }
    )
}

@Composable
fun RawtextScreen(viewModel: RawtextViewModel, getRawJson: () -> String) {
    val context = LocalContext.current
    val output = remember(viewModel.text) { getRawJson() }
    RootViewWithHeaderAndCopyright(stringResource(R.string.raw_json_studio)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Spacer(Modifier.height(15.dp))
                RawJsonTextField(
                    value = viewModel.text,
                    onValueChange = { viewModel.text = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .height(200.dp),
                    hint = stringResource(R.string.ed_hint_rawtext)
                )
                Spacer(Modifier.height(15.dp))
                if (viewModel.isPreview) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 15.dp)
                    ) {
                        Text(text = output)
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        modifier = Modifier.padding(horizontal = 15.dp)
                    ) {
                        items(RawtextViewModel.colorFormats.size) {
                            val colorFormat = RawtextViewModel.colorFormats[it]
                            Box(
                                modifier = Modifier
                                    .padding(5.dp)
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(colorFormat.color)
                                    .clickable {
                                        val selection = viewModel.text.selection
                                        if (selection.start != selection.end) {
                                            viewModel.text = viewModel.text.copy(
                                                annotatedString = buildAnnotatedString {
                                                    append(viewModel.text.annotatedString)
                                                    addStyle(
                                                        style = SpanStyle(color = colorFormat.color),
                                                        start = min(
                                                            selection.start,
                                                            selection.end
                                                        ),
                                                        end = max(
                                                            selection.start,
                                                            selection.end
                                                        )
                                                    )
                                                }
                                            )
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = colorFormat.format,
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        color = if (colorFormat.isLight) Color.Black else Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
            if (viewModel.isPreview) {
                Button(text = stringResource(R.string.copy)) {
                    ClipboardUtil.setText(context, viewModel.text.text)
                }
            }
            Button(text = stringResource(if (viewModel.isPreview) R.string.color else R.string.preview)) {
                viewModel.isPreview = !viewModel.isPreview
            }
        }
    }
}

@Preview
@Composable
fun RawtextScreenLightThemePreview() {
    CHelperTheme(theme = CHelperTheme.Theme.Light, backgroundBitmap = null) {
        RawtextScreen(
            viewModel = viewModel(),
            getRawJson = { "This is output." },
        )
    }
}

@Preview
@Composable
fun RawtextScreenDarkThemePreview() {
    CHelperTheme(theme = CHelperTheme.Theme.Dark, backgroundBitmap = null) {
        RawtextScreen(
            viewModel = viewModel(),
            getRawJson = { "This is output." },
        )
    }
}
