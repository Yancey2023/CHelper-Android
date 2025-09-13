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

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hjq.toast.Toaster
import yancey.chelper.R
import yancey.chelper.android.about.activity.AboutActivity
import yancey.chelper.android.common.activity.SettingsActivity
import yancey.chelper.android.completion.activity.CompletionActivity
import yancey.chelper.android.completion.util.CompletionWindowManager
import yancey.chelper.android.enumeration.activity.EnumerationActivity
import yancey.chelper.android.library.activity.LocalLibraryListActivity
import yancey.chelper.android.old2new.activity.Old2NewActivity
import yancey.chelper.android.old2new.activity.Old2NewIMEGuideActivity
import yancey.chelper.android.rawtext.activity.RawtextActivity

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    RootView {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CHelperTheme.colors.backgroundComponent)
                        .padding(horizontal = 25.dp, vertical = 15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.pack_icon),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier.size(width = 70.dp, height = 70.dp)
                    )
                    Column(modifier = Modifier.padding(start = 20.dp)) {
                        Text(
                            text = stringResource(R.string.layout_home_app_name),
                            style = TextStyle(
                                color = CHelperTheme.colors.textBond,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            ),
                        )
                        Text(
                            text = stringResource(R.string.layout_home_app_description),
                            style = TextStyle(
                                color = CHelperTheme.colors.textBond,
                                fontSize = 18.sp
                            ),
                        )
                    }
                }
                CollectionName(stringResource(R.string.layout_home_command_completion))
                Collection {
                    NameAndAction(
                        name = stringResource(R.string.layout_home_command_completion_app_mode),
                        onClick = {
                            if (CompletionWindowManager.INSTANCE.isUsingFloatingWindow) {
                                Toaster.show("你必须关闭悬浮窗模式才可以进入应用模式")
                            } else {
                                context.startActivity(
                                    Intent(
                                        context,
                                        CompletionActivity::class.java
                                    )
                                )
                            }
                        }
                    )
                    Divider()
                    NameAndAction(
                        name = stringResource(R.string.layout_home_command_completion_floating_window_mode),
                        onClick = {
                            if (CompletionWindowManager.INSTANCE.isUsingFloatingWindow) {
                                CompletionWindowManager.INSTANCE.stopFloatingWindow()
                            } else {
                                CompletionWindowManager.INSTANCE.startFloatingWindow(context)
                            }
                        }
                    )
                    Divider()
                    NameAndStartActivity(
                        name = stringResource(R.string.layout_home_command_completion_settings),
                        activityClass = SettingsActivity::class.java
                    )
                }
                CollectionName(stringResource(R.string.layout_home_old2new))
                Collection {
                    NameAndStartActivity(
                        stringResource(R.string.layout_home_old2new_app_mode),
                        Old2NewActivity::class.java
                    )
                    Divider()
                    NameAndStartActivity(
                        stringResource(R.string.layout_home_old2new_ime_mode),
                        Old2NewIMEGuideActivity::class.java
                    )
                }
                CollectionName(stringResource(R.string.layout_home_enumeration))
                Collection {
                    NameAndStartActivity(
                        stringResource(R.string.layout_home_enumeration_app_mode),
                        EnumerationActivity::class.java
                    )
                }
                CollectionName(stringResource(R.string.layout_home_experimental_feature))
                Collection {
                    NameAndStartActivity(
                        stringResource(R.string.layout_home_experimental_feature_local_library),
                        LocalLibraryListActivity::class.java
                    )
                    Divider()
                    NameAndAction(
                        name = stringResource(R.string.layout_home_experimental_feature_public_library),
                        onClick = {
//                    startActivity(
//                        Intent(
//                            context,
//                            PublicLibraryListView::class.java
//                        )
//                    )
                        }
                    )
                    Divider()
                    NameAndStartActivity(
                        stringResource(R.string.layout_home_experimental_feature_raw_json_studio),
                        RawtextActivity::class.java
                    )
                }
                CollectionName(stringResource(R.string.layout_home_about))
                Collection {
                    NameAndStartActivity(
                        stringResource(R.string.layout_home_about_app_mode),
                        AboutActivity::class.java
                    )
                }
            }
            Copyright(Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Preview
@Composable
fun HomeScreenLightThemePreview() {
    CHelperTheme(theme = CHelperTheme.Theme.Light, backgroundBitmap = null) {
        HomeScreen()
    }
}

@Preview
@Composable
fun HomeScreenDarkThemePreview() {
    CHelperTheme(theme = CHelperTheme.Theme.Dark, backgroundBitmap = null) {
        HomeScreen()
    }
}