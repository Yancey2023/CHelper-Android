package yancey.chelper.android.common.view;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class CustomView extends FrameLayout {

    protected final Consumer<CustomView> openView;

    public CustomView(@NonNull Context context, Consumer<CustomView> openView, @LayoutRes int layoutId) {
        super(context);
        this.openView = openView;
        View view = LayoutInflater.from(context).inflate(layoutId, this, false);
        addView(view);
        onCreateView(context, view);
    }

    public <T> CustomView(@NonNull Context context, Consumer<CustomView> openView, @LayoutRes int layoutId, Object data) {
        super(context);
        this.openView = openView;
        View view = LayoutInflater.from(context).inflate(layoutId, this, false);
        addView(view);
        onCreateView(context, view, data);
    }

    public void onCreateView(Context context, View view) {

    }

    public void onCreateView(Context context, View view, Object data) {

    }

    public void onPause() {

    }

    public void onResume() {

    }

    public void onDestroy() {

    }

    public boolean onBackPressed() {
        return false;
    }

    protected void openView(BiFunction<Context, Consumer<CustomView>, CustomView> createView) {
        ((InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getWindowToken(), 0);
        openView.accept(createView.apply(getContext(), openView));
    }

}
