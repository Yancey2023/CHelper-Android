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
import android.view.MotionEvent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * 可拖拽的视图
 */
public class DraggableView extends AppCompatImageView {

    private float downX;
    private float downY;
    private long downTimeStart;
    private int left, top, right, bottom;
    private Rect insets;

    public DraggableView(@NonNull Context context) {
        super(context);
    }

    public void setInsets(Rect insets) {
        this.insets = insets;
    }

    public Rect getInsets() {
        return insets;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        performClick();
        if (this.isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN -> {
                    downX = event.getX();
                    downY = event.getY();
                    downTimeStart = System.currentTimeMillis();
                }
                case MotionEvent.ACTION_MOVE -> {
                    int xDistance = (int) (event.getX() - downX);
                    int yDistance = (int) (event.getY() - downY);
                    if (xDistance != 0 && yDistance != 0) {
                        customLayout(
                                left + xDistance,
                                top + yDistance,
                                right + xDistance,
                                bottom + yDistance
                        );
                    }
                }
                case MotionEvent.ACTION_UP -> {
                    long downTimeEnd = System.currentTimeMillis();
                    if (downTimeEnd - downTimeStart < 200 && (Math.abs(downX - event.getX()) + Math.abs(downY - event.getY()) == 0)) {
                        callOnClick();
                    }
                }
            }
            return true;
        }
        return false;
    }

    public void customLayout(int left, int top, int right, int bottom) {
        ViewGroup parent = (ViewGroup) getParent();
        int width = parent.getWidth();
        int height = parent.getHeight();
        if (insets == null) {
            insets = new Rect(0, 0, 0, 0);
        }
        if (width != 0 && height != 0) {
            int borderLeft = insets.left;
            int borderRight = width - insets.right;
            int borderTop = insets.top;
            int borderBottom = height - insets.bottom;
            if (left < borderLeft) {
                right = right - left + borderLeft;
                left = borderLeft;
            } else if (right > borderRight) {
                left = borderRight - right + left;
                right = borderRight;
            }
            if (top < borderTop) {
                bottom = bottom - top + borderTop;
                top = borderTop;
            } else if (bottom > borderBottom) {
                top = borderBottom - bottom + top;
                bottom = borderBottom;
            }
        }
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        super.layout(left, top, right, bottom);
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        super.layout(this.left, this.top, this.right, this.bottom);
    }

    @Override
    public float getX() {
        return left;
    }

    @Override
    public float getY() {
        return top;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(false, this.left, this.top, this.right, this.bottom);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean performClick() {
        return true;
    }

}
