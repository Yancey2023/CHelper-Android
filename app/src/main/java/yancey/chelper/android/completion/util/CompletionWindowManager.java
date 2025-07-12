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

package yancey.chelper.android.completion.util;

import android.app.Application;
import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.permissions.permission.PermissionLists;
import com.hjq.permissions.permission.base.IPermission;
import com.hjq.toast.Toaster;
import com.hjq.window.EasyWindow;
import com.hjq.window.draggable.MovingWindowDraggableRule;

import java.io.File;
import java.util.List;

import yancey.chelper.R;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.android.common.util.RomUtils;
import yancey.chelper.android.completion.view.CompletionView;
import yancey.chelper.fws.view.FWSMainView;
import yancey.chelper.fws.view.FWSView;

/**
 * 悬浮窗管理
 */
public class CompletionWindowManager {

    public static CompletionWindowManager INSTANCE;

    private final @NonNull Application application;
    private final @NonNull File xiaomiClipboardPermissionTipsFile;
    private @Nullable EasyWindow<?> mainViewWindow, iconViewWindow;
    private boolean isShowXiaomiClipboardPermissionTips;


    private CompletionWindowManager(@NonNull Application application, @NonNull File xiaomiClipboardPermissionTipsFile) {
        this.application = application;
        this.xiaomiClipboardPermissionTipsFile = xiaomiClipboardPermissionTipsFile;
        this.isShowXiaomiClipboardPermissionTips = RomUtils.isXiaomi() && !xiaomiClipboardPermissionTipsFile.exists();
    }

    /**
     * 初始化
     */
    public static void init(Application application, File xiaomiClipboardPermissionTipsFile) {
        INSTANCE = new CompletionWindowManager(application, xiaomiClipboardPermissionTipsFile);
    }

    /**
     * 是否正在使用悬浮窗
     *
     * @return 是否正在使用悬浮窗
     */
    public boolean isUsingFloatingWindow() {
        return iconViewWindow != null;
    }

    /**
     * 开启悬浮窗
     *
     * @param context  上下文
     * @param iconSize 图标大小
     */
    @SuppressWarnings({"deprecation", "RedundantSuppression", "SameParameterValue"})
    public void startFloatingWindow(Context context, int iconSize) {
        startFloatingWindow(context, iconSize, isShowXiaomiClipboardPermissionTips);
    }

    /**
     * 开启悬浮窗
     *
     * @param context                             上下文
     * @param iconSize                            图标大小
     * @param isShowXiaomiClipboardPermissionTips 是否为小米用户或红米用户显示剪切板权限提示
     */
    @SuppressWarnings({"deprecation", "RedundantSuppression", "SameParameterValue"})
    private void startFloatingWindow(Context context, int iconSize, boolean isShowXiaomiClipboardPermissionTips) {
        if (!XXPermissions.isGrantedPermission(context, PermissionLists.getSystemAlertWindowPermission())) {
            new IsConfirmDialog(context, false)
                    .message("需要悬浮窗权限，请进入设置进行授权")
                    .onConfirm("打开设置", () -> XXPermissions.with(context)
                            .permission(PermissionLists.getSystemAlertWindowPermission())
                            .request(new OnPermissionCallback() {
                                @Override
                                public void onGranted(@NonNull List<IPermission> permissions, boolean allGranted) {
                                    Toaster.show("悬浮窗权限获取成功");
                                }

                                @Override
                                public void onDenied(@NonNull List<IPermission> permissions, boolean doNotAskAgain) {
                                    Toaster.show("悬浮窗权限获取失败");
                                }
                            })).show();
            return;
        }
        if (isShowXiaomiClipboardPermissionTips) {
            new IsConfirmDialog(context, false)
                    .message("对于小米手机和红米手机，需要将写入剪切板权限设置为始终允许才能在悬浮窗复制文本。具体设置方式如下：设置-应用设置-权限管理-应用权限管理-CHelper-写入剪切板-始终允许。")
                    .onConfirm(() -> startFloatingWindow(context, iconSize, false))
                    .onCancel("不再提示", () -> {
                        this.isShowXiaomiClipboardPermissionTips = false;
                        FileUtil.writeString(xiaomiClipboardPermissionTipsFile, "");
                        startFloatingWindow(context, iconSize, false);
                    })
                    .show();
            return;
        }
        int length = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                iconSize,
                application.getResources().getDisplayMetrics()
        );
        ImageView iconView = new ImageView(context);
        iconView.setImageResource(R.drawable.pack_icon);
        iconView.setLayoutParams(new FrameLayout.LayoutParams(length, length, Gravity.START | Gravity.TOP));
        FWSMainView<CompletionView> fwsMainView = new FWSMainView<>(
                context,
                FWSView.Environment.FLOATING_WINDOW,
                customContext -> new CompletionView(customContext, this::stopFloatingWindow, iconView::callOnClick),
                new OnBackPressedDispatcher(iconView::callOnClick)
        );
        iconViewWindow = EasyWindow.with(application)
                .setContentView(iconView)
                .setWindowDraggableRule(new MovingWindowDraggableRule())
                .setOutsideTouchable(true)
                .setWindowLocation(Gravity.START | Gravity.TOP, 0, 0)
                .setWindowAnim(0);
        mainViewWindow = EasyWindow.with(application)
                .setContentView(fwsMainView)
                .setWindowSize(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                .removeWindowFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                .setSystemUiVisibility(fwsMainView.getSystemUiVisibility()
                                       | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                       | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                       | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                .setWindowAnim(0);
        iconView.setOnClickListener(v -> {
            if (mainViewWindow == null) {
                return;
            }
            if (mainViewWindow.getWindowViewVisibility() == View.VISIBLE) {
                fwsMainView.onPause();
                mainViewWindow.setWindowViewVisibility(View.INVISIBLE);
            } else {
                mainViewWindow.setWindowViewVisibility(View.VISIBLE);
                fwsMainView.onResume();
            }
        });
        if (mainViewWindow != null && iconViewWindow != null) {
            mainViewWindow.setWindowViewVisibility(View.INVISIBLE);
            mainViewWindow.show();
            iconViewWindow.show();
        } else {
            stopFloatingWindow();
        }
    }

    /**
     * 关闭悬浮窗
     */
    public void stopFloatingWindow() {
        if (mainViewWindow != null) {
            FWSMainView<?> FWSFloatingMainView = (FWSMainView<?>) mainViewWindow.getContentView();
            if (FWSFloatingMainView != null) {
                FWSFloatingMainView.onPause();
                FWSFloatingMainView.onDestroy();
            }
            mainViewWindow.recycle();
            mainViewWindow = null;
        }
        if (iconViewWindow != null) {
            iconViewWindow.recycle();
            iconViewWindow = null;
        }
    }

}
