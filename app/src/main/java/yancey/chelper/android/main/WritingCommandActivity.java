package yancey.chelper.android.main;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import yancey.chelper.android.settings.Settings;
import yancey.chelper.core.CHelperCore;
import yancey.chelper.core.CHelperGuiCore;

public class WritingCommandActivity extends AppCompatActivity {

    private CHelperGuiCore core;
    private MainView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            core = new CHelperGuiCore(CHelperCore.fromAssets(getAssets(), Settings.getInstance(this).getCpackPath(this)));
        } catch (Exception e) {
            Toast.makeText(this, "资源包加载失败", Toast.LENGTH_SHORT).show();
            return;
        }
        view = new MainView(this, core, false, this::finishAffinity, null);
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
    public void finish() {
        super.finish();
        if (core == null) {
            return;
        }
        core.close();
    }
}