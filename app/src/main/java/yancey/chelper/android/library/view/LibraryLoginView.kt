/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
 * Copyright (C) 2025  Yancey
 *
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https:></https:>//www.gnu.org/licenses/>.
 */
package yancey.chelper.android.library.view

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import yancey.chelper.R
import yancey.chelper.android.common.util.SpanUtil
import yancey.chelper.android.common.view.BaseView
import yancey.chelper.network.library.data.User
import yancey.chelper.network.library.util.LoginUtil
import yancey.chelper.network.library.util.LoginUtil.token
import java.util.Objects

/**
 * 命令库登录视图
 */
@SuppressLint("ViewConstructor")
class LibraryLoginView(fwsContext: FWSContext) :
    BaseView(fwsContext, R.layout.layout_library_login) {
    private val message: TextView
    private var isShowPassword = false
    private var isLogging = false
    private var login: Disposable? = null

    override fun gePageName(): String {
        return "LibraryLogin"
    }

    override fun onDestroy() {
        super.onDestroy()
        if (login != null) {
            login!!.dispose()
        }
    }

    /**
     * 获取输入的文字
     */
    private fun getText(editText: EditText): String? {
        val string = editText.getText().toString()
        return if (string.isEmpty()) null else string
    }

    @ColorInt
    private var textColorMain: Int? = null

    @ColorInt
    private var textColorError: Int? = null

    init {
        val account = view.findViewById<EditText>(R.id.account)
        val password = view.findViewById<EditText>(R.id.password)
        val clearAccount = view.findViewById<ImageView>(R.id.clear_account)
        val togglePasswordVisibility = view.findViewById<ImageView>(R.id.toggle_password_visibility)
        message = view.findViewById<TextView>(R.id.message)
        val confirmRead = view.findViewById<CheckBox>(R.id.confirm_read)
        val spannableString = SpannableString(confirmRead.getText())
        SpanUtil.addTextClickableSpan(context, spannableString, "《用户协议》", Runnable {})
        SpanUtil.addTextClickableSpan(context, spannableString, "《隐私政策》", Runnable {})
        confirmRead.setText(spannableString)
        val loginBtn = view.findViewById<Button>(R.id.btn_login)
        clearAccount.setOnClickListener { account.setText(null) }
        togglePasswordVisibility.setOnClickListener({
            isShowPassword = !isShowPassword
            val start = password.getSelectionStart()
            val end = password.getSelectionEnd()
            if (isShowPassword) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance())
                togglePasswordVisibility.setImageResource(R.drawable.eye)
            } else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance())
                togglePasswordVisibility.setImageResource(R.drawable.eye_off)
            }
            password.setSelection(start, end)
        })
        loginBtn.setOnClickListener({
            // 判断是否勾选了协议
            if (!confirmRead.isChecked()) {
                showMessageError(R.string.layout_library_login_confirm_check_before_login)
                return@setOnClickListener
            }
            // 避免反复按登录按钮
            if (isLogging) {
                showMessage(R.string.layout_library_login_logging)
                return@setOnClickListener
            }
            // 获取输入的内容
            val user = User()
            LoginUtil.user = user
            val accountText = Objects.requireNonNull<String>(getText(account))
            if (accountText.contains("@")) {
                user.email = accountText
            } else {
                user.phoneNumber = accountText
            }
            user.password = getText(password)
            if ((user.email == null && user.phoneNumber == null) || user.password == null) {
                showMessageError(R.string.layout_library_login_varification_failed)
                return@setOnClickListener
            }
            // 开始登录
            if (login != null) {
                login!!.dispose()
            }
            showMessage(R.string.layout_library_login_logging)
            isLogging = true
            login =
                Observable.create<String>( { emitter ->
                    emitter.onNext(token!!)
                    emitter.onComplete()
                }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(Consumer { token: String? ->
                        if (token != null) {
                            showMessage(R.string.layout_library_login_successfully)
                            getOnBackPressedDispatcher().onBackPressed()
                        } else {
                            showMessageError(R.string.layout_library_login_failed)
                            isLogging = false
                        }
                    }, Consumer { throwable: Throwable? ->
                        isLogging = false
                        showMessageError(throwable!!.message)
                    })
        })
    }

    private fun showMessage(@StringRes resId: Int) {
        message.setText(resId)
        if (textColorMain == null) {
            textColorMain = context.getColor(R.color.text_main)
        }
        message.setTextColor(textColorMain!!)
    }

    private fun showMessageError(message: String?) {
        this.message.setText(message)
        if (textColorError == null) {
            textColorError = context.getColor(R.color.red)
        }
        this.message.setTextColor(textColorError!!)
    }

    private fun showMessageError(@StringRes resId: Int) {
        message.setText(resId)
        if (textColorError == null) {
            textColorError = context.getColor(R.color.red)
        }
        message.setTextColor(textColorError!!)
    }
}
