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

package yancey.chelper.android.welcome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.Toaster;
import com.hjq.window.EasyWindow;
import com.hjq.window.draggable.MovingWindowDraggableRule;

import java.io.File;
import java.util.List;
import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.about.activity.AboutActivity;
import yancey.chelper.android.about.activity.ShowTextActivity;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.dialog.PrivacyPolicyDialog;
import yancey.chelper.android.common.util.AssetsUtil;
import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.android.common.view.FloatingMainView;
import yancey.chelper.android.completion.activity.CompletionActivity;
import yancey.chelper.android.completion.activity.SettingsActivity;
import yancey.chelper.android.enumeration.activity.EnumerationActivity;
import yancey.chelper.android.favorites.activity.FavoritesActivity;
import yancey.chelper.android.old2new.activity.Old2NewActivity;
import yancey.chelper.android.old2new.activity.Old2NewIMEGuideActivity;
import yancey.chelper.android.rawtext.activity.RawtextActivity;

/**
 * 欢迎界面 + 悬浮窗管理
 */
public class WelcomeActivity extends AppCompatActivity {

    private EasyWindow<?> mainViewWindow, iconViewWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets stateBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(stateBars.left, stateBars.top, stateBars.right, stateBars.bottom);
            return insets;
        });
        findViewById(R.id.btn_start_suggestion_app).setOnClickListener(v -> {
            if (isUsingFloatingWindow()) {
                Toaster.show("你必须关闭悬浮窗模式才可以进入应用模式");
                return;
            }
            startActivity(new Intent(this, CompletionActivity.class));
        });
        findViewById(R.id.btn_start_enumeration_window).setOnClickListener(v -> {
            if (isUsingFloatingWindow()) {
                stopFloatingWindow();
            } else {
                startFloatingWindow(40, true);
            }
        });
        findViewById(R.id.btn_enumeration_settings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        findViewById(R.id.btn_start_old2new_app).setOnClickListener(v -> startActivity(new Intent(this, Old2NewActivity.class)));
        findViewById(R.id.btn_start_old2new_ime).setOnClickListener(v -> startActivity(new Intent(this, Old2NewIMEGuideActivity.class)));
        findViewById(R.id.btn_public_library).setOnClickListener(v -> Toaster.show("命令库将会在后续版本中开放，敬请期待！"));
//        findViewById(R.id.btn_public_library).setOnClickListener(v -> startActivity(new Intent(this, PublicLibraryListActivity.class)));
        findViewById(R.id.btn_raw_json_studio).setOnClickListener(v -> startActivity(new Intent(this, RawtextActivity.class)));
        findViewById(R.id.btn_enumeration).setOnClickListener(v -> startActivity(new Intent(this, EnumerationActivity.class)));
        findViewById(R.id.btn_favorite).setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.btn_about).setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        String privacyPolicy = AssetsUtil.readStringFromAssets(this, "about/privacy_policy.txt");
        String privacyPolicyHashStr = String.valueOf(privacyPolicy.hashCode());
        File lastReadContent = new File(getDataDir(), "lastReadContent.txt");
        String message = null;
        if (!lastReadContent.exists()) {
            message = "为保障您的权益，请阅读并同意《CHelper 隐私政策》，了解我们如何收集、使用您的信息。";
        } else {
            String lastRead = FileUtil.readString(lastReadContent);
            if (!Objects.equals(privacyPolicyHashStr, lastRead)) {
                message = "我们更新了《CHelper 隐私政策》，请务必仔细阅读以清晰了解您的权利与数据处理规则变化。";
            }
        }
        if (message != null) {
            new PrivacyPolicyDialog(this)
                    .message(message)
                    .onRead(() -> {
                        Intent intent = new Intent(this, ShowTextActivity.class);
                        intent.putExtra(ShowTextActivity.TITLE, getString(R.string.privacy_policy));
                        intent.putExtra(ShowTextActivity.CONTENT, privacyPolicy);
                        startActivity(intent);
                    }).onConfirm(() -> FileUtil.writeString(lastReadContent, privacyPolicyHashStr))
                    .show();
        }
    }

    /**
     * 是否正在使用悬浮窗
     *
     * @return 是否正在使用悬浮窗
     */
    private boolean isUsingFloatingWindow() {
        return iconViewWindow != null;
    }

    /**
     * 开启悬浮窗
     *
     * @param iconSize                            图标大小
     * @param isShowXiaomiClipboardPermissionTips 是否为小米用户或红米用户显示剪切板权限提示
     */
    @SuppressWarnings("SameParameterValue")
    private void startFloatingWindow(int iconSize, boolean isShowXiaomiClipboardPermissionTips) {
        if (!XXPermissions.isGranted(this, Permission.SYSTEM_ALERT_WINDOW)) {
            new IsConfirmDialog(this, false)
                    .message("需要悬浮窗权限，请进入设置进行授权")
                    .onConfirm("打开设置", () -> XXPermissions.with(this)
                            .permission(Permission.SYSTEM_ALERT_WINDOW)
                            .request(new OnPermissionCallback() {
                                @Override
                                public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                                    Toaster.show("悬浮窗权限获取成功");
                                }

                                @Override
                                public void onDenied(@NonNull List<String> permissions, boolean doNotAskAgain) {
                                    Toaster.show("悬浮窗权限获取失败");
                                }
                            })).show();
            return;
        }
//        if (isShowXiaomiClipboardPermissionTips && RomUtils.isXiaomi()) {
        if (isShowXiaomiClipboardPermissionTips) {
            File file = FileUtil.getFile(getDataDir(), "xiaomi_clipboard_permission_no_tips.txt");
            if (!file.exists()) {
                new IsConfirmDialog(this, false)
                        .message("对于小米手机和红米手机，需要将写入剪切板权限设置为始终允许才能在悬浮窗复制文本。具体设置方式如下：设置-应用设置-权限管理-应用权限管理-CHelper-写入剪切板-始终允许。")
                        .onConfirm(() -> startFloatingWindow(iconSize, false))
                        .onCancel("不再提示", () -> {
                            FileUtil.writeString(file, "");
                            startFloatingWindow(iconSize, false);
                        })
                        .show();
                return;
            }
        }
        int length = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                iconSize,
                getResources().getDisplayMetrics()
        );
        FloatingMainView floatingMainView = new FloatingMainView(
                this,
                this::stopFloatingWindow,
                length
        );
        mainViewWindow = EasyWindow.with(getApplication())
                .setContentView(floatingMainView)
                .setWidth(WindowManager.LayoutParams.MATCH_PARENT)
                .setHeight(WindowManager.LayoutParams.MATCH_PARENT)
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                .setAnimStyle(0);
        Runnable hide = () -> {
            iconViewWindow.setXOffset((int) floatingMainView.getIconViewX());
            iconViewWindow.setYOffset((int) floatingMainView.getIconViewY());
            mainViewWindow.cancel();
            floatingMainView.onPause();
        };
        floatingMainView.setOnIconClickListener(hide);
        ImageView iconView = new ImageView(this);
        iconView.setImageResource(R.drawable.pack_icon);
        iconView.setLayoutParams(new FrameLayout.LayoutParams(length, length, Gravity.START | Gravity.TOP));
        iconView.setOnClickListener(v -> {
            mainViewWindow.show();
            WindowManager.LayoutParams layoutParam = iconViewWindow.getWindowParams();
            floatingMainView.setIconPosition(layoutParam.x, layoutParam.y);
            floatingMainView.onResume();
        });
        iconViewWindow = EasyWindow.with(getApplication())
                .setContentView(iconView)
                .setWindowDraggableRule(new MovingWindowDraggableRule())
                .setOutsideTouchable(true)
                .setGravity(Gravity.START | Gravity.TOP)
                .setAnimStyle(0);
        iconViewWindow.show();
    }

    /**
     * 关闭悬浮窗
     */
    private void stopFloatingWindow() {
        if (mainViewWindow != null) {
            FloatingMainView floatingMainView = (FloatingMainView) mainViewWindow.getContentView();
            if (floatingMainView != null) {
                floatingMainView.onPause();
                floatingMainView.onDestroy();
            }
            mainViewWindow.recycle();
            mainViewWindow = null;
        }
        if (iconViewWindow != null) {
            iconViewWindow.recycle();
            iconViewWindow = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopFloatingWindow();
    }

}
