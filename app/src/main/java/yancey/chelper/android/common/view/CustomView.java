package yancey.chelper.android.common.view;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.function.Consumer;

public abstract class CustomView extends FrameLayout {

    protected final boolean isInFloatingWindow;
    protected final Consumer<CustomView> openView;

    public CustomView(@NonNull Context context, Consumer<CustomView> openView, boolean isInFloatingWindow, @LayoutRes int layoutId) {
        super(context);
        this.openView = openView;
        this.isInFloatingWindow = isInFloatingWindow;
        View view = LayoutInflater.from(context).inflate(layoutId, this, false);
        addView(view);
        onCreateView(context, view);
    }

    public abstract void onCreateView(Context context, View view);

    public void onPause() {

    }

    public void onResume() {

    }

    public boolean onBackPressed() {
        return false;
    }

    protected void openView(CreateView createView) {
        ((InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getWindowToken(), 0);
        openView.accept(createView.createView(getContext(), openView, isInFloatingWindow));
    }

    public interface CreateView {
        CustomView createView(Context context, Consumer<CustomView> openView, boolean isInFloatingWindow);
    }

}
