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
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.network.library.data.User;
import yancey.chelper.network.library.util.LoginUtil;

/**
 * 命令库登录视图
 */
@SuppressLint("ViewConstructor")
public class LibraryLoginView extends CustomView {

    private final TextView tv_message;
    private boolean isShowPassword = false;
    private boolean isLogging = false;
    private Disposable login;

    public LibraryLoginView(@NonNull CustomContext customContext) {
        super(customContext, R.layout.layout_library_login);
        EditText email = view.findViewById(R.id.loginname);
        EditText password = view.findViewById(R.id.password);
        ImageView btn_clearName = view.findViewById(R.id.clear_btn_name);
        ImageView btn_hide = view.findViewById(R.id.hide_btn);
        tv_message = view.findViewById(R.id.tv_message);
        Button btn_login = view.findViewById(R.id.login);
        setButtonClear(email, btn_clearName);
        setButtonClear(password, btn_hide);
        btn_hide.setOnClickListener(v -> {
            isShowPassword = !isShowPassword;
            int start = password.getSelectionStart();
            int end = password.getSelectionEnd();
            if (isShowPassword) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                btn_hide.setImageResource(R.drawable.eye);
            } else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                btn_hide.setImageResource(R.drawable.eye_off);
            }
            password.setSelection(start, end);
        });
        btn_login.setOnClickListener(v -> {
            //避免反复按登录按钮
            if (isLogging) {
                showMessage(R.string.login_error_logging);
                return;
            }
            //获取输入的内容
            LoginUtil.user = new User();
            LoginUtil.user.email = getText(email);
            LoginUtil.user.password = getText(password);
            //判断登录方式
            if (LoginUtil.user.email == null || LoginUtil.user.password == null) {
                showMessageError(R.string.login_error_no_parameter);
                return;
            }
            //开始登录
            if (login != null) {
                login.dispose();
            }
            showMessage(R.string.logging);
            isLogging = true;
            login = Observable.create((ObservableOnSubscribe<String>) emitter -> {
                        emitter.onNext(LoginUtil.getToken());
                        emitter.onComplete();
                    }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(token -> {
                        if (token != null) {
                            showMessage(R.string.login_successfully);
                            // TODO back
                        } else {
                            showMessageError(R.string.login_error_password);
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
    private void setButtonClear(EditText editText, ImageView imageView) {
        imageView.setOnClickListener(v -> editText.setText(null));
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
        tv_message.setText(resId);
        if (textColorMain == null) {
            textColorMain = getContext().getColor(R.color.text_main);
        }
        tv_message.setTextColor(textColorMain);
    }

    private void showMessageError(String message) {
        tv_message.setText(message);
        if (textColorError == null) {
            textColorError = getContext().getColor(R.color.red);
        }
        tv_message.setTextColor(textColorError);
    }

    private void showMessageError(@StringRes int resId) {
        tv_message.setText(resId);
        if (textColorError == null) {
            textColorError = getContext().getColor(R.color.red);
        }
        tv_message.setTextColor(textColorError);
    }
}
