package yancey.chelper.android.common.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.function.Consumer;

import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.common.view.MainView;

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