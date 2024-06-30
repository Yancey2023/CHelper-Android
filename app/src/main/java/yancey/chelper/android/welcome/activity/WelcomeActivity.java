package yancey.chelper.android.welcome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lzf.easyfloat.EasyFloat;
import com.lzf.easyfloat.enums.ShowPattern;
import com.lzf.easyfloat.permission.PermissionUtils;

import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.about.activity.AboutActivity;
import yancey.chelper.android.common.dialog.IsConfirmDialog;
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.android.completion.activity.CompletionActivity;
import yancey.chelper.android.completion.activity.SettingsActivity;
import yancey.chelper.android.enumeration.activity.EnumerationActivity;
import yancey.chelper.android.favorites.activity.FavoritesActivity;
import yancey.chelper.android.old2new.activity.Old2NewActivity;
import yancey.chelper.android.old2new.activity.Old2NewIMEGuideActivity;
import yancey.chelper.android.rawtext.activity.RawtextActivity;
import yancey.chelper.android.welcome.view.FloatMainView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        findViewById(R.id.btn_start_suggestion_app).setOnClickListener(v -> {
            if (isUsingFloatingWindow()) {
                ToastUtil.show(this, "你必须关闭悬浮窗模式才可以进入应用模式");
                return;
            }
            startActivity(new Intent(this, CompletionActivity.class));
        });
        findViewById(R.id.btn_start_enumeration_window).setOnClickListener(v -> {
            if (isUsingFloatingWindow()) {
                stopFloatingWindow();
            } else {
                startFloatingWindow(40);
            }
        });
        findViewById(R.id.btn_enumeration_settings).setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        findViewById(R.id.btn_start_old2new_app).setOnClickListener(v -> startActivity(new Intent(this, Old2NewActivity.class)));
        findViewById(R.id.btn_start_old2new_ime).setOnClickListener(v -> startActivity(new Intent(this, Old2NewIMEGuideActivity.class)));
        findViewById(R.id.btn_raw_json_studio).setOnClickListener(v -> startActivity(new Intent(this, RawtextActivity.class)));
        findViewById(R.id.btn_enumeration).setOnClickListener(v -> startActivity(new Intent(this, EnumerationActivity.class)));
        findViewById(R.id.btn_favorite).setOnClickListener(v -> startActivity(new Intent(this, FavoritesActivity.class)));
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
                            ToastUtil.show(WelcomeActivity.this, "悬浮窗权限获取失败");
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
            floatMainView.onDestroy();
        }
        EasyFloat.dismiss("icon_view");
        EasyFloat.dismiss("main_view");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopFloatingWindow();
    }

}
