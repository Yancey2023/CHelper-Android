package yancey.chelper.android.completion.activity;

import java.util.function.Consumer;

import yancey.chelper.android.common.activity.CustomActivity;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.completion.view.CompletionView;

public class CompletionActivity extends CustomActivity<CompletionView> {

    @Override
    protected CompletionView createView(Consumer<CustomView> openView) {
        return new CompletionView(this, this::finishAffinity, null, openView);
    }

}