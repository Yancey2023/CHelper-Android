/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
 * Copyright (C) 2025  Yancey
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package yancey.chelper.android.common.view;

import android.annotation.SuppressLint;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.Toaster;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.common.dialog.ChoosingDialog;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.style.CustomTheme;
import yancey.chelper.android.common.util.MonitorUtil;
import yancey.chelper.android.common.util.Settings;

/**
 * 设置界面
 */
@SuppressLint("ViewConstructor")
public class SettingsView extends BaseView {

    public SettingsView(@NonNull CustomContext customContext, @Nullable Runnable backgroundPicker) {
        super(customContext, R.layout.layout_settings);
        // 页面顶部逻辑
        findViewById(R.id.back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        // 新版本提醒
        SwitchCompat isEnableUpdateNotification = view.findViewById(R.id.cb_enable_update_notification);
        isEnableUpdateNotification.setChecked(Settings.INSTANCE.isEnableUpdateNotifications);
        isEnableUpdateNotification.setOnCheckedChangeListener((buttonView, isChecked) -> Settings.INSTANCE.isEnableUpdateNotifications = isChecked);
        // 自定义UI设置
        RelativeLayout btn_chooseBackground = view.findViewById(R.id.btn_choose_background);
        btn_chooseBackground.setOnClickListener(v -> {
            if (getEnvironment() != Environment.APPLICATION) {
                Toaster.show("不支持在悬浮窗模式设置背景");
            } else if (backgroundPicker == null) {
                Toaster.show("当前场景不支持设置背景");
            } else if (XXPermissions.isGranted(context, Permission.READ_MEDIA_IMAGES)) {
                backgroundPicker.run();
            } else {
                XXPermissions.with(context)
                        .permission(Permission.READ_MEDIA_IMAGES)
                        .request(new OnPermissionCallback() {
                            @Override
                            public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                Toaster.show("图片访问权限申请成功");
                            }

                            @Override
                            public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                                Toaster.show("图片访问权限申请失败");
                            }
                        });
            }
        });
        RelativeLayout btn_restoreBackground = view.findViewById(R.id.btn_restore_background);
        btn_restoreBackground.setOnClickListener(v -> {
            if (getEnvironment() != Environment.APPLICATION) {
                Toaster.show("不支持在悬浮窗模式恢复背景");
            } else {
                new IsConfirmDialog(context, false)
                        .message("是否恢复背景？")
                        .onConfirm(() -> {
                            try {
                                CustomTheme.INSTANCE.setBackGroundDrawable(null);
                                backgroundUpdateTimes = CustomTheme.INSTANCE.invokeBackgroundForce(findViewById(R.id.main));
                            } catch (IOException e) {
                                Toaster.show(e.getMessage());
                                MonitorUtil.generateCustomLog(e, "ResetBackgroundException");
                            }
                        }).show();
            }
        });
        RelativeLayout btn_chooseTheme = view.findViewById(R.id.btn_choose_theme);
        btn_chooseTheme.setOnClickListener(v -> {
            if (getEnvironment() != Environment.APPLICATION) {
                Toaster.show("不支持在悬浮窗模式选择主题");
            } else {
                new ChoosingDialog(context, List.of("浅色模式", "深色模式", "跟随系统"), (which) -> {
                    Settings.INSTANCE.themeId = switch (which) {
                        case 0 -> "MODE_NIGHT_NO";
                        case 1 -> "MODE_NIGHT_YES";
                        case 2 -> "MODE_NIGHT_FOLLOW_SYSTEM";
                        default -> throw new IllegalStateException("Unexpected value: " + which);
                    };
                    CustomTheme.refreshTheme();
                }).show();
            }
        });
        // 命令补全设置
        TextView tv_currentCPack = view.findViewById(R.id.tv_current_cpack);
        RelativeLayout btn_chooseCpack = view.findViewById(R.id.btn_choose_cpack);
        SwitchCompat isCheckingBySelection = view.findViewById(R.id.cb_is_checking_by_selection);
        SwitchCompat isHideWindowWhenCopying = view.findViewById(R.id.cb_is_hide_window_when_copying);
        SwitchCompat isSavingWhenPausing = view.findViewById(R.id.cb_is_saving_when_pausing);
        SwitchCompat isCrowed = view.findViewById(R.id.cb_is_crowed);
        SwitchCompat isSyntaxHighlight = view.findViewById(R.id.cb_is_syntax_highlight);
        List<String> showStrings = List.of(
                "正式版-原版-" + Settings.versionReleaseVanilla,
                "正式版-实验性玩法-" + Settings.versionReleaseExperiment,
                "测试版-原版-" + Settings.versionBetaVanilla,
                "测试版-实验性玩法-" + Settings.versionBetaExperiment,
                "中国版-原版-" + Settings.versionNeteaseVanilla,
                "中国版-实验性玩法-" + Settings.versionNeteaseExperiment
        );
        //noinspection SpellCheckingInspection
        String[] cpackPaths = {
                "release-vanilla",
                "release-experiment",
                "beta-vanilla",
                "beta-experiment",
                "netease-vanilla",
                "netease-experiment"
        };
        String cpackBranch = Settings.INSTANCE.getCpackBranch();
        for (int i = 0; i < cpackPaths.length; i++) {
            if (Objects.equals(cpackBranch, cpackPaths[i])) {
                tv_currentCPack.setText(context.getString(R.string.current_cpack, showStrings.get(i)));
                break;
            }
        }
        btn_chooseCpack.setOnClickListener(v -> new ChoosingDialog(context, showStrings, which -> {
            String cpackPath1 = cpackPaths[which];
            tv_currentCPack.setText(context.getString(R.string.current_cpack, showStrings.get(which)));
            Settings.INSTANCE.setCpackPath(cpackPath1);
        }).show());
        isCheckingBySelection.setChecked(Settings.INSTANCE.isCheckingBySelection);
        isCheckingBySelection.setOnCheckedChangeListener((buttonView, isChecked) -> Settings.INSTANCE.isCheckingBySelection = isChecked);
        isHideWindowWhenCopying.setChecked(Settings.INSTANCE.isHideWindowWhenCopying);
        isHideWindowWhenCopying.setOnCheckedChangeListener((buttonView, isChecked) -> Settings.INSTANCE.isHideWindowWhenCopying = isChecked);
        isSavingWhenPausing.setChecked(Settings.INSTANCE.isSavingWhenPausing);
        isSavingWhenPausing.setOnCheckedChangeListener((buttonView, isChecked) -> Settings.INSTANCE.isSavingWhenPausing = isChecked);
        isCrowed.setChecked(Settings.INSTANCE.isCrowed);
        isCrowed.setOnCheckedChangeListener((buttonView, isChecked) -> Settings.INSTANCE.isCrowed = isChecked);
        isSyntaxHighlight.setChecked(Settings.INSTANCE.isSyntaxHighlight);
        isSyntaxHighlight.setOnCheckedChangeListener((buttonView, isChecked) -> Settings.INSTANCE.isSyntaxHighlight = isChecked);
    }

    @Override
    protected String gePageName() {
        return "Settings";
    }

    @Override
    public void onPause() {
        super.onPause();
        // 保存设置到文件
        Settings.INSTANCE.save();
    }

}
