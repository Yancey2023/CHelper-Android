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

package yancey.chelper.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import yancey.chelper.R
import yancey.chelper.android.common.dialog.InputStringDialog
import yancey.chelper.ui.CHelperTheme
import yancey.chelper.ui.Collection
import yancey.chelper.ui.CollectionName
import yancey.chelper.ui.Divider
import yancey.chelper.ui.NameAndAction
import yancey.chelper.ui.RootViewWithHeaderAndCopyright
import yancey.chelper.ui.SettingsItem

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    chooseCpack: () -> Unit,
    chooseBackground: () -> Unit,
    restoreBackground: () -> Unit,
    chooseTheme: () -> Unit,
) {
    RootViewWithHeaderAndCopyright(stringResource(R.string.layout_settings_title)) {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            CollectionName(stringResource(R.string.layout_settings_application_update))
            Collection {
                SettingsItem(
                    name = stringResource(R.string.layout_settings_is_enable_update_notification),
                    description = stringResource(R.string.layout_settings_is_enable_update_notification_description),
                    checked = viewModel.isEnableUpdateNotifications,
                    onCheckedChange = { viewModel.isEnableUpdateNotifications = it },
                )
            }
            CollectionName(stringResource(R.string.layout_settings_theme_settings))
            Collection {
                NameAndAction(
                    name = stringResource(R.string.layout_settings_choose_background),
                    description = stringResource(R.string.layout_settings_choose_background_description),
                ) {
                    chooseBackground()
                }
                Divider()
                NameAndAction(
                    name = stringResource(R.string.layout_settings_restore_background),
                    description = stringResource(R.string.layout_settings_restore_background_description),
                ) {
                    restoreBackground()
                }
                Divider()
                NameAndAction(
                    name = stringResource(R.string.layout_settings_choose_theme),
                    description = stringResource(R.string.layout_settings_choose_theme_description),
                ) {
                    chooseTheme()
                }
                Divider()
                NameAndAction(
                    name = stringResource(R.string.layout_settings_floating_window_alpha),
                    description = stringResource(R.string.layout_settings_floating_window_alpha_description),
                ) {
                    InputStringDialog(context)
                        .title("请输入透明度")
                        .defaultInput(
                            (viewModel.floatingWindowAlpha * 100).toInt().toString()
                        )
                        .onConfirm { s: String? ->
                            try {
                                var integer = s!!.toInt()
                                if (integer < 10) {
                                    integer = 10
                                } else if (integer > 100) {
                                    integer = 100
                                }
                                viewModel.floatingWindowAlpha = integer / 100f
                            } catch (_: NumberFormatException) {
                            }
                        }
                        .show()
                }
                Divider()
                NameAndAction(
                    name = stringResource(R.string.layout_settings_floating_window_size),
                    description = stringResource(R.string.layout_settings_floating_window_size_description),
                ) {
                    InputStringDialog(context)
                        .title("请输入大小")
                        .defaultInput(viewModel.floatingWindowSize.toString())
                        .onConfirm { s: String? ->
                            try {
                                var integer = s!!.toInt()
                                if (integer < 10) {
                                    integer = 10
                                } else if (integer > 100) {
                                    integer = 100
                                }
                                viewModel.floatingWindowSize = integer
                            } catch (_: java.lang.NumberFormatException) {
                            }
                        }
                        .show()
                }
            }
            CollectionName(stringResource(R.string.layout_settings_completion_settings))
            Collection {
                NameAndAction(
                    name = stringResource(R.string.layout_settings_choose_cpack),
                    description = stringResource(
                        R.string.layout_settings_current_cpack,
                        viewModel.currentCpackBranchTranslation
                    )
                ) {
                    chooseCpack()
                }
                Divider()
                SettingsItem(
                    name = stringResource(R.string.layout_setting_checking_by_selection),
                    description = stringResource(R.string.layout_setting_checking_by_selection_description),
                    checked = viewModel.isCheckingBySelection,
                    onCheckedChange = { viewModel.isCheckingBySelection = it },
                )
                Divider()
                SettingsItem(
                    name = stringResource(R.string.layout_setting_is_hide_window_when_copying),
                    description = stringResource(R.string.layout_setting_is_hide_window_when_copying_description),
                    checked = viewModel.isHideWindowWhenCopying,
                    onCheckedChange = { viewModel.isHideWindowWhenCopying = it },
                )
                Divider()
                SettingsItem(
                    name = stringResource(R.string.layout_setting_is_saving_when_pausing),
                    description = stringResource(R.string.layout_setting_is_saving_when_pausing_description),
                    checked = viewModel.isSavingWhenPausing,
                    onCheckedChange = { viewModel.isSavingWhenPausing = it },
                )
                Divider()
                SettingsItem(
                    name = stringResource(R.string.layout_setting_is_crowed),
                    description = stringResource(R.string.layout_setting_is_crowed_description),
                    checked = viewModel.isCrowed,
                    onCheckedChange = { viewModel.isCrowed = it },
                )
                Divider()
                SettingsItem(
                    name = stringResource(R.string.layout_setting_is_show_error_reason),
                    description = stringResource(R.string.layout_setting_is_show_error_reason_description),
                    checked = viewModel.isShowErrorReason,
                    onCheckedChange = { viewModel.isShowErrorReason = it },
                )
                Divider()
                SettingsItem(
                    name = stringResource(R.string.layout_setting_is_syntax_highlight),
                    description = stringResource(R.string.layout_setting_is_syntax_highlight_description),
                    checked = viewModel.isSyntaxHighlight,
                    onCheckedChange = { viewModel.isSyntaxHighlight = it },
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenLightThemePreview() {
    CHelperTheme(
        theme = CHelperTheme.Theme.Light,
        backgroundBitmap = null
    ) {
        SettingsScreen(viewModel(), {}, {}, {}, {})
    }
}

@Preview
@Composable
fun SettingsScreenDarkThemePreview() {
    CHelperTheme(
        theme = CHelperTheme.Theme.Dark,
        backgroundBitmap = null
    ) {
        SettingsScreen(viewModel(), {}, {}, {}, {})
    }
}
