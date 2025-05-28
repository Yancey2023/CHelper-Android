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

package yancey.chelper.android.common.dialog;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

import yancey.chelper.R;

/**
 * 对话框，从多个内容选择一个
 */
public class ChoosingDialog extends FixedDialog {

    /**
     * 可选择的内容
     */
    private final List<String> strings;
    /**
     * 选择了内容之后要执行的事件
     */
    private final OnChooseListener onChooseListener;

    public ChoosingDialog(@NonNull Context context, List<String> strings, OnChooseListener onChooseListener) {
        super(context);
        this.strings = strings;
        this.onChooseListener = onChooseListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getContext();
        ScrollView scrollView = new ScrollView(context);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundResource(R.color.background_component_no_translate);
        scrollView.addView(linearLayout);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp50 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, displayMetrics);
        float sp20 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, displayMetrics);
        for (int i = 0; i < strings.size(); i++) {
            TextView textView = getTextView(context, i, dp50, sp20);
            linearLayout.addView(textView);
        }
        setContentView(scrollView);
    }

    @NonNull
    private TextView getTextView(Context context, int i, int height, float textSize) {
        TextView textView = new TextView(context);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        textView.setLayoutParams(marginLayoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundResource(R.drawable.bg_ripple);
        textView.setText(strings.get(i));
        textView.setTextColor(getContext().getColor(R.color.text_main));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setOnClickListener(view -> {
            onChooseListener.onChoose(i);
            dismiss();
        });
        return textView;
    }

    public interface OnChooseListener {

        void onChoose(int which);
    }
}
