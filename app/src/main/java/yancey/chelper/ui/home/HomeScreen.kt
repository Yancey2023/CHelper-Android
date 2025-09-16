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

package yancey.chelper.ui.home

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.hjq.toast.Toaster
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import yancey.chelper.R
import yancey.chelper.android.common.util.PolicyGrantManager
import yancey.chelper.android.completion.activity.CompletionActivity
import yancey.chelper.android.completion.util.CompletionWindowManager
import yancey.chelper.android.library.activity.LocalLibraryListActivity
import yancey.chelper.ui.AboutScreenKey
import yancey.chelper.ui.EnumerationScreenKey
import yancey.chelper.ui.Old2NewIMEGuideScreenKey
import yancey.chelper.ui.Old2NewScreenKey
import yancey.chelper.ui.RawtextScreenKey
import yancey.chelper.ui.SettingsScreenKey
import yancey.chelper.ui.ShowTextScreenKey
import yancey.chelper.ui.common.CHelperTheme
import yancey.chelper.ui.common.dialog.IsConfirmDialog
import yancey.chelper.ui.common.dialog.PolicyGrantDialog
import yancey.chelper.ui.common.layout.Collection
import yancey.chelper.ui.common.layout.CollectionName
import yancey.chelper.ui.common.layout.Copyright
import yancey.chelper.ui.common.layout.NameAndAction
import yancey.chelper.ui.common.layout.NameAndStartActivity
import yancey.chelper.ui.common.layout.RootView
import yancey.chelper.ui.common.widget.Divider
import yancey.chelper.ui.common.widget.Text

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
) {
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        if (viewModel.policyGrantState == PolicyGrantManager.State.AGREE) {
            viewModel.showAnnouncementDialog(context)
        }
    }
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
                    NameAndAction(stringResource(R.string.layout_home_command_completion_settings)) {
                        navController.navigate(SettingsScreenKey)
                    }
                }
                CollectionName(stringResource(R.string.layout_home_old2new))
                Collection {
                    NameAndAction(stringResource(R.string.layout_home_old2new_app_mode)) {
                        navController.navigate(Old2NewScreenKey)
                    }
                    Divider()
                    NameAndAction(stringResource(R.string.layout_home_old2new_ime_mode)) {
                        navController.navigate(Old2NewIMEGuideScreenKey)
                    }
                }
                CollectionName(stringResource(R.string.layout_home_enumeration))
                Collection {
                    NameAndAction(stringResource(R.string.layout_home_enumeration_app_mode)) {
                        navController.navigate(EnumerationScreenKey)
                    }
                }
                CollectionName(stringResource(R.string.layout_home_experimental_feature))
                Collection {
                    NameAndStartActivity(
                        stringResource(R.string.layout_home_experimental_feature_local_library),
                        LocalLibraryListActivity::class.java
                    )
                    Divider()
                    NameAndAction(stringResource(R.string.layout_home_experimental_feature_public_library)) {
                        // TODO
                    }
                    Divider()
                    NameAndAction(stringResource(R.string.layout_home_experimental_feature_raw_json_studio)) {
                        navController.navigate(RawtextScreenKey)
                    }
                }
                CollectionName(stringResource(R.string.layout_home_about))
                Collection {
                    NameAndAction(stringResource(R.string.layout_home_about_app_mode)) {
                        navController.navigate(AboutScreenKey)
                    }
                }
            }
            Copyright(Modifier.align(Alignment.CenterHorizontally))
        }
    }
    if (viewModel.isShowPolicyGrantDialog) {
        val policyPageTitle = stringResource(R.string.layout_about_privacy_policy)
        PolicyGrantDialog(
            content = if (viewModel.policyGrantState == PolicyGrantManager.State.NOT_READ)
                stringResource(R.string.dialog_policy_grant_message_if_unread) else
                stringResource(R.string.dialog_policy_grant_message_if_updated),
            readPolicy = {
                viewModel.viewModelScope.launch {
                    val content = withContext(Dispatchers.IO) {
                        context.assets.open("about/privacy_policy.txt").bufferedReader()
                            .use { it.readText() }
                    }
                    navController.navigate(ShowTextScreenKey(policyPageTitle, content))
                }
            },
            onConfirm = {
                viewModel.agreePolicy()
            },
        )
    }
    if (viewModel.isShowAnnouncementDialog) {
        IsConfirmDialog(
            onDismissRequest = { viewModel.dismissAnnouncementDialog(context) },
            isBig = viewModel.announcement!!.isBigDialog!!,
            title = viewModel.announcement!!.title!!,
            content = viewModel.announcement!!.message!!,
            cancelText = if (viewModel.announcement!!.isForce!!) "取消" else "不再提醒",
            onCancel = {
                if (!viewModel.announcement!!.isForce!!) {
                    viewModel.ignoreCurrentAnnouncement(context)
                }
            }
        )
    }
    if (viewModel.isShowUpdateNotificationsDialog) {
        val content = remember(viewModel.latestVersionInfo) {
            viewModel.latestVersionInfo!!.version_name + "版本已发布，欢迎下载体验。本次更新内容如下：\n" + viewModel.latestVersionInfo!!.changelog
        }
        IsConfirmDialog(
            onDismissRequest = { viewModel.dismissUpdateNotificationDialog(context) },
            title = "更新提醒",
            content = content,
            cancelText = "忽略此版本",
            onCancel = { viewModel.ignoreLatestVersion(context) }
        )
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