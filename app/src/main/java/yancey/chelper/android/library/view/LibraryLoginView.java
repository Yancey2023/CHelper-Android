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
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import yancey.chelper.R;
import yancey.chelper.android.common.util.TextWatcherUtil;
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
        EditText email = view.findViewById(R.id.email);
        EditText password = view.findViewById(R.id.password);
        ImageView clearEmail = view.findViewById(R.id.clear_email);
        ImageView hidePassword = view.findViewById(R.id.hide_password);
        message = view.findViewById(R.id.message);
        Button btn_login = view.findViewById(R.id.btn_login);
        setButtonVisibility(email, clearEmail);
        setButtonVisibility(password, hidePassword);
        clearEmail.setOnClickListener(v -> email.setText(null));
        hidePassword.setOnClickListener(v -> {
            isShowPassword = !isShowPassword;
            int start = password.getSelectionStart();
            int end = password.getSelectionEnd();
            if (isShowPassword) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                hidePassword.setImageResource(R.drawable.eye);
            } else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                hidePassword.setImageResource(R.drawable.eye_off);
            }
            password.setSelection(start, end);
        });
        btn_login.setOnClickListener(v -> {
            //避免反复按登录按钮
            if (isLogging) {
                showMessage(R.string.layout_login_logging);
                return;
            }
            //获取输入的内容
            LoginUtil.user = new User();
            LoginUtil.user.email = getText(email);
            LoginUtil.user.password = getText(password);
            if (LoginUtil.user.email == null || LoginUtil.user.password == null) {
                showMessageError(R.string.layout_login_no_parameter);
                return;
            }
            //开始登录
            if (login != null) {
                login.dispose();
            }
            showMessage(R.string.layout_login_logging);
            isLogging = true;
            login = Observable.create((ObservableOnSubscribe<String>) emitter -> {
                        emitter.onNext(LoginUtil.getToken());
                        emitter.onComplete();
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(token -> {
                        if (token != null) {
                            showMessage(R.string.layout_login_successfully);
                            getOnBackPressedDispatcher().onBackPressed();
                        } else {
                            showMessageError(R.string.layout_login_failed);
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

    /**
     * 设置用于清空输入框内容的按钮
     */
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    private void setButtonVisibility(EditText editText, ImageView imageView) {
        if (editText.getText().length() == 0) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setVisibility(View.VISIBLE);
        }
        editText.addTextChangedListener(TextWatcherUtil.onTextChanged(s -> {
            if (s.length() == 0) {
                imageView.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.VISIBLE);
            }
        }));
    }

    @ColorInt
    private Integer textColorMain, textColorError;

    private void showMessage(@StringRes int resId) {
        message.setText(resId);
        if (textColorMain == null) {
            textColorMain = getContext().getColor(R.color.text_main);
        }
        message.setTextColor(textColorMain);
    }

    private void showMessageError(String message) {
        this.message.setText(message);
        if (textColorError == null) {
            textColorError = getContext().getColor(R.color.red);
        }
        this.message.setTextColor(textColorError);
    }

    private void showMessageError(@StringRes int resId) {
        message.setText(resId);
        if (textColorError == null) {
            textColorError = getContext().getColor(R.color.red);
        }
        message.setTextColor(textColorError);
    }
}
