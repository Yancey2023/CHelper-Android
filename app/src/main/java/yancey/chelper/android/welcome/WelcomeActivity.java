package yancey.chelper.android.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.permission.PermissionUtils;

import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.about.AboutActivity;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.main.WritingCommandActivity;
import yancey.chelper.android.old2new.Old2NewIMESettingsActivity;
import yancey.chelper.core.CHelperCore;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (!CHelperCore.INSTANCE.initCPack(this)) {
            Toast.makeText(this, "资源包加载失败", Toast.LENGTH_SHORT).show();
            return;
        }
        findViewById(R.id.btn_start_app).setOnClickListener(v -> {
            if (isUsingFloatingWindow()) {
                Toast.makeText(this, "你必须关闭悬浮窗模式才可以进入应用模式", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(this, WritingCommandActivity.class));
        });
        findViewById(R.id.btn_start_window).setOnClickListener(v -> {
            if (isUsingFloatingWindow()) {
                stopFloatingWindow();
            } else {
                startFloatingWindow(40);
            }
        });
        findViewById(R.id.btn_start_ime).setOnClickListener(v -> {
            startActivity(new Intent(this, Old2NewIMESettingsActivity.class));
        });
        findViewById(R.id.btn_about).setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));
    }

    private boolean isUsingFloatingWindow() {
        return EasyFloat.getFloatView("icon_view") != null;
    }

    private void startFloatingWindow(int iconSize) {
        if (!PermissionUtils.checkPermission(this)) {
            new IsConfirmDialog(this, false)
                    .message("需要悬浮窗权限，请进入设置进行授权")
                    .onConfirm("打开设置", v -> PermissionUtils.requestPermission(this, isOpen -> {
                        if (isOpen) {
                            startFloatingWindow(iconSize);
                        } else {
                            Toast.makeText(WelcomeActivity.this, "悬浮窗权限获取失败", Toast.LENGTH_SHORT).show();
                        }
                    })).show();
            return;
        }
        int length = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                iconSize,
                getResources().getDisplayMetrics()
        );
        FloatMainView floatMainView = new FloatMainView(this, this::stopFloatingWindow, length);
        Runnable hide = () -> {
            EasyFloat.updateFloat("icon_view", (int) floatMainView.getIconViewX(), (int) floatMainView.getIconViewY());
            EasyFloat.hide("main_view");
            EasyFloat.clearFocus("main_view");
            floatMainView.onPause();
        };
        floatMainView.setOnIconClickListener(hide);
        ImageView iconView = new ImageView(this);
        iconView.setImageResource(R.drawable.pack_icon);
        iconView.setLayoutParams(new FrameLayout.LayoutParams(length, length, Gravity.START | Gravity.TOP));
        iconView.setOnClickListener(v -> {
            if (EasyFloat.getFloatView("main_view") == null) {
                EasyFloat.with(this)
                        .setLayout(floatMainView)
                        .setTag("main_view")
                        .setShowPattern(ShowPattern.ALL_TIME)
                        .setDragEnable(false)
                        .setMatchParent(true, true)
                        .hasEditText(true)
                        .setAnimator(null)
                        .show();
                floatMainView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        floatMainView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        EasyFloat.requestFocus("main_view");
                    }
                });
            } else {
                EasyFloat.show("main_view");
                EasyFloat.requestFocus("main_view");
            }
            floatMainView.setIconPosition(
                    Objects.requireNonNull(EasyFloat.getX("icon_view")),
                    Objects.requireNonNull(EasyFloat.getY("icon_view"))
            );
            floatMainView.onResume();
        });
        EasyFloat.with(this)
                .setLayout(iconView)
                .setTag("icon_view")
                .setShowPattern(ShowPattern.ALL_TIME)
                .setDragEnable(true)
                .setGravity(Gravity.START | Gravity.TOP)
                .setAnimator(null)
                .show();
    }

    private void stopFloatingWindow() {
        FloatMainView floatMainView = (FloatMainView) EasyFloat.getFloatView("main_view");
        if (floatMainView != null) {
            floatMainView.onPause();
        }
        EasyFloat.dismiss("icon_view");
        EasyFloat.dismiss("main_view");
    }

}
