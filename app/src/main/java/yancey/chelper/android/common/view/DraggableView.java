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

package yancey.chelper.android.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
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

    public DraggableView(@NonNull Context context) {
        super(context);
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
        int width = ((ViewGroup) getParent()).getWidth();
        int height = ((ViewGroup) getParent()).getHeight();
        if (width != 0 && height != 0) {
            if (left < 0) {
                right = right - left;
                left = 0;
            } else if (right > width) {
                left = width - right + left;
                right = width;
            }
            if (top < 0) {
                bottom = bottom - top;
                top = 0;
            } else if (bottom > height) {
                top = height - bottom + top;
                bottom = height;
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
