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

package yancey.chelper.android.common.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.hjq.toast.Toaster;

import java.io.BufferedInputStream;
import java.io.InputStream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import yancey.chelper.R;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.style.CustomTheme;
import yancey.chelper.android.common.view.SettingsView;
import yancey.chelper.fws.activity.FWSActivity;
import yancey.chelper.fws.view.FWSView;

/**
 * 设置界面
 */
public class SettingsActivity extends FWSActivity<SettingsView> {

    private Disposable setBackGroundDrawable;

    @Override
    protected SettingsView createView(@NonNull FWSView.CustomContext customContext) {
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                if (!setBackGroundDrawable.isDisposed()) {
                    new IsConfirmDialog(SettingsActivity.this)
                            .message("背景图片正在保存中，请稍候")
                            .show();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
        ActivityResultLauncher<PickVisualMediaRequest> photoPicker = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (setBackGroundDrawable != null) {
                setBackGroundDrawable.dispose();
            }
            if (uri == null) {
                return;
            }
            setBackGroundDrawable = Observable
                    .<Bitmap>create(emitter -> {
                        Bitmap bitmap;
                        try (InputStream inputStream = new BufferedInputStream(getContentResolver().openInputStream(uri))) {
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
        Runnable backgroundPicker = () -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            photoPicker.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        };
        return new SettingsView(customContext, backgroundPicker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (setBackGroundDrawable != null) {
            setBackGroundDrawable.dispose();
        }
    }
}