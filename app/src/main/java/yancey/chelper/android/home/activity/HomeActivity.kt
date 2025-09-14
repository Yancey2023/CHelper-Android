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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.compose.rememberNavController
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.toast.Toaster
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.functions.Functions
import io.reactivex.rxjava3.schedulers.Schedulers
import yancey.chelper.BuildConfig
import yancey.chelper.android.common.activity.BaseComposeActivity
import yancey.chelper.android.common.dialog.IsConfirmDialog
import yancey.chelper.android.common.dialog.PolicyGrantDialog
import yancey.chelper.android.common.style.CustomTheme
import yancey.chelper.android.common.util.FileUtil
import yancey.chelper.android.common.util.MonitorUtil
import yancey.chelper.android.common.util.PolicyGrantManager
import yancey.chelper.android.common.util.Settings
import yancey.chelper.android.completion.util.CompletionWindowManager
import yancey.chelper.network.ServiceManager
import yancey.chelper.ui.NavHost
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException
import java.lang.Boolean
import kotlin.plus

/**
 * 首页
 */
class HomeActivity : BaseComposeActivity() {
    private var getAnnouncement: Disposable? = null
    private var getLatestVersionInfo: Disposable? = null

    override val pageName = "Home"

    private var setBackGroundDrawable: Disposable? = null
    lateinit var onBackPressedCallback: OnBackPressedCallback
    lateinit var photoPicker: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (setBackGroundDrawable != null && !setBackGroundDrawable!!.isDisposed) {
                    IsConfirmDialog(this@HomeActivity)
                        .message("背景图片正在保存中，请稍候")
                        .show()
                }
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        photoPicker = registerForActivityResult(PickVisualMedia()) { setBackground(it) }
        setContent {
            NavHost(
                navController = rememberNavController(),
                chooseBackground = this::chooseBackground,
                restoreBackground = this::restoreBackground,
                onChooseTheme = this::refreshTheme,
            )
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
                    if (versionInfo.version_name != BuildConfig.VERSION_NAME) {
                        val ignoreVersionFile = File(dataDir, "ignore_version.txt")
                        val ignoreVersion = ignoreVersionFile.bufferedReader().use { it.readText() }
                        if (versionInfo.version_name != ignoreVersion) {
                            IsConfirmDialog(this, false)
                                .title("更新提醒")
                                .message(versionInfo.version_name + "版本已发布，欢迎下载体验。本次更新内容如下：\n" + versionInfo.changelog)
                                .onCancel("忽略此版本") {
                                    FileUtil.writeString(
                                        ignoreVersionFile,
                                        versionInfo.version_name
                                    )
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
        PolicyGrantDialog(this)
            .state(state)
            .onConfirm {
                this.showAnnouncement()
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

    private fun chooseBackground() {
        if (XXPermissions.isGrantedPermission(
                this,
                PermissionLists.getReadMediaImagesPermission()
            )
        ) {
            photoPicker.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ImageOnly)
                    .build()
            )
        } else {
            XXPermissions.with(this)
                .permission(PermissionLists.getReadMediaImagesPermission())
                .request { grantedList, deniedList ->
                    if (deniedList.isEmpty()) {
                        Toaster.show("图片访问权限申请成功")
                    } else {
                        Toaster.show("图片访问权限申请失败")
                    }
                }
        }
    }

    private fun setBackground(uri: Uri?) {
        setBackGroundDrawable?.dispose()
        if (uri == null) {
            return
        }
        setBackGroundDrawable =
            Observable.create { emitter ->
                val bitmap: Bitmap
                BufferedInputStream(contentResolver.openInputStream(uri))
                    .use { bitmap = BitmapFactory.decodeStream(it) }
                CustomTheme.INSTANCE.setBackGroundDrawableWithoutSave(bitmap)
                emitter.onNext(bitmap)
                CustomTheme.INSTANCE.setBackGroundDrawable(bitmap)
                emitter.onComplete()
            }.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { onBackPressedCallback.isEnabled = false }
                .subscribe(
                    { backgroundBitmap = it.asImageBitmap() },
                    { Toaster.show(it.message) }
                )
        onBackPressedCallback.isEnabled = true
    }

    private fun restoreBackground() {
        try {
            CustomTheme.INSTANCE.setBackGroundDrawable(null)
            backgroundBitmap = null
        } catch (e: IOException) {
            Toaster.show(e.message)
            MonitorUtil.generateCustomLog(e, "ResetBackgroundException")
        }
    }

}
