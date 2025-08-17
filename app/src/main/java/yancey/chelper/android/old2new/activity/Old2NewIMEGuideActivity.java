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

package yancey.chelper.android.old2new.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;

import yancey.chelper.R;
import yancey.chelper.android.common.activity.BaseActivity;
import yancey.chelper.android.common.dialog.IsConfirmDialog;

/**
 * 旧命令转新命令输入法引导界面
 */
public class Old2NewIMEGuideActivity extends BaseActivity {

    @Override
    protected String gePageName() {
        return "Old2NewIMEGuide";
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_old2new_ime_guide;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.back).setOnClickListener(v -> finish());
        findViewById(R.id.btn_start_ime_step1).setOnClickListener(v -> {
            if ((checkFeatureInputMethods())) {
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
            }
        });
        findViewById(R.id.btn_start_ime_step2).setOnClickListener(v -> {
            if (checkFeatureInputMethods()) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
            }
        });
    }

    private boolean checkFeatureInputMethods() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_INPUT_METHODS)) {
            return true;
        } else {
            new IsConfirmDialog(getApplicationContext(), false)
                    .message(getString(R.string.layout_old2new_ime_guide_not_support))
                    .show();
            return false;
        }
    }

}
