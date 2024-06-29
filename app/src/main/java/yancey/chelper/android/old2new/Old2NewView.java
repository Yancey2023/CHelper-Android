package yancey.chelper.android.old2new;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.function.Consumer;

import yancey.chelper.R;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.core.CHelperCore;

@SuppressLint("ViewConstructor")
public class Old2NewView extends CustomView {

    private EditText mEd_oldCommand;
    private TextView mTv_newCommand;
    private ClipboardManager clipboardManager;

    public Old2NewView(@NonNull Context context, Consumer<CustomView> openView, boolean isInFloatingWindow) {
        super(context, openView, isInFloatingWindow, R.layout.layout_old2new);
    }

    @Override
    public void onCreateView(Context context, View view) {
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        mEd_oldCommand = view.findViewById(R.id.ed_old_command);
        mTv_newCommand = view.findViewById(R.id.tv_new_command);
        view.findViewById(R.id.btn_paste).setOnClickListener(v -> {
            ClipData clip = clipboardManager.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                mEd_oldCommand.setText(clip.getItemAt(0).coerceToText(context));
            }
        });
        view.findViewById(R.id.btn_copy).setOnClickListener(v ->
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, mTv_newCommand.getText())));
        mEd_oldCommand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTv_newCommand.setText(CHelperCore.old2new(mEd_oldCommand.getText().toString()));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //execute @e[name="Yancey NB"] ~~2.5 ~ detect ~~-1~ stone 1 setblock ~ ~-1 ~ command_block 0

}
