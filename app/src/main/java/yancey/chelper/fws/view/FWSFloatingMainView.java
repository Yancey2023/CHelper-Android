/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
 * Copyright (C) 2025  Yancey
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package yancey.chelper.fws.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.function.Function;

/**
 * 悬浮窗主视图
 */
@SuppressLint("ViewConstructor")
public class FWSFloatingMainView<T extends FWSView> extends FrameLayout {

    private final int iconEdgeLength;
    private final DraggableView iconView;
    private final FWSMainView<T> fwsMainView;
    private final OnBackPressedDispatcher onBackPressedDispatcher;

    public FWSFloatingMainView(@NonNull Context context, @NonNull Function<FWSView.CustomContext, T> createRootView, DraggableView iconView, int iconEdgeLength) {
        super(context);
        this.iconEdgeLength = iconEdgeLength;
        this.iconView = iconView;
        iconView.setLayoutParams(new LayoutParams(iconEdgeLength, iconEdgeLength, Gravity.START | Gravity.TOP));
        onBackPressedDispatcher = new OnBackPressedDispatcher(iconView::callOnClick);
        fwsMainView = new FWSMainView<>(
                context,
                FWSView.Environment.FLOATING_WINDOW,
                createRootView,
                onBackPressedDispatcher
        );
        ViewCompat.setOnApplyWindowInsetsListener(this, (v, insets) -> {
            Insets stateBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            iconView.setInsets(new Rect(stateBars.left, stateBars.top, stateBars.right, stateBars.bottom));
            return insets;
        });
        addView(fwsMainView);
        addView(iconView);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            onBackPressedDispatcher.onBackPressed();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void setIconPosition(int x, int y) {
        WindowInsetsCompat insets = ViewCompat.getRootWindowInsets(this);
        if (insets != null) {
            Insets stateBars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            x += stateBars.left;
            y += stateBars.top;
        }
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
        fwsMainView.onPause();
    }

    public void onResume() {
        fwsMainView.onResume();
    }

    public void onDestroy() {
        fwsMainView.onDestroy();
    }

}
