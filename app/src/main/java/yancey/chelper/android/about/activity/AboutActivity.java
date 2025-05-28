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

package yancey.chelper.android.about.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import yancey.chelper.R;
import yancey.chelper.android.common.activity.BaseActivity;
import yancey.chelper.android.common.util.AssetsUtil;

/**
 * 软件的关于界面
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected String gePageName() {
        return "About";
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_about;
    }

    @Override
    @SuppressLint("SetTextI18n")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ((TextView) findViewById(R.id.current_version)).setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
        findViewById(R.id.back).setOnClickListener(v -> finish());
        ((TextView) findViewById(R.id.author)).setText("Yancey");
        ((TextView) findViewById(R.id.qq_personal)).setText("1709185482");
        ((TextView) findViewById(R.id.qq_group)).setText("766625597");
        findViewById(R.id.btn_bilibili).setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://space.bilibili.com/470179011"))));
        findViewById(R.id.btn_core_source_code).setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Yancey2023/CHelper-Core"))));
        findViewById(R.id.btn_app_source_code).setOnClickListener(v ->
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Yancey2023/CHelper-Android"))));
        findViewById(R.id.btn_release_note).setOnClickListener(v -> {
            Intent intent = new Intent(this, ShowTextActivity.class);
            intent.putExtra(ShowTextActivity.TITLE, getString(R.string.release_note));
            intent.putExtra(ShowTextActivity.CONTENT, AssetsUtil.readStringFromAssets(this, "about/release_note.txt"));
            startActivity(intent);
        });
        findViewById(R.id.btn_privacy_policy).setOnClickListener(v -> {
            Intent intent = new Intent(this, ShowTextActivity.class);
            intent.putExtra(ShowTextActivity.TITLE, getString(R.string.privacy_policy));
            intent.putExtra(ShowTextActivity.CONTENT, AssetsUtil.readStringFromAssets(this, "about/privacy_policy.txt"));
            startActivity(intent);
        });
        findViewById(R.id.btn_open_source_terms).setOnClickListener(v -> {
            Intent intent = new Intent(this, ShowTextActivity.class);
            intent.putExtra(ShowTextActivity.TITLE, getString(R.string.open_source_terms));
            intent.putExtra(ShowTextActivity.CONTENT, AssetsUtil.readStringFromAssets(this, "about/open_source_terms.txt"));
            startActivity(intent);
        });
    }

}
