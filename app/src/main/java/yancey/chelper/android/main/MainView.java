package yancey.chelper.android.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import yancey.chelper.android.common.view.CustomView;

@SuppressLint("ViewConstructor")
public class MainView extends FrameLayout {

    public MainView(@NonNull Context context, boolean isInFloatingWindow, Runnable shutDown, Runnable hideView) {
        super(context);
        openView(new WritingCommandView(context, isInFloatingWindow, shutDown, hideView, this::openView));
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

}
