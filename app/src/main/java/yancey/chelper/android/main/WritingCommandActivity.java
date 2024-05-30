package yancey.chelper.android.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class WritingCommandActivity extends AppCompatActivity {

    private MainView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new MainView(this, false, this::finishAffinity, null);
        setContentView(view);
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
    }

    @Override
    public void onBackPressed() {
        if (!view.onBackPressed()) {
            super.onBackPressed();
        }
    }

}