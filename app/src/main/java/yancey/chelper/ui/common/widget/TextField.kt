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

package yancey.chelper.ui.common.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import yancey.chelper.ui.common.CHelperTheme
import yancey.chelper.ui.common.layout.Surface

@Composable
fun TextField(
    state: TextFieldState,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    hint: String? = null,
    isNarrow: Boolean = false,
    lineLimits: TextFieldLineLimits = TextFieldLineLimits.Default,
    style: TextStyle = TextStyle(),
) {
    BasicTextField(
        state = state,
        modifier = modifier,
        textStyle = style.copy(
            color = if (style.color == Color.Unspecified) CHelperTheme.colors.textMain else style.color,
            fontSize = if (style.fontSize == TextUnit.Unspecified) 16.sp else style.fontSize,
            textAlign =
                if (contentAlignment is BiasAlignment && contentAlignment.horizontalBias == 0f)
                    TextAlign.Center
                else
                    TextAlign.Start,
        ),
        lineLimits = lineLimits,
        cursorBrush = SolidColor(CHelperTheme.colors.mainColor),
        decorator = { innerTextField ->
            Surface(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = contentAlignment,
                isNarrow = isNarrow
            ) {
                innerTextField()
                if (state.text.isEmpty() && hint != null) {
                    Text(
                        text = hint,
                        modifier = Modifier.fillMaxWidth(),
                        style = TextStyle(
                            color = CHelperTheme.colors.textHint,
                        ),
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun TextFieldPreview() {
    CHelperTheme(
        theme = CHelperTheme.Theme.Light,
        backgroundBitmap = null
    ) {
        TextField(
            state = rememberTextFieldState(),
            modifier = Modifier
                .padding(15.dp)
                .width(100.dp)
                .height(40.dp)
                .background(CHelperTheme.colors.backgroundComponent),
            hint = "Hint"
        )
    }
}
