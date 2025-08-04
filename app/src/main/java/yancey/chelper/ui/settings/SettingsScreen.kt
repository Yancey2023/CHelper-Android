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
import androidx.lifecycle.viewmodel.compose.viewModel
import yancey.chelper.R
import yancey.chelper.ui.CHelperTheme
import yancey.chelper.ui.Collection
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
    RootViewWithHeaderAndCopyright(stringResource(R.string.settings)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(15.dp))
            Collection {
                SettingsItem(
                    name = stringResource(R.string.settings_is_enable_update_notification),
                    description = stringResource(R.string.settings_is_enable_update_notification_description),
                    checked = viewModel.isEnableUpdateNotifications,
                    onCheckedChange = { viewModel.isEnableUpdateNotifications = it },
                )
            }
            Spacer(Modifier.height(15.dp))
            Collection {
                NameAndAction(
                    name = stringResource(R.string.choose_background),
                    description = stringResource(R.string.choose_background_description),
                ) {
                    chooseBackground()
                }
                Divider()
                NameAndAction(
                    name = stringResource(R.string.restore_background_description),
                    description = stringResource(R.string.choose_background_description),
                ) {
                    restoreBackground()
                }
                Divider()
                NameAndAction(
                    name = stringResource(R.string.choose_theme),
                    description = stringResource(R.string.choose_theme_description),
                ) {
                    chooseTheme()
                }
            }
            Spacer(Modifier.height(15.dp))
            Collection {
                NameAndAction(
                    name = stringResource(R.string.choose_cpack),
                    description = stringResource(R.string.current_cpack, viewModel.currentCpackBranchTranslation)
                ) {
                    chooseCpack()
                }
                Divider()
                SettingsItem(
                    name = stringResource(R.string.setting_is_checking_by_selection),
                    description = stringResource(R.string.setting_is_checking_by_selection_description),
                    checked = viewModel.isCheckingBySelection,
                    onCheckedChange = { viewModel.isCheckingBySelection = it },
                )
                Divider()
                SettingsItem(
                    name = stringResource(R.string.setting_is_hide_window_when_copying),
                    description = stringResource(R.string.setting_is_hide_window_when_copying_description),
                    checked = viewModel.isHideWindowWhenCopying,
                    onCheckedChange = { viewModel.isHideWindowWhenCopying = it },
                )
                Divider()
                SettingsItem(
                    name = stringResource(R.string.setting_is_saving_when_pausing),
                    description = stringResource(R.string.setting_is_saving_when_pausing_description),
                    checked = viewModel.isSavingWhenPausing,
                    onCheckedChange = { viewModel.isSavingWhenPausing = it },
                )
                Divider()
                SettingsItem(
                    name = stringResource(R.string.setting_is_crowed),
                    description = stringResource(R.string.setting_is_crowed_description),
                    checked = viewModel.isCrowed,
                    onCheckedChange = { viewModel.isCrowed = it },
                )
                Divider()
                SettingsItem(
                    name = stringResource(R.string.setting_is_show_error_reason),
                    description = stringResource(R.string.setting_is_show_error_reason_description),
                    checked = viewModel.isShowErrorReason,
                    onCheckedChange = { viewModel.isShowErrorReason = it },
                )
                Divider()
                SettingsItem(
                    name = stringResource(R.string.setting_is_syntax_highlight),
                    description = stringResource(R.string.setting_is_syntax_highlight_description),
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
