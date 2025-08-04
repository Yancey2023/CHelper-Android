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

package yancey.chelper.android.home.activity

import android.content.Intent
import android.os.Bundle
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import yancey.chelper.BuildConfig
import yancey.chelper.R
import yancey.chelper.android.about.activity.ShowTextActivity
import yancey.chelper.android.common.activity.BaseComposeActivity
import yancey.chelper.android.common.dialog.IsConfirmDialog
import yancey.chelper.android.common.dialog.PolicyGrantDialog
import yancey.chelper.android.common.util.FileUtil
import yancey.chelper.android.common.util.PolicyGrantManager
import yancey.chelper.android.common.util.Settings
import yancey.chelper.android.completion.util.CompletionWindowManager
import yancey.chelper.network.ServiceManager
import yancey.chelper.ui.HomeScreen
import java.io.File
import java.lang.Boolean
import kotlin.String
import kotlin.plus

/**
 * 首页
 */
class HomeActivity : BaseComposeActivity() {
    private var getAnnouncement: Disposable? = null
    private var getLatestVersionInfo: Disposable? = null

    override val pageName = "Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreen()
        }
        if (PolicyGrantManager.INSTANCE.state == PolicyGrantManager.State.AGREE) {
            showAnnouncement()
        }
    }

    private fun showAnnouncement() {
        getAnnouncement = ServiceManager.CHELPER_SERVICE
            .getAnnouncement()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ announcement ->
                var isShow = true
                val isForce = Boolean.TRUE == announcement.isForce
                if (!isForce) {
                    isShow = Boolean.TRUE == announcement.isEnable
                    if (isShow) {
                        val ignoreAnnouncementFile = File(dataDir, "ignore_announcement.txt")
                        val ignoreAnnouncement =
                            ignoreAnnouncementFile.inputStream().bufferedReader()
                                .use { it.readText() }
                        val announcementHashCode = announcement.hashCode().toString()
                        if (announcementHashCode == ignoreAnnouncement) {
                            isShow = false
                        }
                    }
                }
                if (isShow) {
                    IsConfirmDialog(this, Boolean.TRUE == announcement.isBigDialog)
                        .title(announcement.title)
                        .message(announcement.message)
                        .onCancel(if (isForce) "取消" else "不再提醒") {
                            FileUtil.writeString(
                                File(dataDir, "ignore_announcement.txt"),
                                announcement.hashCode().toString()
                            )
                        }
                        .onDismiss(this::checkUpdate)
                        .show()
                } else {
                    checkUpdate()
                }
            }, Functions.emptyConsumer())
    }

    fun checkUpdate() {
        if (Settings.INSTANCE.isEnableUpdateNotifications) {
            getLatestVersionInfo = ServiceManager.CHELPER_SERVICE
                .getLatestVersionInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ versionInfo ->
                    if (versionInfo.version != BuildConfig.VERSION_NAME) {
                        val ignoreVersionFile = File(dataDir, "ignore_version.txt")
                        val ignoreVersion = ignoreVersionFile.bufferedReader().use { it.readText() }
                        if (versionInfo.version != ignoreVersion) {
                            IsConfirmDialog(this, false)
                                .title("更新提醒")
                                .message(versionInfo.version + "版本已发布，欢迎下载体验。本次更新内容如下：\n" + versionInfo.changelog)
                                .onCancel("忽略此版本") {
                                    FileUtil.writeString(ignoreVersionFile, versionInfo.version)
                                }
                                .show()
                        }
                    }
                }, Functions.emptyConsumer())
        }
    }

    override fun onResume() {
        super.onResume()
        val state = PolicyGrantManager.INSTANCE.state
        if (state == PolicyGrantManager.State.AGREE) {
            return
        }
        var message: String? = null
        if (state == PolicyGrantManager.State.NOT_READ) {
            message = "为保障您的权益，请阅读并同意《CHelper 隐私政策》，了解我们如何收集、使用您的信息。"
        } else if (state == PolicyGrantManager.State.UPDATED) {
            message =
                "我们更新了《CHelper 隐私政策》，请务必仔细阅读以清晰了解您的权利与数据处理规则变化。"
        }
        PolicyGrantDialog(this)
            .message(message)
            .onRead {
                val intent = Intent(this, ShowTextActivity::class.java)
                intent.putExtra(ShowTextActivity.TITLE, this.getString(R.string.privacy_policy))
                intent.putExtra(
                    ShowTextActivity.CONTENT,
                    PolicyGrantManager.INSTANCE.privatePolicy
                )
                startActivity(intent)
            }.onConfirm {
                PolicyGrantManager.INSTANCE.agree()
                showAnnouncement()
            }
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        CompletionWindowManager.INSTANCE.stopFloatingWindow()
        if (getAnnouncement != null) {
            getAnnouncement!!.dispose()
        }
        if (getLatestVersionInfo != null) {
            getLatestVersionInfo!!.dispose()
        }
    }

}
