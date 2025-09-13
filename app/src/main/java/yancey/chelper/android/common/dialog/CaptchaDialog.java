package yancey.chelper.android.common.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialog;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class CaptchaDialog extends AppCompatDialog {

    private final String specialCode;
    private Callback callback;

    public CaptchaDialog(@NonNull Context context) {
        super(context);
        this.specialCode = UUID.randomUUID().toString();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webView = new WebView(getContext());
        setContentView(webView);
        Window window = Objects.requireNonNull(getWindow());
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams attributes = window.getAttributes();
        Point point = new Point();
        window.getWindowManager().getDefaultDisplay().getSize(point);
        int edgeLength = (int) (((double) point.x) * 0.95d);
        attributes.width = edgeLength;
        attributes.height = edgeLength;
        window.setAttributes(attributes);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setBackgroundColor(Color.WHITE);
        webView.addJavascriptInterface(new JavaScriptInterface(), "android");
        webView.loadUrl("https://abyssous.site/captcha/verifing?token=" + specialCode);
        setOnCancelListener(dialog -> callback.onCancel(specialCode));
    }

    public class JavaScriptInterface {

        public JavaScriptInterface() {

        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void onSuccess(@Nullable String specialCode) {
            if (callback != null && !TextUtils.isEmpty(specialCode)) {
                callback.onSuccess(specialCode);
            }
            dismiss();
        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void onFail() {
            if (callback != null && !TextUtils.isEmpty(specialCode)) {
                callback.onFail(specialCode);
            }
            dismiss();
        }

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void onCancel() {
            if (callback != null && !TextUtils.isEmpty(specialCode)) {
                callback.onCancel(specialCode);
            }
            dismiss();
        }
    }

    public interface Callback {
        void onSuccess(@NotNull String specialCode);

        void onFail(@NotNull String specialCode);

        void onCancel(@NotNull String specialCode);
    }
}
