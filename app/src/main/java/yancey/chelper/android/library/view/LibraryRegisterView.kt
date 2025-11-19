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

package yancey.chelper.android.library.view;

import android.annotation.SuppressLint;
import android.text.SpannableString;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.hjq.toast.Toaster;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;
import yancey.chelper.R;
import yancey.chelper.android.common.dialog.CaptchaDialog;
import yancey.chelper.android.common.util.SpanUtil;
import yancey.chelper.android.common.view.BaseView;
import yancey.chelper.network.library.data.User;

/**
 * 命令库注册视图
 */
@SuppressLint("ViewConstructor")
public class LibraryRegisterView extends BaseView {

    private final TextView message;
    private boolean isShowPassword = false;
    private Disposable register;

    public LibraryRegisterView(@NonNull FWSContext fwsContext) {
        super(fwsContext, R.layout.layout_library_register);
        EditText account = view.findViewById(R.id.account);
        EditText password = view.findViewById(R.id.password);
        ImageView clearAccount = view.findViewById(R.id.clear_account);
        ImageView togglePasswordVisibility = view.findViewById(R.id.toggle_password_visibility);
        message = view.findViewById(R.id.message);
        CheckBox confirmRead = view.findViewById(R.id.confirm_read);
        SpannableString spannableString = new SpannableString(confirmRead.getText());
        SpanUtil.addTextClickableSpan(context, spannableString, "《用户协议》", () -> {
//            Intent intent = new Intent(context, ShowTextActivity.class);
//            intent.putExtra(ShowTextActivity.TITLE, context.getString(R.string.layout_about_privacy_policy));
//            intent.putExtra(ShowTextActivity.CONTENT, AssetsUtil.readStringFromAssets(context, "about/privacy_policy.txt"));
//            context.startActivity(intent);
        });
        SpanUtil.addTextClickableSpan(context, spannableString, "《隐私政策》", () -> {
//            Intent intent = new Intent(context, ShowTextActivity.class);
//            intent.putExtra(ShowTextActivity.TITLE, context.getString(R.string.layout_about_privacy_policy));
//            intent.putExtra(ShowTextActivity.CONTENT, AssetsUtil.readStringFromAssets(context, "about/privacy_policy.txt"));
//            context.startActivity(intent);
        });
        confirmRead.setText(spannableString);
        Button nextStepBtn = view.findViewById(R.id.btn_register);
        clearAccount.setOnClickListener(v -> account.setText(null));
        togglePasswordVisibility.setOnClickListener(v -> {
            isShowPassword = !isShowPassword;
            int start = password.getSelectionStart();
            int end = password.getSelectionEnd();
            if (isShowPassword) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                togglePasswordVisibility.setImageResource(R.drawable.eye);
            } else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                togglePasswordVisibility.setImageResource(R.drawable.eye_off);
            }
            password.setSelection(start, end);
        });
        nextStepBtn.setOnClickListener(v -> {
            // 判断是否勾选了协议
            if (!confirmRead.isChecked()) {
                showMessageError(R.string.layout_library_register_confirm_check_before_register);
                return;
            }
            // 获取输入的内容
            User user = new User();
            String accountText = Objects.requireNonNull(getText(account));
            if (accountText.contains("@")) {
                user.email = accountText;
            } else {
                user.phoneNumber = accountText;
            }
            user.password = getText(password);
            if ((user.email == null && user.phoneNumber == null) || user.password == null) {
                showMessageError(R.string.layout_library_register_varification_failed);
                return;
            }
            // 人机认证
            clearMessage();
            new CaptchaDialog(context)
                    .setCallback(new CaptchaDialog.Callback() {
                        @Override
                        public void onSuccess(@NonNull String specialCode) {
                            // TODO continue
                        }

                        @Override
                        public void onFail(@NonNull String specialCode) {
                            Toaster.show("人机验证失败");
                        }

                        @Override
                        public void onCancel(@NonNull String specialCode) {
                            // do nothing
                        }
                    });
        });
    }

    @Override
    protected String gePageName() {
        return "LibraryLogin";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (register != null) {
            register.dispose();
        }
    }

    /**
     * 获取输入的文字
     */
    @Nullable
    private String getText(EditText editText) {
        String string = editText.getText().toString();
        return string.isEmpty() ? null : string;
    }

    @ColorInt
    private Integer textColorMain, textColorError;

    private void clearMessage() {
        message.setText(null);
    }

    private void showMessage(@StringRes int resId) {
        message.setText(resId);
        if (textColorMain == null) {
            textColorMain = context.getColor(R.color.text_main);
        }
        message.setTextColor(textColorMain);
    }

    private void showMessageError(String message) {
        this.message.setText(message);
        if (textColorError == null) {
            textColorError = context.getColor(R.color.red);
        }
        this.message.setTextColor(textColorError);
    }

    private void showMessageError(@StringRes int resId) {
        message.setText(resId);
        if (textColorError == null) {
            textColorError = context.getColor(R.color.red);
        }
        message.setTextColor(textColorError);
    }

}
