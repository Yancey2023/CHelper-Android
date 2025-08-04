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

package yancey.chelper.android.common.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.activity.viewModels
import androidx.compose.ui.graphics.asImageBitmap
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.permission.PermissionLists
import com.hjq.permissions.permission.base.IPermission
import com.hjq.toast.Toaster
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import yancey.chelper.android.common.dialog.ChoosingDialog
import yancey.chelper.android.common.dialog.IsConfirmDialog
import yancey.chelper.android.common.style.CustomTheme
import yancey.chelper.android.common.util.MonitorUtil
import yancey.chelper.android.common.util.Settings
import yancey.chelper.ui.settings.SettingsScreen
import yancey.chelper.ui.settings.SettingsViewModel
import java.io.BufferedInputStream
import java.io.IOException

/**
 * 设置界面
 */
class SettingsActivity : BaseComposeActivity() {

    override val pageName = "Settings"
    private var setBackGroundDrawable: Disposable? = null

    val viewModel: SettingsViewModel by viewModels()

    lateinit var onBackPressedCallback: OnBackPressedCallback
    lateinit var photoPicker: ActivityResultLauncher<PickVisualMediaRequest>
    lateinit var cpackBranches: Array<String>
    lateinit var cpackBranchTranslations: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isEnableUpdateNotifications = Settings.INSTANCE.isEnableUpdateNotifications
        viewModel.isCheckingBySelection = Settings.INSTANCE.isCheckingBySelection
        viewModel.isHideWindowWhenCopying = Settings.INSTANCE.isHideWindowWhenCopying
        viewModel.isSavingWhenPausing = Settings.INSTANCE.isSavingWhenPausing
        viewModel.isCrowed = Settings.INSTANCE.isCrowed
        viewModel.isShowErrorReason = Settings.INSTANCE.isShowErrorReason
        viewModel.isSyntaxHighlight = Settings.INSTANCE.isSyntaxHighlight
        cpackBranches = arrayOf(
            "release-vanilla",
            "release-experiment",
            "beta-vanilla",
            "beta-experiment",
            "netease-vanilla",
            "netease-experiment"
        )
        cpackBranchTranslations = arrayOf(
            "正式版-原版-" + Settings.versionReleaseVanilla,
            "正式版-实验性玩法-" + Settings.versionReleaseExperiment,
            "测试版-原版-" + Settings.versionBetaVanilla,
            "测试版-实验性玩法-" + Settings.versionBetaExperiment,
            "中国版-原版-" + Settings.versionNeteaseVanilla,
            "中国版-实验性玩法-" + Settings.versionNeteaseExperiment
        )
        val cpackBranch = Settings.INSTANCE.cpackBranch
        for (i in cpackBranches.indices) {
            if (cpackBranch == cpackBranches[i]) {
                viewModel.currentCpackBranchTranslation = cpackBranchTranslations[i]
                break
            }
        }
        onBackPressedCallback = object : OnBackPressedCallback(false) {
            override fun handleOnBackPressed() {
                if (setBackGroundDrawable != null && !setBackGroundDrawable!!.isDisposed) {
                    IsConfirmDialog(this@SettingsActivity)
                        .message("背景图片正在保存中，请稍候")
                        .show()
                }
            }
        }
        onBackPressedDispatcher.addCallback(onBackPressedCallback)
        photoPicker = registerForActivityResult(PickVisualMedia()) { setBackground(it) }
        setContent {
            SettingsScreen(
                viewModel = viewModel,
                chooseCpack = this::chooseCpack,
                chooseBackground = this::chooseBackground,
                restoreBackground = this::restoreBackground,
                chooseTheme = this::chooseTheme,
            )
        }
    }

    override fun onPause() {
        super.onPause()
        Settings.INSTANCE.isEnableUpdateNotifications = viewModel.isEnableUpdateNotifications
        Settings.INSTANCE.isCheckingBySelection = viewModel.isCheckingBySelection
        Settings.INSTANCE.isHideWindowWhenCopying = viewModel.isHideWindowWhenCopying
        Settings.INSTANCE.isSavingWhenPausing = viewModel.isSavingWhenPausing
        Settings.INSTANCE.isCrowed = viewModel.isCrowed
        Settings.INSTANCE.isShowErrorReason = viewModel.isShowErrorReason
        Settings.INSTANCE.isSyntaxHighlight = viewModel.isSyntaxHighlight
        Settings.INSTANCE.save()
    }

    private fun chooseCpack() {
        ChoosingDialog(this, cpackBranchTranslations) { which: Int ->
            viewModel.currentCpackBranchTranslation = cpackBranchTranslations[which]
            Settings.INSTANCE.cpackBranch = cpackBranches[which]
        }.show()
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
                .request(object : OnPermissionCallback {
                    override fun onGranted(
                        permissions: MutableList<IPermission?>,
                        allGranted: Boolean
                    ) {
                        Toaster.show("图片访问权限申请成功")
                    }

                    override fun onDenied(
                        permissions: MutableList<IPermission?>,
                        doNotAskAgain: Boolean
                    ) {
                        Toaster.show("图片访问权限申请失败")
                    }
                })
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
        IsConfirmDialog(this, false)
            .message("是否恢复背景？")
            .onConfirm {
                try {
                    CustomTheme.INSTANCE.setBackGroundDrawable(null)
                    backgroundBitmap = null
                } catch (e: IOException) {
                    Toaster.show(e.message)
                    MonitorUtil.generateCustomLog(e, "ResetBackgroundException")
                }
            }.show()
    }

    private fun chooseTheme() {
        ChoosingDialog(this, arrayOf("浅色模式", "深色模式", "跟随系统")) {
            Settings.INSTANCE.themeId = when (it) {
                0 -> "MODE_NIGHT_NO"
                1 -> "MODE_NIGHT_YES"
                2 -> "MODE_NIGHT_FOLLOW_SYSTEM"
                else -> throw IllegalStateException("Unexpected value: $it")
            }
            CustomTheme.refreshTheme()
            refreshTheme()
        }.show()
    }

}