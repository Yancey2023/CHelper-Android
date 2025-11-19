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

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import yancey.chelper.R;
import yancey.chelper.android.common.util.SpanUtil;
import yancey.chelper.android.common.view.BaseView;
import yancey.chelper.network.library.data.User;
import yancey.chelper.network.library.util.LoginUtil;

/**
 * 命令库登录视图
 */
@SuppressLint("ViewConstructor")
public class LibraryLoginView extends BaseView {

    private final TextView message;
    private boolean isShowPassword = false;
    private boolean isLogging = false;
    private Disposable login;

    public LibraryLoginView(@NonNull FWSContext fwsContext) {
        super(fwsContext, R.layout.layout_library_login);
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
        Button loginBtn = view.findViewById(R.id.btn_login);
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
        loginBtn.setOnClickListener(v -> {
            // 判断是否勾选了协议
            if (!confirmRead.isChecked()) {
                showMessageError(R.string.layout_library_login_confirm_check_before_login);
                return;
            }
            // 避免反复按登录按钮
            if (isLogging) {
                showMessage(R.string.layout_library_login_logging);
                return;
            }
            // 获取输入的内容
            LoginUtil.user = new User();
            String accountText = Objects.requireNonNull(getText(account));
            if (accountText.contains("@")) {
                LoginUtil.user.email = accountText;
            } else {
                LoginUtil.user.phoneNumber = accountText;
            }
            LoginUtil.user.password = getText(password);
            if ((LoginUtil.user.email == null && LoginUtil.user.phoneNumber == null) || LoginUtil.user.password == null) {
                showMessageError(R.string.layout_library_login_varification_failed);
                return;
            }
            // 开始登录
            if (login != null) {
                login.dispose();
            }
            showMessage(R.string.layout_library_login_logging);
            isLogging = true;
            login = Observable.create((ObservableOnSubscribe<String>) emitter -> {
                        emitter.onNext(LoginUtil.getToken());
                        emitter.onComplete();
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(token -> {
                        if (token != null) {
                            showMessage(R.string.layout_library_login_successfully);
                            getOnBackPressedDispatcher().onBackPressed();
                        } else {
                            showMessageError(R.string.layout_library_login_failed);
                            isLogging = false;
                        }
                    }, throwable -> {
                        isLogging = false;
                        showMessageError(throwable.getMessage());
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
        if (login != null) {
            login.dispose();
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
