package yancey.chelper.android.welcome;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.common.view.DraggableView;
import yancey.chelper.android.main.MainView;
import yancey.chelper.core.CHelperGuiCore;

@SuppressLint("ViewConstructor")
public class FloatMainView extends FrameLayout {

    private final int iconEdgeLength;
    private final DraggableView iconView;
    private final MainView mainView;

    public FloatMainView(@NonNull Context context, CHelperGuiCore core, Runnable shutDown, int iconEdgeLength) {
        super(context);
        this.iconEdgeLength = iconEdgeLength;
        iconView = new DraggableView(context);
        iconView.setImageResource(R.drawable.pack_icon);
        iconView.setLayoutParams(new LayoutParams(iconEdgeLength, iconEdgeLength, Gravity.START | Gravity.TOP));
        Objects.requireNonNull(core);
        mainView = new MainView(context, core,true, shutDown, iconView::callOnClick);
        addView(mainView);
        addView(iconView);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (!mainView.backView()) {
                // 这个方法有bug
//                if (!keyBoardWatcher.isKeyboardShow(this)) {
//                    iconView.callOnClick();
//                }
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

}
