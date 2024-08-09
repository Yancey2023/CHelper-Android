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

/**
 * 是否确认的对话框
 */
public class IsConfirmDialog extends Dialog {

    // 标题，内容，取消按钮文本，确认按钮文本
    private String title, message, confirm, cancel;
    // 取消按钮按下后的事件
    private View.OnClickListener onConfirm;
    // 确认按钮按下后的事件
    private View.OnClickListener onCancel;

    /**
     * @param context 上下文
     * @param isBig   是否为大型对话框
     */
    public IsConfirmDialog(@NonNull Context context, boolean isBig) {
        super(context);
        if (isBig) {
            setContentView(R.layout.dialog_is_confirm_big);
        } else {
            setContentView(R.layout.dialog_is_confirm);
        }
        // 因为在界面中定义的宽度无效，所以在代码中把宽度设置为0.95倍屏幕宽度，并把背景设置透明
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
        // 绑定相应的信息
        ((TextView) findViewById(R.id.is_confirm_title)).setText(Objects.requireNonNullElse(title, "温馨提示"));
        ((TextView) findViewById(R.id.is_confirm_message)).setText(Objects.requireNonNullElse(message, ""));
        TextView btn_confirm = findViewById(R.id.is_confirm_confirm);
        TextView btn_cancel = findViewById(R.id.is_confirm_cancel);
        btn_confirm.setText(Objects.requireNonNullElse(confirm, getContext().getString(R.string.tv_confirm)));
        btn_cancel.setText(Objects.requireNonNullElse(cancel, getContext().getString(R.string.tv_cancel)));
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
        // 显示
        super.show();
    }
}
