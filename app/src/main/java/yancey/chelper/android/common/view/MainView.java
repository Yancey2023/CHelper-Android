/**
 * It is part of CHelper. CHelper a command helper for Minecraft Bedrock Edition.
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
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressLint("ViewConstructor")
public class MainView<T extends CustomView> extends FrameLayout {

    private final CustomView.Environment environment;

    public interface ViewInterface<T> {

        T createView(@NonNull Consumer<CustomView> openView, @NonNull Supplier<Boolean> backView);
    }

    public MainView(
            @NonNull Context context,
            @NonNull CustomView.Environment environment,
            @NonNull ViewInterface<T> createView
    ) {
        super(context);
        this.environment = environment;
        openView(createView.createView(this::openView, this::backView));
    }

    public void openView(@NonNull CustomView view) {
        addView(view);
        int index = getChildCount() - 2;
        if (index >= 0) {
            ((CustomView) getChildAt(index)).onPause();
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
            return false;
        }
        View currentView = getChildAt(childCount - 1);
        if (((CustomView) currentView).onBackPressed()) {
            return true;
        }
        ((CustomView) currentView).onPause();
        CustomView backView = (CustomView) getChildAt(childCount - 2);
        backView.onResume();
        removeViewAt(childCount - 1);
        return true;
    }

    public void onPause() {
        clearFocus();
        ((CustomView) getChildAt(getChildCount() - 1)).onPause();
    }

    public void onResume() {
        ((CustomView) getChildAt(getChildCount() - 1)).onResume();
        if (environment == CustomView.Environment.FLOATING_WINDOW) {
            requestFocus();
        }
    }

    public boolean onBackPressed() {
        return backView();
    }

    public void onDestroy() {
        clearFocus();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((CustomView) getChildAt(i)).onDestroy();
        }
        removeAllViews();
    }

}
