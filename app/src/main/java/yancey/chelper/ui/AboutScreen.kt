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

package yancey.chelper.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import yancey.chelper.BuildConfig
import yancey.chelper.R

@Composable
fun AboutScreen() {
    RootViewWithHeaderAndCopyright(stringResource(R.string.about)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(15.dp))
            Collection {
                NameAndValue("当前版本", BuildConfig.VERSION_NAME)
                Divider()
                NameAndValue("作者", "Yancey")
                Divider()
                NameAndValue("QQ号", "1709185482")
                Divider()
                NameAndValue("QQ群号", "766625597")
                Divider()
                NameAndLink(
                    "bilibili",
                    "https://space.bilibili.com/470179011".toUri()
                )
                Divider()
                NameAndLink(
                    "内核源码",
                    "https://github.com/Yancey2023/CHelper-Core".toUri()
                )
                Divider()
                NameAndLink(
                    "软件源码",
                    "https://github.com/Yancey2023/CHelper-Android".toUri()
                )
            }
            Spacer(Modifier.height(15.dp))
            Collection {
                NameAndAsset("更新日志", "about/release_note.txt")
                Divider()
                NameAndLink(
                    "最新版更新日志",
                    "https://www.yanceymc.cn/blog/article/chelper-release-notes".toUri()
                )
                Divider()
                NameAndAsset("隐私政策", "about/privacy_policy.txt")
                Divider()
                NameAndAsset("开源条款", "about/open_source_terms.txt")
            }
        }
    }
}

@Preview
@Composable
fun AboutScreenLightThemePreview() {
    CHelperTheme(
        theme = CHelperTheme.Theme.Light,
        backgroundBitmap = null
    ) {
        AboutScreen()
    }
}

@Preview
@Composable
fun AboutScreenDarkThemePreview() {
    CHelperTheme(theme = CHelperTheme.Theme.Dark, backgroundBitmap = null) {
        AboutScreen()
    }
}
