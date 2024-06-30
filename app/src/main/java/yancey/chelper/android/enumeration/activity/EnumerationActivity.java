package yancey.chelper.android.enumeration.activity;

import java.util.function.Consumer;

import yancey.chelper.android.common.activity.CustomActivity;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.enumeration.view.EnumerationView;

public class EnumerationActivity extends CustomActivity<EnumerationView> {

    @Override
    protected EnumerationView createView(Consumer<CustomView> openView) {
        return new EnumerationView(this, openView);
    }

}