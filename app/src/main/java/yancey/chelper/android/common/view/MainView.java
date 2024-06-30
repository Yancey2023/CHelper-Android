package yancey.chelper.android.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressLint("ViewConstructor")
public class MainView<T extends CustomView> extends FrameLayout {

    public MainView(@NonNull Context context, Function<Consumer<CustomView>, T> function) {
        super(context);
        openView(function.apply(this::openView));
    }

    public void openView(CustomView view) {
        addView(view);
        int index = getChildCount() - 2;
        if (index >= 0) {
            ((CustomView) getChildAt(index)).onPause();
        }
    }

    public boolean backView() {
        int childCount = getChildCount();
        if (childCount <= 1) {
            return false;
        }
        if (((CustomView) getChildAt(childCount - 1)).onBackPressed()) {
            return true;
        }
        ((CustomView) getChildAt(childCount - 1)).onPause();
        ((CustomView) getChildAt(childCount - 2)).onResume();
        removeViewAt(childCount - 1);
        return true;
    }

    public void onPause() {
        ((CustomView) getChildAt(getChildCount() - 1)).onPause();
    }

    public void onResume() {
        ((CustomView) getChildAt(getChildCount() - 1)).onResume();
    }

    public boolean onBackPressed() {
        return backView();
    }

    public void onDestroy() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((CustomView) getChildAt(i)).onDestroy();
        }
        removeAllViews();
    }

}
