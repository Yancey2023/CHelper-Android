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

package yancey.chelper.android.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Objects;

import yancey.chelper.R;

/**
 * 对话框，从多个内容选择一个
 */
public class ChoosingDialog extends Dialog {

    private final Context context;
    /**
     * 可选择的内容
     */
    private final String[] strings;
    /**
     * 选择了内容之后要执行的事件
     */
    private final OnChooseListener onChooseListener;

    /**
     * @param context          上下文
     * @param strings          可选择的内容
     * @param onChooseListener 选择了内容之后要执行的事件
     */
    public ChoosingDialog(@NonNull Context context, String[] strings, OnChooseListener onChooseListener) {
        super(context);
        this.context = context;
        this.strings = strings;
        this.onChooseListener = onChooseListener;
    }

    /**
     * 创建界面
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scrollView = new ScrollView(context);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundResource(R.color.background);
        scrollView.addView(linearLayout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp50 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, displayMetrics);
        float sp20 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, displayMetrics);
        for (int i = 0; i < strings.length; i++) {
            TextView textView = new TextView(context);
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp50);
            textView.setLayoutParams(marginLayoutParams);
            textView.setGravity(Gravity.CENTER);
            textView.setBackgroundResource(R.drawable.bg_ripple);
            textView.setText(strings[i]);
            textView.setTextColor(getContext().getColor(R.color.text_main));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp20);
            int finalI = i;
            textView.setOnClickListener(view -> {
                onChooseListener.onChoose(finalI);
                dismiss();
            });
            linearLayout.addView(textView);
        }
        setContentView(scrollView);
        // 因为在界面中定义的宽度无效，所以在代码中把宽度设置为0.95倍屏幕宽度，并把背景设置透明
        Window window = Objects.requireNonNull(getWindow());
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams attributes = window.getAttributes();
        Point point = new Point();
        window.getWindowManager().getDefaultDisplay().getSize(point);
        attributes.width = (int) (((double) point.x) * 0.95d);
        window.setAttributes(attributes);
    }

    public interface OnChooseListener {

        /**
         * 当内容被选择了要执行的事件
         * @param which 第几个被选择了，从0开始
         */
        void onChoose(int which);
    }
}
