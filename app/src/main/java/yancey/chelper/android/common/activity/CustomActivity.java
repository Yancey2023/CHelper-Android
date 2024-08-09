package yancey.chelper.android.common.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.function.Consumer;

import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.common.view.MainView;

/**
 * 使用单个View组成的界面
 * 悬浮窗只能使用单个View显示界面，所以为了方便在悬浮窗模式和应用模式共享界面代码，所以设计了CustomActivity
 *
 * @param <T> View的内容
 */
public abstract class CustomActivity<T extends CustomView> extends AppCompatActivity {

    private MainView<T> view;

    protected abstract T createView(Consumer<CustomView> openView);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MainView<>(this, this::createView);
        setContentView(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (view == null) {
            return;
        }
        view.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (view == null) {
            return;
        }
        view.onResume();
    }

    @Override
    public void onBackPressed() {
        if (view == null) {
            return;
        }
        if (!view.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (view == null) {
            return;
        }
        view.onDestroy();
    }
}