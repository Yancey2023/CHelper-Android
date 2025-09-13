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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.toast.Toaster;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import yancey.chelper.R;
import yancey.chelper.android.common.dialog.ChoosingDialog;
import yancey.chelper.android.common.dialog.InputStringDialog;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.style.CustomTheme;
import yancey.chelper.android.common.util.MonitorUtil;
import yancey.chelper.android.common.util.Settings;

/**
 * 设置界面
 */
@SuppressLint("ViewConstructor")
public class SettingsView extends BaseView {

    private Disposable setBackGroundDrawable;

    public SettingsView(
            @NonNull FWSContext fwsContext,
            @Nullable BiFunction<ActivityResultContracts.PickVisualMedia, ActivityResultCallback<Uri>, ActivityResultLauncher<PickVisualMediaRequest>> registerForActivityResult
    ) {
        super(fwsContext, R.layout.layout_settings);
        // 页面顶部逻辑
        findViewById(R.id.back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        // 新版本提醒
        SwitchCompat isEnableUpdateNotification = view.findViewById(R.id.is_enable_update_notification);
        isEnableUpdateNotification.setChecked(Settings.INSTANCE.isEnableUpdateNotifications);
        isEnableUpdateNotification.setOnCheckedChangeListener((buttonView, isChecked) -> Settings.INSTANCE.isEnableUpdateNotifications = isChecked);
        // 自定义UI设置
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                if (!setBackGroundDrawable.isDisposed()) {
                    new IsConfirmDialog(context)
                            .message("背景图片正在保存中，请稍候")
                            .show();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
        ActivityResultLauncher<PickVisualMediaRequest> photoPicker = registerForActivityResult == null ? null :
                registerForActivityResult.apply(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (setBackGroundDrawable != null) {
                        setBackGroundDrawable.dispose();
                    }
                    if (uri == null) {
                        return;
                    }
                    setBackGroundDrawable = Observable
                            .<Bitmap>create(emitter -> {
                                Bitmap bitmap;
                                try (InputStream inputStream = new BufferedInputStream(context.getContentResolver().openInputStream(uri))) {
                                    bitmap = BitmapFactory.decodeStream(inputStream);
                                }
                                CustomTheme.INSTANCE.setBackGroundDrawableWithoutSave(bitmap);
                                emitter.onNext(bitmap);
                                CustomTheme.INSTANCE.setBackGroundDrawable(bitmap);
                                emitter.onComplete();
                            }).subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally(() -> onBackPressedCallback.setEnabled(false))
                            .subscribe(
                                    bitmap -> CustomTheme.INSTANCE.invokeBackgroundForce(findViewById(R.id.main)),
                                    throwable -> Toaster.show(throwable.getMessage())
                            );
                    onBackPressedCallback.setEnabled(true);
                });
        Runnable backgroundPicker = photoPicker == null ? null :
                () -> photoPicker.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build());
        RelativeLayout chooseBackground = view.findViewById(R.id.choose_background);
        chooseBackground.setOnClickListener(v -> {
            if (getEnvironment() != Environment.APPLICATION) {
                Toaster.show("不支持在悬浮窗模式设置背景");
            } else if (backgroundPicker == null) {
                Toaster.show("当前场景不支持设置背景");
            } else if (XXPermissions.isGrantedPermission(context, PermissionLists.getReadMediaImagesPermission())) {
                backgroundPicker.run();
            } else {
                XXPermissions.with(context)
                        .permission(PermissionLists.getReadMediaImagesPermission())
                        .request((grantedList, deniedList) -> {
                            if (deniedList.isEmpty()) {
                                Toaster.show("图片访问权限申请成功");
                            } else {
                                Toaster.show("图片访问权限申请失败");
                            }
                        });
            }
        });
        RelativeLayout restoreBackground = view.findViewById(R.id.restore_background);
        restoreBackground.setOnClickListener(v -> {
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
        RelativeLayout chooseTheme = view.findViewById(R.id.choose_theme);
        chooseTheme.setOnClickListener(v -> {
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
        RelativeLayout floatingWindowAlpha = view.findViewById(R.id.floating_window_alpha);
        floatingWindowAlpha.setOnClickListener(v -> new InputStringDialog(context)
                .title("请输入透明度")
                .defaultInput(String.valueOf((int) (Settings.INSTANCE.floatingWindowAlpha * 100)))
                .onConfirm(s -> {
                    try {
                        int integer = Integer.parseInt(s);
                        if (integer < 10) {
                            integer = 10;
                        } else if (integer > 100) {
                            integer = 100;
                        }
                        Settings.INSTANCE.floatingWindowAlpha = integer / 100f;
                    } catch (NumberFormatException ignored) {

                    }
                })
                .show()
        );
        RelativeLayout floatingWindowSize = view.findViewById(R.id.floating_window_size);
        floatingWindowSize.setOnClickListener(v -> new InputStringDialog(context)
                .title("请输入大小")
                .defaultInput(String.valueOf(Settings.INSTANCE.floatingWindowSize))
                .onConfirm(s -> {
                    try {
                        int integer = Integer.parseInt(s);
                        if (integer < 10) {
                            integer = 10;
                        } else if (integer > 100) {
                            integer = 100;
                        }
                        Settings.INSTANCE.floatingWindowSize = integer;
                    } catch (NumberFormatException ignored) {

                    }
                })
                .show()
        );
        // 命令补全设置
        TextView currentCPack = view.findViewById(R.id.tv_current_cpack);
        RelativeLayout chooseCpack = view.findViewById(R.id.choose_cpack);
        SwitchCompat isCheckingBySelection = view.findViewById(R.id.is_checking_by_selection);
        SwitchCompat isHideWindowWhenCopying = view.findViewById(R.id.is_hide_window_when_copying);
        SwitchCompat isSavingWhenPausing = view.findViewById(R.id.is_saving_when_pausing);
        SwitchCompat isCrowed = view.findViewById(R.id.is_crowed);
        SwitchCompat isShowErrorReason = view.findViewById(R.id.is_show_error_reason);
        SwitchCompat isSyntaxHighlight = view.findViewById(R.id.is_syntax_highlight);
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
                currentCPack.setText(context.getString(R.string.layout_settings_current_cpack, showStrings.get(i)));
                break;
            }
        }
        chooseCpack.setOnClickListener(v -> new ChoosingDialog(context, showStrings, which -> {
            String cpackPath1 = cpackPaths[which];
            currentCPack.setText(context.getString(R.string.layout_settings_current_cpack, showStrings.get(which)));
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
        isShowErrorReason.setChecked(Settings.INSTANCE.isShowErrorReason);
        isShowErrorReason.setOnCheckedChangeListener((buttonView, isChecked) -> Settings.INSTANCE.isShowErrorReason = isChecked);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (setBackGroundDrawable != null) {
            setBackGroundDrawable.dispose();
        }
    }
}
