package yancey.chelper.android.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import java.util.function.Consumer;

import yancey.chelper.R;
import yancey.chelper.android.common.view.CustomView;

@SuppressLint("ViewConstructor")
public class SettingsView extends CustomView {

    private SwitchCompat isCheckingBySelection, isHideWindowWhenCopying, isSavingWhenPausing, isCrowed;

    public SettingsView(@NonNull Context context, Consumer<CustomView> openView, boolean isInFloatingWindow) {
        super(context, openView, isInFloatingWindow, R.layout.layout_settings);
    }

    @Override
    public void onCreateView(Context context, View view) {
        isCheckingBySelection = view.findViewById(R.id.cb_is_checking_by_selection);
        isHideWindowWhenCopying = view.findViewById(R.id.cb_is_hide_window_when_copying);
        isSavingWhenPausing = view.findViewById(R.id.cb_is_saving_when_pausing);
        isCrowed = view.findViewById(R.id.cb_is_crowed);
        isCheckingBySelection.setChecked(Settings.getInstance(context).isCheckingBySelection);
        isHideWindowWhenCopying.setChecked(Settings.getInstance(context).isHideWindowWhenCopying);
        isSavingWhenPausing.setChecked(Settings.getInstance(context).isSavingWhenPausing);
        isCrowed.setChecked(Settings.getInstance(context).isCrowed);
    }

    @Override
    public void onPause() {
        super.onPause();
        Settings settings = Settings.getInstance(getContext());
        settings.isCheckingBySelection = isCheckingBySelection.isChecked();
        settings.isHideWindowWhenCopying = isHideWindowWhenCopying.isChecked();
        settings.isSavingWhenPausing = isSavingWhenPausing.isChecked();
        settings.isCrowed = isCrowed.isChecked();
        settings.save(getContext());
    }


}
