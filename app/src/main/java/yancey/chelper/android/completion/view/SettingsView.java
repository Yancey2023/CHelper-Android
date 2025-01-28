/**
 * It is part of CHelper. CHelper a command helper for Minecraft Bedrock Edition.
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

package yancey.chelper.android.completion.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import java.util.Objects;
import java.util.function.Consumer;

import yancey.chelper.R;
import yancey.chelper.android.common.dialog.ChoosingDialog;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.completion.data.Settings;

/**
 * 设置界面
 */
@SuppressLint("ViewConstructor")
public class SettingsView extends CustomView {

    public SettingsView(@NonNull Context context, @NonNull Consumer<CustomView> openView, @NonNull Environment environment) {
        super(context, openView, environment, R.layout.layout_settings);
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        TextView tv_currentCPack = view.findViewById(R.id.tv_current_cpack);
        RelativeLayout btn_chooseCpack = view.findViewById(R.id.btn_choose_cpack);
        SwitchCompat isCheckingBySelection = view.findViewById(R.id.cb_is_checking_by_selection);
        SwitchCompat isHideWindowWhenCopying = view.findViewById(R.id.cb_is_hide_window_when_copying);
        SwitchCompat isSavingWhenPausing = view.findViewById(R.id.cb_is_saving_when_pausing);
        SwitchCompat isCrowed = view.findViewById(R.id.cb_is_crowed);
        SwitchCompat isSyntaxHighlight = view.findViewById(R.id.cb_is_syntax_highlight);
        Settings settings = Settings.getInstance(context);
        String[] showStrings = {
                "正式版-原版-" + Settings.VERSION_RELEASE,
                "正式版-实验性玩法-" + Settings.VERSION_RELEASE,
                "测试版-原版-" + Settings.VERSION_BETA,
                "测试版-实验性玩法-" + Settings.VERSION_BETA,
                "中国版-原版-" + Settings.VERSION_NETEASE,
                "中国版-实验性玩法-" + Settings.VERSION_NETEASE
        };
        //noinspection SpellCheckingInspection
        String[] cpackPaths = {
                "release-vanilla",
                "release-experiment",
                "beta-vanilla",
                "beta-experiment",
                "netease-vanilla",
                "netease-experiment"
        };
        String cpackBranch = settings.getCpackBranch();
        for (int i = 0; i < cpackPaths.length; i++) {
            if (Objects.equals(cpackBranch, cpackPaths[i])) {
                tv_currentCPack.setText(context.getString(R.string.current_cpack, showStrings[i]));
                break;
            }
        }
        btn_chooseCpack.setOnClickListener(v -> new ChoosingDialog(context, showStrings, which -> {
            String cpackPath1 = cpackPaths[which];
            tv_currentCPack.setText(context.getString(R.string.current_cpack, showStrings[which]));
            settings.setCpackPath(cpackPath1);
        }).show());
        isCheckingBySelection.setChecked(settings.isCheckingBySelection);
        isCheckingBySelection.setOnCheckedChangeListener((buttonView, isChecked) -> settings.isCheckingBySelection = isChecked);
        isHideWindowWhenCopying.setChecked(settings.isHideWindowWhenCopying);
        isHideWindowWhenCopying.setOnCheckedChangeListener((buttonView, isChecked) -> settings.isHideWindowWhenCopying = isChecked);
        isSavingWhenPausing.setChecked(settings.isSavingWhenPausing);
        isSavingWhenPausing.setOnCheckedChangeListener((buttonView, isChecked) -> settings.isSavingWhenPausing = isChecked);
        isCrowed.setChecked(settings.isCrowed);
        isCrowed.setOnCheckedChangeListener((buttonView, isChecked) -> settings.isCrowed = isChecked);
        isSyntaxHighlight.setChecked(settings.isSyntaxHighlight);
        isSyntaxHighlight.setOnCheckedChangeListener((buttonView, isChecked) -> settings.isSyntaxHighlight = isChecked);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 保存设置到文件
        Settings.getInstance(getContext()).save(getContext());
    }

}
