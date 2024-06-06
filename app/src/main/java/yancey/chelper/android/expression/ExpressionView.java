package yancey.chelper.android.expression;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import redempt.crunch.data.Pair;
import redempt.crunch.exceptions.ExpressionCompilationException;
import yancey.chelper.R;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.util.DataVariable;
import yancey.chelper.expression.CustomDoubleSupplier;
import yancey.chelper.expression.ExpressionUtil;

@SuppressLint("ViewConstructor")
public class ExpressionView extends CustomView {

    private static final String TAG = "ExpressionView";
    private VariableListAdapter adapter;
    private EditText mEd_input, mEd_times;
    private ClipboardManager clipboardManager;

    public ExpressionView(@NonNull Context context, Consumer<CustomView> openView, boolean isInFloatingWindow) {
        super(context, openView, isInFloatingWindow, R.layout.layout_expression);
    }

    @Override
    public void onCreateView(Context context, View view) {
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
                toast("无法获取输出的内容");
                return;
            }
            String input = editable.toString();
            editable = mEd_times.getText();
            if (editable == null) {
                Log.w(TAG, "mEd_times.getText() is null");
                toast("无法获取输出的次数");
                return;
            }
            int times;
            try {
                times = Integer.parseInt(editable.toString());
            } catch (NumberFormatException e) {
                Log.w(TAG, "运行次数不是整数", e);
                toast("运行次数不是整数");
                return;
            }
            List<Pair<String, CustomDoubleSupplier>> pairs;
            try {
                pairs = adapter.getValue();
            } catch (NumberFormatException e) {
                Log.w(TAG, "变量数据的获取出错", e);
                toast("变量数据的获取出错");
                return;
            }
            String output;
            try {
                output = ExpressionUtil.run(input, pairs, times);
            } catch (NumberFormatException e) {
                Log.w(TAG, "运行时出错", e);
                toast("运行时出错 : 文字转数字时失败");
                return;
            } catch (ExpressionCompilationException e) {
                Log.w(TAG, "运行时出错", e);
                toast("运行时出错 : 表达式结构有问题");
                return;
            } catch (Exception e) {
                Log.w(TAG, "运行时出错", e);
                toast("运行时出错");
                return;
            }
            new IsConfirmDialog(context, true).title("输出预览").message(output).onConfirm("复制", v1 -> {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, output));
                toast("已复制");
            }).show();
        });
    }

    public void toast(String str) {
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }

}
