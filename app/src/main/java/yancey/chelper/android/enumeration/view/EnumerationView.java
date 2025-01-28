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

package yancey.chelper.android.enumeration.view;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import redempt.crunch.data.Pair;
import redempt.crunch.exceptions.ExpressionCompilationException;
import yancey.chelper.R;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.enumeration.adapter.VariableListAdapter;
import yancey.chelper.android.enumeration.core.CustomDoubleSupplier;
import yancey.chelper.android.enumeration.core.EnumerationUtil;
import yancey.chelper.android.enumeration.data.DataVariable;

/**
 * 穷举界面
 */
@SuppressLint("ViewConstructor")
public class EnumerationView extends CustomView {

    private static final String TAG = "ExpressionView";
    private VariableListAdapter adapter;
    private EditText mEd_input, mEd_times;
    private ClipboardManager clipboardManager;

    public EnumerationView(@NonNull Context context, @NonNull Consumer<CustomView> openView, @NonNull Environment environment) {
        super(context, openView, environment, R.layout.layout_enumeration);
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        adapter = new VariableListAdapter(context, new ArrayList<>());
        RecyclerView recyclerView = view.findViewById(R.id.rv_variable_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);
        mEd_input = view.findViewById(R.id.ed_input);
        mEd_times = view.findViewById(R.id.ed_times);
        view.findViewById(R.id.btn_add).setOnClickListener(v -> adapter.add(new DataVariable()));
        view.findViewById(R.id.btn_run).setOnClickListener(v -> {
            Editable editable = mEd_input.getText();
            if (editable == null) {
                Log.w(TAG, "mEd_input.getText() is null");
                ToastUtil.show(context, "无法获取输出的内容");
                return;
            }
            String input = editable.toString();
            editable = mEd_times.getText();
            if (editable == null) {
                Log.w(TAG, "mEd_times.getText() is null");
                ToastUtil.show(context, "无法获取输出的次数");
                return;
            }
            int times;
            try {
                times = Integer.parseInt(editable.toString());
            } catch (NumberFormatException e) {
                Log.w(TAG, "运行次数不是整数", e);
                ToastUtil.show(context, "运行次数不是整数");
                return;
            }
            List<Pair<String, CustomDoubleSupplier>> pairs;
            try {
                pairs = adapter.getValue();
            } catch (NumberFormatException e) {
                Log.w(TAG, "变量数据的获取出错", e);
                ToastUtil.show(context, "变量数据的获取出错");
                return;
            }
            String output;
            try {
                output = EnumerationUtil.run(input, pairs, times);
            } catch (NumberFormatException e) {
                Log.w(TAG, "运行时出错", e);
                ToastUtil.show(context, "运行时出错 : 文字转数字时失败");
                return;
            } catch (ExpressionCompilationException e) {
                Log.w(TAG, "运行时出错", e);
                ToastUtil.show(context, "运行时出错 : 表达式结构有问题");
                return;
            } catch (Exception e) {
                Log.w(TAG, "运行时出错", e);
                ToastUtil.show(context, "运行时出错");
                return;
            }
            new IsConfirmDialog(context, true).title("输出预览").message(output).onConfirm("复制", v1 -> {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, output));
                ToastUtil.show(context, "已复制");
            }).show();
        });
    }

}
