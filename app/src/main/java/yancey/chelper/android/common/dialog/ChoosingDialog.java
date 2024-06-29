package yancey.chelper.android.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
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

public class ChoosingDialog extends Dialog {

    private final Context context;
    private final String[] strings;
    private final OnChooseListener onChooseListener;

    public ChoosingDialog(@NonNull Context context, String[] strings, OnChooseListener onChooseListener) {
        super(context);
        this.context = context;
        this.strings = strings;
        this.onChooseListener = onChooseListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scrollView = new ScrollView(context);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scrollView.setLayoutParams(new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(Color.WHITE);
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
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, sp20);
            int finalI = i;
            textView.setOnClickListener(view -> {
                onChooseListener.onChoose(finalI);
                dismiss();
            });
            linearLayout.addView(textView);
        }
        setContentView(scrollView);
        Window window = Objects.requireNonNull(getWindow());
        window.setDimAmount(0.5F);
        WindowManager.LayoutParams attributes = window.getAttributes();
        Point point = new Point();
        window.getWindowManager().getDefaultDisplay().getSize(point);
        attributes.width = (int) (((double) point.x) * 0.95d);
        window.setAttributes(attributes);
    }

    public interface OnChooseListener {

        void onChoose(int which);
    }
}
