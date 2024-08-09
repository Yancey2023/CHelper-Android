package yancey.chelper.android.welcome.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import yancey.chelper.R;
import yancey.chelper.android.common.view.DraggableView;
import yancey.chelper.android.common.view.MainView;
import yancey.chelper.android.completion.view.CompletionView;

/**
 * 悬浮窗
 */
@SuppressLint("ViewConstructor")
public class FloatingMainView extends FrameLayout {

    private final int iconEdgeLength;
    private final DraggableView iconView;
    private final MainView<CompletionView> mainView;

    public FloatingMainView(@NonNull Context context, Runnable shutDown, int iconEdgeLength) {
        super(context);
        this.iconEdgeLength = iconEdgeLength;
        iconView = new DraggableView(context);
        iconView.setImageResource(R.drawable.pack_icon);
        iconView.setLayoutParams(new LayoutParams(iconEdgeLength, iconEdgeLength, Gravity.START | Gravity.TOP));
        mainView = new MainView<>(context, openView -> new CompletionView(context, shutDown, iconView::callOnClick, openView));
        addView(mainView);
        addView(iconView);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (!mainView.backView()) {
                return super.dispatchKeyEvent(event);
            }
        }
        return super.dispatchKeyEvent(event);
    }

    public void setIconPosition(int x, int y) {
        iconView.customLayout(x, y, x + iconEdgeLength, y + iconEdgeLength);
    }

    public float getIconViewX() {
        return iconView.getX();
    }

    public float getIconViewY() {
        return iconView.getY();
    }

    public void setOnIconClickListener(@NonNull Runnable onIconClickListener) {
        iconView.setOnClickListener(v -> onIconClickListener.run());
    }

    public void onPause() {
        mainView.onPause();
    }

    public void onResume() {
        mainView.onResume();
    }

    public void onDestroy() {
        mainView.onDestroy();
    }

}
