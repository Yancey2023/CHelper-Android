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
import androidx.annotation.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressLint("ViewConstructor")
public class MainView<T extends CustomView> extends FrameLayout {

    @NonNull
    private final CustomView.Environment environment;
    @Nullable
    private final Runnable setFocusable;

    public MainView(
            @NonNull Context context,
            @NonNull CustomView.Environment environment,
            @Nullable Runnable setFocusable,
            @NonNull Function<Consumer<CustomView>, T> function
    ) {
        super(context);
        this.environment = environment;
        this.setFocusable = setFocusable;
        openView(function.apply(this::openView));
    }

    public void openView(@NonNull CustomView view) {
        addView(view);
        int index = getChildCount() - 2;
        if (index >= 0) {
            ((CustomView) getChildAt(index)).onPause();
        }
//        if (environment == CustomView.Environment.FLOATING_WINDOW && setFocusable != null) {
//            setFocusable.run();
//        }
        view.requestFocus();
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
//        if (environment == CustomView.Environment.FLOATING_WINDOW && setFocusable != null) {
//            setFocusable.run();
//            requestFocus();
//        }
        return true;
    }

    public void onPause() {
//        clearFocus();
        ((CustomView) getChildAt(getChildCount() - 1)).onPause();
    }

    public void onResume() {
        ((CustomView) getChildAt(getChildCount() - 1)).onResume();
//        requestFocus();
    }

    public boolean onBackPressed() {
        return backView();
    }

    public void onDestroy() {
//        clearFocus();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            ((CustomView) getChildAt(i)).onDestroy();
        }
        removeAllViews();
    }

}
