package yancey.chelper.android.completion.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import yancey.chelper.R;
import yancey.chelper.android.common.util.ClipboardUtil;
import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.android.common.util.SelectedString;
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.android.common.view.CommandEditText;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.completion.adater.SuggestionListAdapter;
import yancey.chelper.android.completion.data.Settings;
import yancey.chelper.core.CHelperCore;
import yancey.chelper.core.CHelperGuiCore;

@SuppressLint("ViewConstructor")
public class CompletionView extends CustomView {

    private static final String TAG = "WritingCommandView";
    private FrameLayout fl_action_container, fl_actions;
    private View btn_action;
    private CommandEditText commandEditText;
    private boolean isShowActions = false;
    public boolean isGuiLoaded = false;
    private final Runnable shutDown, hideView;
    private CHelperGuiCore core;

    public CompletionView(
            @NonNull Context context,
            Runnable shutDown,
            Runnable hideView,
            Consumer<CustomView> openView
    ) {
        super(context, openView, Settings.getInstance(context).isCrowed ?
                R.layout.layout_writing_command_crowded : R.layout.layout_writing_command);
        this.shutDown = shutDown;
        this.hideView = hideView;
    }

    @Override
    public void onCreateView(Context context, View view) {
        isGuiLoaded = false;
        core = new CHelperGuiCore();
        TextView mtv_structure = view.findViewById(R.id.tv_structure);
        TextView mtv_description = view.findViewById(R.id.tv_description);
        TextView mtv_errorReasons = view.findViewById(R.id.tv_error_reasons);
        commandEditText = view.findViewById(R.id.ed_input);
        boolean isCrowed = Settings.getInstance(context).isCrowed;
        commandEditText.setListener(null, core::onSelectionChanged, () -> isGuiLoaded);
        SuggestionListAdapter adapter = new SuggestionListAdapter(context, core, isCrowed);
        RecyclerView recyclerView = view.findViewById(R.id.rv_command_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        if (isCrowed) {
            core.setUpdateStructure(adapter::setStructure);
            core.setUpdateDescription(adapter::setDescription);
        } else {
            core.setUpdateStructure(mtv_structure::setText);
            core.setUpdateDescription(mtv_description::setText);
        }
        btn_action = view.findViewById(R.id.btn_action);
        btn_action.setOnClickListener(v -> {
            isShowActions = !isShowActions;
            updateActions();
        });
        core.setTvErrorReasons(mtv_errorReasons);
        core.setCommandEditText(commandEditText);
        core.setUpdateList(adapter::notifyDataSetChanged);
        view.findViewById(R.id.btn_copy).setOnClickListener(v -> {
            if (ClipboardUtil.setText(getContext(), commandEditText.getText())) {
                ToastUtil.show(context, "已复制");
                if (hideView != null && Settings.getInstance(context).isHideWindowWhenCopying) {
                    hideView.run();
                }
            } else {
                ToastUtil.show(context, "复制失败");
            }
        });
        view.findViewById(R.id.btn_undo).setOnClickListener(v -> commandEditText.undo());
        view.findViewById(R.id.btn_redo).setOnClickListener(v -> commandEditText.redo());
        view.findViewById(R.id.btn_delete).setOnClickListener(v -> commandEditText.delete());
        view.findViewById(R.id.btn_shut_down).setOnClickListener(v -> shutDown.run());
        //加载上次的输入内容
        SelectedString selectedString = null;
        if (Settings.getInstance(context).isSavingWhenPausing) {
            File file = FileUtil.getFile(context.getFilesDir().getAbsolutePath(), "cache", "lastInput.dat");
            if (file.exists()) {
                try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                    selectedString = new SelectedString(dataInputStream.readUTF(), dataInputStream.readInt(), dataInputStream.readInt());
                } catch (IOException e) {
                    Log.e(TAG, "fail to save file : " + file.getAbsolutePath(), e);
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
    public void onPause() {
        super.onPause();
        isGuiLoaded = false;
        //保存上次的输入内容
        if (Settings.getInstance(getContext()).isSavingWhenPausing) {
            File file = FileUtil.getFile(getContext().getFilesDir().getAbsolutePath(), "cache", "lastInput.dat");
            if (!FileUtil.createParentFile(file)) {
                Log.e(TAG, "fail to create parent file : " + file.getAbsolutePath());
                return;
            }
            SelectedString selectedString = commandEditText.getSelectedString();
            try (DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
                dataOutputStream.writeUTF(selectedString.string);
                dataOutputStream.writeInt(selectedString.start);
                dataOutputStream.writeInt(selectedString.end);
            } catch (IOException e) {
                Log.e(TAG, "fail to save file : " + file.getAbsolutePath(), e);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isGuiLoaded = true;
        String cpackPath = Settings.getInstance(getContext()).getCpackPath(getContext());
        if (core.getCore() == null || !Objects.equals(core.getCore().getPath(), cpackPath)) {
            CHelperCore core1 = null;
            try {
                core1 = CHelperCore.fromAssets(getContext().getAssets(), cpackPath);
            } catch (Exception e) {
                ToastUtil.show(getContext(), "资源包加载失败");
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

    private void updateActions() {
        if (isShowActions) {
            fl_action_container.addView(fl_actions);
            btn_action.setBackgroundResource(R.drawable.icon_show);
        } else {
            fl_action_container.removeView(fl_actions);
            btn_action.setBackgroundResource(R.drawable.icon_hide);
        }
    }

}
