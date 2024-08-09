package yancey.chelper.android.completion.activity;

import java.util.function.Consumer;

import yancey.chelper.android.common.activity.CustomActivity;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.completion.view.SettingsView;
import yancey.chelper.android.enumeration.view.EnumerationView;

/**
 * 设置界面
 */
public class SettingsActivity extends CustomActivity<SettingsView> {

    @Override
    protected SettingsView createView(Consumer<CustomView> openView) {
        return new SettingsView(this, openView);
    }

}