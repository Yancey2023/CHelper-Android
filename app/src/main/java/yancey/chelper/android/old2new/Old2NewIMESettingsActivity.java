package yancey.chelper.android.old2new;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import yancey.chelper.R;
import yancey.chelper.android.common.dialog.IsConfirmDialog;

public class Old2NewIMESettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_old2new_ime_settings);
        findViewById(R.id.btn_start_ime1).setOnClickListener(v -> {
            if ((checkFeatureInputMethods())) {
                startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
            }
        });
        findViewById(R.id.btn_start_ime2).setOnClickListener(v -> {
            if (checkFeatureInputMethods()) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showInputMethodPicker();
            }
        });
    }

    private boolean checkFeatureInputMethods() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_INPUT_METHODS)) {
            return true;
        } else {
            new IsConfirmDialog(getApplicationContext(), false)
                    .message(getString(R.string.old2new_ime_warn))
                    .show();
            return false;
        }
    }

}
