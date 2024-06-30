package yancey.chelper.android.old2new.activity;

import java.util.function.Consumer;

import yancey.chelper.android.common.activity.CustomActivity;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.old2new.view.Old2NewView;

public class Old2NewActivity extends CustomActivity<Old2NewView> {

    @Override
    protected Old2NewView createView(Consumer<CustomView> openView) {
        return new Old2NewView(this, openView);
    }

}