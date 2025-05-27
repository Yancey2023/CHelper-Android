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
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.function.Function;
import java.util.function.Supplier;

@SuppressLint("ViewConstructor")
public class MainView<T extends CustomView<?>> extends FrameLayout {

    private final @NonNull CustomView.Environment environment;
    private final @NonNull Supplier<Boolean> backView;

    public MainView(
            @NonNull Context context,
            @NonNull CustomView.Environment environment,
            @NonNull Function<CustomView.CustomContext, T> createView,
            @NonNull Supplier<Boolean> backView
    ) {
        super(context);
        this.environment = environment;
        this.backView = backView;
        openView(createView.apply(new CustomView.CustomContext(context, this::openView, this::backView, environment)));
    }

    public CustomView<?> getChildAt(int index) {
        return (CustomView<?>) super.getChildAt(index);
    }

    public void openView(@NonNull CustomView<?> view) {
        addView(view);
        int index = getChildCount() - 2;
        if (index >= 0) {
            getChildAt(index).onPause();
        }
        if (environment == CustomView.Environment.FLOATING_WINDOW) {
            view.requestFocus();
        } else {
            clearFocus();
        }
    }

    public boolean backView() {
        int childCount = getChildCount();
        if (childCount <= 1) {
            return backView.get();
        }
        CustomView<?> currentView = getChildAt(childCount - 1);
        if (currentView.onBackPressed()) {
            return true;
        }
        currentView.onPause();
        currentView.onDestroy();
        CustomView<?> backView = getChildAt(childCount - 2);
        backView.onResume();
        removeViewAt(childCount - 1);
        return true;
    }

    public void onPause() {
        clearFocus();
        getChildAt(getChildCount() - 1).onPause();
    }

    public void onResume() {
        getChildAt(getChildCount() - 1).onResume();
        if (environment == CustomView.Environment.FLOATING_WINDOW) {
            requestFocus();
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean onBackPressed() {
        return backView();
    }

    public void onDestroy() {
        clearFocus();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            getChildAt(i).onDestroy();
        }
        removeAllViews();
    }

}
