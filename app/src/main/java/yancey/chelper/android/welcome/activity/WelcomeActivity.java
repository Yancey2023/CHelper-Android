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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hjq.toast.Toaster;

import java.io.File;
import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.about.activity.AboutActivity;
import yancey.chelper.android.about.activity.ShowTextActivity;
import yancey.chelper.android.common.activity.SettingsActivity;
import yancey.chelper.android.common.dialog.PrivacyPolicyDialog;
import yancey.chelper.android.common.style.CustomTheme;
import yancey.chelper.android.common.util.AssetsUtil;
import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.completion.activity.CompletionActivity;
import yancey.chelper.android.completion.util.CompletionWindowManager;
import yancey.chelper.android.enumeration.activity.EnumerationActivity;
import yancey.chelper.android.favorites.activity.FavoritesActivity;
import yancey.chelper.android.old2new.activity.Old2NewActivity;
import yancey.chelper.android.old2new.activity.Old2NewIMEGuideActivity;
import yancey.chelper.android.rawtext.activity.RawtextActivity;

/**
 * 欢迎界面
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets stateBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(stateBars.left, stateBars.top, stateBars.right, stateBars.bottom);
            return insets;
        });
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
                CompletionWindowManager.INSTANCE.startFloatingWindow(this, 40, true);
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
        CustomTheme.INSTANCE.invokeBackground(findViewById(R.id.main), CustomView.Environment.APPLICATION);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CompletionWindowManager.INSTANCE.stopFloatingWindow();
    }

}
