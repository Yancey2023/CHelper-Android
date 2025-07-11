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

package yancey.chelper.android.home.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hjq.toast.Toaster;

import java.io.File;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.schedulers.Schedulers;
import yancey.chelper.BuildConfig;
import yancey.chelper.R;
import yancey.chelper.android.about.activity.AboutActivity;
import yancey.chelper.android.about.activity.ShowTextActivity;
import yancey.chelper.android.common.activity.BaseActivity;
import yancey.chelper.android.common.activity.SettingsActivity;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.dialog.PolicyGrantDialog;
import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.android.common.util.PolicyGrantManager;
import yancey.chelper.android.common.util.Settings;
import yancey.chelper.android.completion.activity.CompletionActivity;
import yancey.chelper.android.completion.util.CompletionWindowManager;
import yancey.chelper.android.enumeration.activity.EnumerationActivity;
import yancey.chelper.android.favorites.activity.FavoritesActivity;
import yancey.chelper.android.library.activity.LocalLibraryListActivity;
import yancey.chelper.android.old2new.activity.Old2NewActivity;
import yancey.chelper.android.old2new.activity.Old2NewIMEGuideActivity;
import yancey.chelper.android.rawtext.activity.RawtextActivity;
import yancey.chelper.network.ServiceManager;

/**
 * 首页
 */
public class HomeActivity extends BaseActivity {

    private Disposable getAnnouncement, getLatestVersionInfo;

    @Override
    protected String gePageName() {
        return "Home";
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_home;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.btn_start_suggestion_app).setOnClickListener(v -> {
            if (CompletionWindowManager.INSTANCE.isUsingFloatingWindow()) {
                Toaster.show("你必须关闭悬浮窗模式才可以进入应用模式");
                return;
            }
            startActivity(new Intent(this, CompletionActivity.class));
        });
        findViewById(R.id.btn_start_enumeration_window).setOnClickListener(v -> {
            if (CompletionWindowManager.INSTANCE.isUsingFloatingWindow()) {
                CompletionWindowManager.INSTANCE.stopFloatingWindow();
            } else {
                CompletionWindowManager.INSTANCE.startFloatingWindow(this, 40);
            }
        });
        findViewById(R.id.btn_enumeration_settings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        findViewById(R.id.btn_start_old2new_app).setOnClickListener(v -> startActivity(new Intent(this, Old2NewActivity.class)));
        findViewById(R.id.btn_start_old2new_ime).setOnClickListener(v -> startActivity(new Intent(this, Old2NewIMEGuideActivity.class)));
        findViewById(R.id.btn_local_library).setOnClickListener(v -> startActivity(new Intent(this, LocalLibraryListActivity.class)));
//        findViewById(R.id.btn_public_library).setOnClickListener(v -> startActivity(new Intent(this, PublicLibraryListActivity.class)));
        findViewById(R.id.btn_raw_json_studio).setOnClickListener(v -> startActivity(new Intent(this, RawtextActivity.class)));
        findViewById(R.id.btn_enumeration).setOnClickListener(v -> startActivity(new Intent(this, EnumerationActivity.class)));
        findViewById(R.id.btn_favorite).setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));
        findViewById(R.id.btn_about).setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
        if (PolicyGrantManager.INSTANCE.getState() == PolicyGrantManager.State.AGREE) {
            showAnnouncement();
        }
    }

    private void showAnnouncement() {
        getAnnouncement = ServiceManager.CHELPER_SERVICE
                .getAnnouncement()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(announcement -> {
                    boolean isShow = true;
                    boolean isForce = Boolean.TRUE.equals(announcement.isForce);
                    if (!isForce) {
                        isShow = Boolean.TRUE.equals(announcement.isEnable);
                        if (isShow) {
                            File ignoreAnnouncementFile = new File(getDataDir(), "ignore_announcement.txt");
                            String ignoreAnnouncement = FileUtil.readString(ignoreAnnouncementFile);
                            String announcementHashCode = String.valueOf(announcement.hashCode());
                            if (Objects.equals(announcementHashCode, ignoreAnnouncement)) {
                                isShow = false;
                            }
                        }
                    }
                    if (isShow) {
                        new IsConfirmDialog(this, Boolean.TRUE.equals(announcement.isBigDialog))
                                .title(announcement.title)
                                .message(announcement.message)
                                .onCancel(isForce ? "取消" : "不再提醒", () -> FileUtil.writeString(
                                        new File(getDataDir(), "ignore_announcement.txt"),
                                        String.valueOf(announcement.hashCode())
                                ))
                                .onDismiss(this::checkUpdate)
                                .show();
                    } else {
                        checkUpdate();
                    }
                }, Functions.emptyConsumer());

    }

    public void checkUpdate() {
        if (Settings.INSTANCE.isEnableUpdateNotifications) {
            getLatestVersionInfo = ServiceManager.CHELPER_SERVICE
                    .getLatestVersionInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(versionInfo -> {
                        if (Objects.equals(versionInfo.version, BuildConfig.VERSION_NAME)) {
                            return;
                        }
                        File ignoreVersionFile = new File(getDataDir(), "ignore_version.txt");
                        String ignoreVersion = FileUtil.readString(ignoreVersionFile);
                        if (Objects.equals(versionInfo.version, ignoreVersion)) {
                            return;
                        }
                        new IsConfirmDialog(this, false)
                                .title("更新提醒")
                                .message(versionInfo.version + "版本已发布，欢迎下载体验。本次更新内容如下：\n" + versionInfo.changelog)
                                .onCancel("忽略此版本", () -> FileUtil.writeString(ignoreVersionFile, versionInfo.version))
                                .show();
                    }, Functions.emptyConsumer());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PolicyGrantManager.State state = PolicyGrantManager.INSTANCE.getState();
        if (state == PolicyGrantManager.State.AGREE) {
            return;
        }
        String message = null;
        if (state == PolicyGrantManager.State.NOT_READ) {
            message = "为保障您的权益，请阅读并同意《CHelper 隐私政策》，了解我们如何收集、使用您的信息。";
        } else if (state == PolicyGrantManager.State.UPDATED) {
            message = "我们更新了《CHelper 隐私政策》，请务必仔细阅读以清晰了解您的权利与数据处理规则变化。";
        }
        new PolicyGrantDialog(this)
                .message(message)
                .onRead(() -> {
                    Intent intent = new Intent(this, ShowTextActivity.class);
                    intent.putExtra(ShowTextActivity.TITLE, this.getString(R.string.privacy_policy));
                    intent.putExtra(ShowTextActivity.CONTENT, PolicyGrantManager.INSTANCE.getPrivatePolicy());
                    startActivity(intent);
                }).onConfirm(() -> {
                    PolicyGrantManager.INSTANCE.agree();
                    showAnnouncement();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CompletionWindowManager.INSTANCE.stopFloatingWindow();
        if (getAnnouncement != null) {
            getAnnouncement.dispose();
        }
        if (getLatestVersionInfo != null) {
            getLatestVersionInfo.dispose();
        }
    }

}
