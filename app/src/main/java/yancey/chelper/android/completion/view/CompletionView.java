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

package yancey.chelper.android.completion.view;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.Toaster;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.common.util.ClipboardUtil;
import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.android.common.util.MonitorUtil;
import yancey.chelper.android.common.util.Settings;
import yancey.chelper.android.common.view.BaseView;
import yancey.chelper.android.common.view.CommandEditText;
import yancey.chelper.android.completion.adater.SuggestionListAdapter;
import yancey.chelper.android.library.view.LocalLibraryListView;
import yancey.chelper.core.CHelperCore;
import yancey.chelper.core.CHelperGuiCore;
import yancey.chelper.core.CommandGuiCoreInterface;
import yancey.chelper.core.ErrorReason;
import yancey.chelper.core.SelectedString;
import yancey.chelper.core.Theme;

@SuppressLint("ViewConstructor")
public class CompletionView extends BaseView {

    private static final String TAG = "WritingCommandView";
    private final FrameLayout fl_action_container, fl_actions;
    private final View btn_action;
    private final CommandEditText commandEditText;
    private boolean isShowActions = false;
    public boolean isGuiLoaded;
    private final CHelperGuiCore core;

    public CompletionView(
            @NonNull CustomContext customContext,
            @NonNull Runnable shutDown,
            @Nullable Runnable hideView
    ) {
        super(customContext, Settings.INSTANCE.isCrowed ? R.layout.layout_writing_command_crowded : R.layout.layout_writing_command);
        isGuiLoaded = false;
        boolean isDarkMode = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        core = new CHelperGuiCore(isDarkMode ? Theme.THEME_NIGHT : Theme.THEME_DAY);
        TextView mtv_structure = view.findViewById(R.id.tv_structure);
        TextView mtv_description = view.findViewById(R.id.tv_description);
        TextView mtv_errorReasons = view.findViewById(R.id.tv_error_reasons);
        commandEditText = view.findViewById(R.id.ed_input);
        commandEditText.setListener(null, core::onSelectionChanged, () -> isGuiLoaded);
        SuggestionListAdapter adapter = new SuggestionListAdapter(context, core, Settings.INSTANCE.isCrowed);
        RecyclerView recyclerView = view.findViewById(R.id.rv_command_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        core.setCommandGuiCoreInterface(new CommandGuiCoreInterface() {
            @Override
            public boolean isUpdateStructure() {
                return true;
            }

            @Override
            public boolean isUpdateDescription() {
                return true;
            }

            @Override
            public boolean isUpdateErrorReason() {
                return true;
            }

            @Override
            public boolean isCheckingBySelection() {
                return Settings.INSTANCE.isCheckingBySelection;
            }

            @Override
            public boolean isSyntaxHighlight() {
                if (!Settings.INSTANCE.isSyntaxHighlight) {
                    return false;
                }
                Editable text = commandEditText.getText();
                return text != null && text.length() < 200;
            }

            @Override
            public void updateStructure(@Nullable String structure) {
                if (Settings.INSTANCE.isCrowed) {
                    adapter.setStructure(structure);
                } else {
                    mtv_structure.setText(structure);
                }
            }

            @Override
            public void updateDescription(@Nullable String description) {
                if (Settings.INSTANCE.isCrowed) {
                    adapter.setDescription(description);
                } else {
                    mtv_description.setText(description);
                }
            }

            @Override
            public void updateErrorReason(@Nullable ErrorReason[] errorReasons) {
                if (errorReasons == null || errorReasons.length == 0) {
                    if (mtv_errorReasons != null) {
                        mtv_errorReasons.setVisibility(View.GONE);
                    }
                    commandEditText.setErrorReasons(null);
                } else {
                    if (errorReasons.length == 1) {
                        if (mtv_errorReasons != null) {
                            mtv_errorReasons.setText(errorReasons[0].errorReason);
                        }
                    } else {
                        StringBuilder errorReasonStr = new StringBuilder("可能的错误原因：");
                        for (int i = 0; i < errorReasons.length; i++) {
                            errorReasonStr.append("\n").append(i + 1).append(". ").append(errorReasons[i].errorReason);
                        }
                        if (mtv_errorReasons != null) {
                            mtv_errorReasons.setText(errorReasonStr);
                        }
                    }
                    if (mtv_errorReasons != null) {
                        mtv_errorReasons.setVisibility(View.VISIBLE);
                    }
                    commandEditText.setErrorReasons(isSyntaxHighlight() ? errorReasons : null);
                }
            }

            @Override
            @SuppressLint("NotifyDataSetChanged")
            public void updateSuggestions() {
                adapter.notifyDataSetChanged();
            }

            @Override
            public SelectedString getSelectedString() {
                return commandEditText.getSelectedString();
            }

            @Override
            public void setSelectedString(SelectedString selectedString) {
                commandEditText.setSelectedString(selectedString);
            }

            @Override
            public void updateSyntaxHighlight(int[] colors) {
                commandEditText.setColors(colors);
            }
        });
        btn_action = view.findViewById(R.id.btn_action);
        btn_action.setOnClickListener(v -> {
            isShowActions = !isShowActions;
            updateActions();
        });
        view.findViewById(R.id.btn_copy).setOnClickListener(v -> {
            if (ClipboardUtil.setText(getContext(), commandEditText.getText())) {
                Toaster.show("已复制");
                if (hideView != null && Settings.INSTANCE.isHideWindowWhenCopying) {
                    hideView.run();
                }
            } else {
                Toaster.show("复制失败");
            }
        });
        view.findViewById(R.id.btn_undo).setOnClickListener(v -> commandEditText.undo());
        view.findViewById(R.id.btn_redo).setOnClickListener(v -> commandEditText.redo());
        view.findViewById(R.id.btn_delete).setOnClickListener(v -> commandEditText.delete());
        view.findViewById(R.id.btn_local_library).setOnClickListener(v -> openView(LocalLibraryListView::new));
        view.findViewById(R.id.btn_shut_down).setOnClickListener(v -> shutDown.run());
        // 加载上次的输入内容
        SelectedString selectedString = null;
        if (Settings.INSTANCE.isSavingWhenPausing) {
            File file = FileUtil.getFile(context.getFilesDir().getAbsolutePath(), "cache", "lastInput.dat");
            if (file.exists()) {
                try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                    selectedString = new SelectedString(dataInputStream.readUTF(), dataInputStream.readInt(), dataInputStream.readInt());
                } catch (IOException e) {
                    Log.e(TAG, "fail to save file : " + file.getAbsolutePath(), e);
                    MonitorUtil.generateCustomLog(e, "IOException");
                }
            }
        }
        if (selectedString == null) {
            selectedString = new SelectedString("", 0);
        }
        SelectedString finalSelectedString = selectedString;
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                isGuiLoaded = true;
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                commandEditText.setSelectedString(finalSelectedString);
                core.onSelectionChanged();
            }
        });
        fl_action_container = view.findViewById(R.id.fl_action_container);
        fl_actions = view.findViewById(R.id.fl_actions);
        if (!isShowActions) {
            updateActions();
        }
    }

    @Override
    protected String gePageName() {
        return "Completion";
    }

    @Override
    public void onPause() {
        super.onPause();
        isGuiLoaded = false;
        // 保存上次的输入内容
        File file = FileUtil.getFile(getContext().getFilesDir().getAbsolutePath(), "cache", "lastInput.dat");
        if (!FileUtil.createParentFile(file)) {
            Log.e(TAG, "fail to create parent file : " + file.getAbsolutePath());
            return;
        }
        SelectedString selectedString = commandEditText.getSelectedString();
        try (DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            dataOutputStream.writeUTF(selectedString.text);
            dataOutputStream.writeInt(selectedString.selectionStart);
            dataOutputStream.writeInt(selectedString.selectionEnd);
        } catch (IOException e) {
            Log.e(TAG, "fail to save file : " + file.getAbsolutePath(), e);
            MonitorUtil.generateCustomLog(e, "IOException");
        }
    }

    private void updateActions() {
        if (isShowActions) {
            fl_action_container.addView(fl_actions);
            btn_action.setBackgroundResource(R.drawable.chevron_down);
        } else {
            fl_action_container.removeView(fl_actions);
            btn_action.setBackgroundResource(R.drawable.chevron_up);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isGuiLoaded = true;
        String cpackPath = Settings.INSTANCE.getCpackPath();
        if (core.getCore() == null || !Objects.equals(core.getCore().getPath(), cpackPath)) {
            CHelperCore core1 = null;
            try {
                core1 = CHelperCore.fromAssets(getContext().getAssets(), cpackPath);
            } catch (Throwable throwable) {
                Toaster.show("资源包加载失败");
                Log.w(TAG, "fail to load resource pack", throwable);
                MonitorUtil.generateCustomLog(throwable, "LoadResourcePackException");
            }
            core.setCore(core1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (core == null) {
            return;
        }
        core.close();
    }

}
