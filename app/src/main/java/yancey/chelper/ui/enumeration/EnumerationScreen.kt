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

package yancey.chelper.ui.enumeration

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import yancey.chelper.R
import yancey.chelper.ui.Button
import yancey.chelper.ui.CHelperTheme
import yancey.chelper.ui.Icon
import yancey.chelper.ui.RootViewWithHeaderAndCopyright
import yancey.chelper.ui.Text
import yancey.chelper.ui.TextField

@Composable
fun EnumerationScreen(viewModel: EnumerationViewModel, run: () -> Unit) {
    RootViewWithHeaderAndCopyright(stringResource(R.string.enumeration)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(15.dp))
            TextField(
                state = viewModel.expression,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .height(100.dp),
                hint = stringResource(R.string.ed_enumeration_hint)
            )
            Spacer(Modifier.height(15.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
                    .height(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.tv_times))
                TextField(
                    state = viewModel.times,
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp),
                    isNarrow = true,
                    lineLimits = TextFieldLineLimits.SingleLine
                )
            }
            Spacer(Modifier.height(15.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp)
            ) {
                Text(
                    text = stringResource(R.string.variable_name),
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                    ),
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = stringResource(R.string.tv_start_value),
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                    ),
                )
                Spacer(modifier = Modifier.width(15.dp))
                Text(
                    text = stringResource(R.string.tv_interval),
                    modifier = Modifier.weight(1f),
                    style = TextStyle(
                        textAlign = TextAlign.Center,
                    ),
                )
                Spacer(modifier = Modifier.width(45.dp))
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                itemsIndexed(viewModel.variableList) { index, variable ->
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp)
                            .height(30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            state = variable.name,
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center,
                            isNarrow = true,
                            lineLimits = TextFieldLineLimits.SingleLine
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        TextField(
                            state = variable.start,
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center,
                            isNarrow = true,
                            lineLimits = TextFieldLineLimits.SingleLine
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        TextField(
                            state = variable.interval,
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center,
                            isNarrow = true,
                            lineLimits = TextFieldLineLimits.SingleLine
                        )
                        Spacer(modifier = Modifier.width(15.dp))
                        Icon(
                            id = R.drawable.x,
                            modifier = Modifier
                                .size(30.dp)
                                .clickable(onClick = {
                                    viewModel.variableList.removeAt(index)
                                }),
                        )
                    }
                    if (index == viewModel.variableList.size) {
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }
            Button(text = stringResource(R.string.btn_enumeration_add)) {
                viewModel.variableList.add(DataVariable())
            }
            Button(text = stringResource(R.string.btn_enumeration_run)) {
                run()
            }
        }
    }
}

@Preview
@Composable
fun EnumerationScreenLightThemePreview() {
    val viewModel = remember {
        EnumerationViewModel().apply {
            times = TextFieldState("20")
            for (i in 1..20) {
                variableList.add(
                    DataVariable().apply {
                        name = TextFieldState("name$i")
                        start = TextFieldState("1")
                        interval = TextFieldState("$i")
                    }
                )
            }
        }
    }
    CHelperTheme(theme = CHelperTheme.Theme.Light, backgroundBitmap = null) {
        EnumerationScreen(
            viewModel = viewModel,
            run = { },
        )
    }
}

@Preview
@Composable
fun EnumerationScreenDarkThemePreview() {
    val viewModel = remember {
        EnumerationViewModel().apply {
            times = TextFieldState("20")
            for (i in 1..20) {
                variableList.add(
                    DataVariable().apply {
                        name = TextFieldState("name$i")
                        start = TextFieldState("1")
                        interval = TextFieldState("$i")
                    }
                )
            }
        }
    }
    CHelperTheme(theme = CHelperTheme.Theme.Dark, backgroundBitmap = null) {
        EnumerationScreen(
            viewModel = viewModel,
            run = { },
        )
    }
}
