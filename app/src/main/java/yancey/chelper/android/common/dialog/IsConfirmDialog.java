package yancey.chelper.android.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Objects;

import yancey.chelper.R;

public class IsConfirmDialog extends Dialog {

    private String title, message, confirm, cancel;
    private View.OnClickListener onConfirm;
    private View.OnClickListener onCancel;

    public IsConfirmDialog(@NonNull Context context,boolean isBig) {
        super(context);
        if(isBig){
            setContentView(R.layout.dialog_is_confirm_big);
        }else{
            setContentView(R.layout.dialog_is_confirm);
        }
        Window window = Objects.requireNonNull(getWindow());
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams attributes = window.getAttributes();
        Point point = new Point();
        window.getWindowManager().getDefaultDisplay().getSize(point);
        attributes.width = (int) (((double) point.x) * 0.95d);
        window.setAttributes(attributes);
    }

    public IsConfirmDialog title(String title) {
        this.title = title;
        return this;
    }

    public IsConfirmDialog message(String message) {
        this.message = message;
        return this;
    }

    public IsConfirmDialog onConfirm(String confirm) {
        this.confirm = confirm;
        return this;
    }

    public IsConfirmDialog onConfirm(View.OnClickListener onConfirm) {
        this.onConfirm = onConfirm;
        return this;
    }

    public IsConfirmDialog onConfirm(String confirm, View.OnClickListener onConfirm) {
        this.confirm = confirm;
        this.onConfirm = onConfirm;
        return this;
    }

    public IsConfirmDialog onCancel(String cancel) {
        this.cancel = cancel;
        return this;
    }

    public IsConfirmDialog onCancel(View.OnClickListener onCancel) {
        this.onCancel = onCancel;
        return this;
    }

    public IsConfirmDialog onCancel(String cancel, View.OnClickListener onCancel) {
        this.cancel = cancel;
        this.onCancel = onCancel;
        return this;
    }

    @Override
    public void show() {
        ((TextView) findViewById(R.id.is_confirm_title)).setText(Objects.requireNonNullElse(title, "温馨提示"));
        ((TextView) findViewById(R.id.is_confirm_message)).setText(Objects.requireNonNullElse(message, ""));
        TextView btn_confirm = findViewById(R.id.is_confirm_confirm);
        TextView btn_cancel = findViewById(R.id.is_confirm_cancel);
        btn_confirm.setText(Objects.requireNonNullElse(confirm, "确认"));
        btn_cancel.setText(Objects.requireNonNullElse(cancel, "取消"));
        btn_confirm.setOnClickListener(view -> {
            if (onConfirm != null) {
                onConfirm.onClick(view);
            }
            dismiss();
        });
        btn_cancel.setOnClickListener(view -> {
            if (onCancel != null) {
                onCancel.onClick(view);
            }
            dismiss();
        });
        super.show();
    }
}
