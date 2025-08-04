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

package yancey.chelper.ui.old2new

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import yancey.chelper.R
import yancey.chelper.android.common.util.ClipboardUtil
import yancey.chelper.ui.Button
import yancey.chelper.ui.CHelperTheme
import yancey.chelper.ui.RootViewWithHeaderAndCopyright
import yancey.chelper.ui.Surface
import yancey.chelper.ui.Text
import yancey.chelper.ui.TextField

@Composable
fun Old2NewScreen(viewModel: Old2NewViewModel, old2new: (String) -> String) {
    val context = LocalContext.current
    RootViewWithHeaderAndCopyright(stringResource(R.string.old2new)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(15.dp))
                TextField(
                    state = viewModel.oldCommand,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .height(200.dp),
                    hint = stringResource(R.string.ed_hint_old_command)
                )
                Spacer(modifier = Modifier.height(15.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 15.dp)
                        .height(200.dp)
                ) {
                    Text(text = viewModel.newCommand)
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
            Button(stringResource(R.string.btn_old2new_paste)) {
                val charSequence = ClipboardUtil.getText(context)
                if (charSequence != null) {
                    val text = charSequence.toString()
                    viewModel.oldCommand.setTextAndPlaceCursorAtEnd(text)
                }
            }
            Button(stringResource(R.string.btn_old2new_copy)) {
                ClipboardUtil.setText(context, viewModel.newCommand)
            }
        }
        LaunchedEffect(viewModel.oldCommand.text) {
            viewModel.newCommand = old2new(viewModel.oldCommand.text.toString())
        }
    }
}

@Preview
@Composable
fun Old2NewScreenLightThemePreview() {
    CHelperTheme(
        theme = CHelperTheme.Theme.Light,
        backgroundBitmap = null
    ) {
        Old2NewScreen(viewModel = viewModel(), old2new = { old -> old })
    }
}

@Preview
@Composable
fun Old2NewScreenDarkThemePreview() {
    CHelperTheme(
        theme = CHelperTheme.Theme.Dark,
        backgroundBitmap = null
    ) {
        Old2NewScreen(viewModel = viewModel(), old2new = { old -> old })
    }
}