package yancey.chelper.android.old2new.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.function.Consumer;

import yancey.chelper.R;
import yancey.chelper.android.common.util.ClipboardUtil;
import yancey.chelper.android.common.util.TextWatcherUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.core.CHelperCore;

/**
 * 旧命令转新命令界面
 */
@SuppressLint("ViewConstructor")
public class Old2NewView extends CustomView {

    private EditText mEd_oldCommand;
    private TextView mTv_newCommand;

    public Old2NewView(@NonNull Context context, Consumer<CustomView> openView) {
        super(context, openView, R.layout.layout_old2new);
    }

    @Override
    public void onCreateView(Context context, View view) {
        mEd_oldCommand = view.findViewById(R.id.ed_old_command);
        mTv_newCommand = view.findViewById(R.id.tv_new_command);
        view.findViewById(R.id.btn_paste).setOnClickListener(v -> {
            CharSequence charSequence = ClipboardUtil.getText(context);
            if (charSequence != null) {
                mEd_oldCommand.setText(charSequence);
                mEd_oldCommand.setSelection(charSequence.length());
            }
        });
        view.findViewById(R.id.btn_copy).setOnClickListener(v ->
                ClipboardUtil.setText(getContext(), mTv_newCommand.getText()));
        mEd_oldCommand.addTextChangedListener(TextWatcherUtil.onTextChanged(charSequence ->
                mTv_newCommand.setText(CHelperCore.old2new(getContext(), mEd_oldCommand.getText().toString()))));
    }

}
