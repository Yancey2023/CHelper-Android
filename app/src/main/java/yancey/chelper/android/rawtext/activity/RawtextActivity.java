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

package yancey.chelper.android.rawtext.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import yancey.chelper.R;
import yancey.chelper.android.common.activity.BaseActivity;
import yancey.chelper.android.common.util.TextWatcherUtil;

/**
 * Json文本组件生成器界面
 */
public class RawtextActivity extends BaseActivity {

    private static final Map<Integer, String> colorToString;
    private static final int COLOR_0 = 0xFF000000;
    private static final int COLOR_1 = 0xFF0000AA;
    private static final int COLOR_2 = 0xFF00AA00;
    private static final int COLOR_3 = 0xFF00AAAA;
    private static final int COLOR_4 = 0xFFAA0000;
    private static final int COLOR_5 = 0xFFAA00AA;
    private static final int COLOR_6 = 0xFFFFAA00;
    private static final int COLOR_7 = 0xFFAAAAAA;
    private static final int COLOR_8 = 0xFF555555;
    private static final int COLOR_9 = 0xFF5555FF;
    private static final int COLOR_a = 0xFF55FF55;
    private static final int COLOR_b = 0xFF55FFFF;
    private static final int COLOR_c = 0xFFFF5555;
    private static final int COLOR_d = 0xFFFF55FF;
    private static final int COLOR_e = 0xFFFFFF55;
    private static final int COLOR_f = 0xFFFFFFFF;
    private static final int COLOR_g = 0xFFDDD605;
    private static final int COLOR_h = 0xFFE3D4D1;
    private static final int COLOR_i = 0xFFCECACA;
    private static final int COLOR_j = 0xFF443A3B;
    private static final int COLOR_m = 0xFF971607;
    private static final int COLOR_n = 0xFFB4684D;
    private static final int COLOR_p = 0xFFDEB12D;
    private static final int COLOR_q = 0xFF47A036;
    private static final int COLOR_s = 0xFF2CBAA8;
    private static final int COLOR_t = 0xFF21497B;
    private static final int COLOR_u = 0xFF9A5CC6;

    static {
        colorToString = new HashMap<>();
        colorToString.put(COLOR_0, "§0");
        colorToString.put(COLOR_1, "§1");
        colorToString.put(COLOR_2, "§2");
        colorToString.put(COLOR_3, "§3");
        colorToString.put(COLOR_4, "§4");
        colorToString.put(COLOR_5, "§5");
        colorToString.put(COLOR_6, "§6");
        colorToString.put(COLOR_7, "§7");
        colorToString.put(COLOR_8, "§8");
        colorToString.put(COLOR_9, "§9");
        colorToString.put(COLOR_a, "§a");
        colorToString.put(COLOR_b, "§b");
        colorToString.put(COLOR_c, "§c");
        colorToString.put(COLOR_d, "§d");
        colorToString.put(COLOR_e, "§e");
        colorToString.put(COLOR_f, "§f");
        colorToString.put(COLOR_g, "§g");
        colorToString.put(COLOR_h, "§h");
        colorToString.put(COLOR_i, "§i");
        colorToString.put(COLOR_j, "§j");
        colorToString.put(COLOR_m, "§m");
        colorToString.put(COLOR_n, "§n");
        colorToString.put(COLOR_p, "§p");
        colorToString.put(COLOR_q, "§q");
        colorToString.put(COLOR_s, "§s");
        colorToString.put(COLOR_t, "§t");
        colorToString.put(COLOR_u, "§u");
    }

    private EditText mEd_text;
    private TextView mTv_preview;
    private GridLayout mGl_colorBoard;
    private boolean isShowColorBoard = true;

    @Override
    protected String gePageName() {
        return "Rawtext";
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_rawtext;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViewById(R.id.back).setOnClickListener(v -> finish());
        mEd_text = findViewById(R.id.text);
        mTv_preview = findViewById(R.id.tv_preview);
        mGl_colorBoard = findViewById(R.id.color_board);
        mEd_text.addTextChangedListener(TextWatcherUtil.onTextChanged(s -> {
            if (!isShowColorBoard) {
                mTv_preview.setText(getRawJson());
            }
        }));
        findViewById(R.id.btn_color_0).setOnClickListener(v -> setColor(COLOR_0));
        findViewById(R.id.btn_color_1).setOnClickListener(v -> setColor(COLOR_1));
        findViewById(R.id.btn_color_2).setOnClickListener(v -> setColor(COLOR_2));
        findViewById(R.id.btn_color_3).setOnClickListener(v -> setColor(COLOR_3));
        findViewById(R.id.btn_color_4).setOnClickListener(v -> setColor(COLOR_4));
        findViewById(R.id.btn_color_5).setOnClickListener(v -> setColor(COLOR_5));
        findViewById(R.id.btn_color_6).setOnClickListener(v -> setColor(COLOR_6));
        findViewById(R.id.btn_color_7).setOnClickListener(v -> setColor(COLOR_7));
        findViewById(R.id.btn_color_8).setOnClickListener(v -> setColor(COLOR_8));
        findViewById(R.id.btn_color_9).setOnClickListener(v -> setColor(COLOR_9));
        findViewById(R.id.btn_color_a).setOnClickListener(v -> setColor(COLOR_a));
        findViewById(R.id.btn_color_b).setOnClickListener(v -> setColor(COLOR_b));
        findViewById(R.id.btn_color_c).setOnClickListener(v -> setColor(COLOR_c));
        findViewById(R.id.btn_color_d).setOnClickListener(v -> setColor(COLOR_d));
        findViewById(R.id.btn_color_e).setOnClickListener(v -> setColor(COLOR_e));
        findViewById(R.id.btn_color_f).setOnClickListener(v -> setColor(COLOR_f));
        findViewById(R.id.btn_color_g).setOnClickListener(v -> setColor(COLOR_g));
        findViewById(R.id.btn_color_h).setOnClickListener(v -> setColor(COLOR_h));
        findViewById(R.id.btn_color_i).setOnClickListener(v -> setColor(COLOR_i));
        findViewById(R.id.btn_color_j).setOnClickListener(v -> setColor(COLOR_j));
        findViewById(R.id.btn_color_m).setOnClickListener(v -> setColor(COLOR_m));
        findViewById(R.id.btn_color_n).setOnClickListener(v -> setColor(COLOR_n));
        findViewById(R.id.btn_color_p).setOnClickListener(v -> setColor(COLOR_p));
        findViewById(R.id.btn_color_q).setOnClickListener(v -> setColor(COLOR_q));
        findViewById(R.id.btn_color_s).setOnClickListener(v -> setColor(COLOR_s));
        findViewById(R.id.btn_color_t).setOnClickListener(v -> setColor(COLOR_t));
        findViewById(R.id.btn_color_u).setOnClickListener(v -> setColor(COLOR_u));
        TextView btn_copy = findViewById(R.id.btn_copy);
        TextView btn_change = findViewById(R.id.btn_change);
        btn_change.setOnClickListener(v -> {
            isShowColorBoard = !isShowColorBoard;
            if (isShowColorBoard) {
                btn_change.setText(R.string.layout_rawtext_preview);
                btn_copy.setVisibility(View.GONE);
                mTv_preview.setVisibility(View.GONE);
                mGl_colorBoard.setVisibility(View.VISIBLE);
            } else {
                btn_change.setText(R.string.layout_rawtext_color);
                mTv_preview.setText(getRawJson());
                btn_copy.setVisibility(View.VISIBLE);
                mTv_preview.setVisibility(View.VISIBLE);
                mGl_colorBoard.setVisibility(View.GONE);
            }
        });
    }

    private void setColor(int color) {
        Editable editable = mEd_text.getText();
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(editable);
        int selectionStart = mEd_text.getSelectionStart();
        int selectionEnd = mEd_text.getSelectionEnd();
        if (selectionStart > selectionEnd) {
            int temp = selectionStart;
            selectionStart = selectionEnd;
            selectionEnd = temp;
        }
        spannableStringBuilder.setSpan(new ForegroundColorSpan(color), selectionStart, selectionEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        mEd_text.setText(spannableStringBuilder);
        mEd_text.setSelection(selectionStart, selectionEnd);
    }

    private String getRawJson() {
        // 获取文本
        SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) mEd_text.getText();
        String string = spannableStringBuilder.toString();
        // 获取颜色
        int[] colors = new int[string.length()];
        for (int i = 0; i < colors.length; i++) {
            ForegroundColorSpan[] spans = spannableStringBuilder.getSpans(i, i + 1, ForegroundColorSpan.class);
            colors[i] = spans.length == 0 ? Color.WHITE : spans[spans.length - 1].getForegroundColor();
        }
        // 输出Json
        StringBuilder stringBuilder = new StringBuilder();
        int indexStart = 0;
        for (int i = 0; i < colors.length; i++) {
            if (i == colors.length - 1) {
                stringBuilder.append(colorToString.get(colors[indexStart])).append(string.substring(indexStart, colors.length));
                break;
            }
            if (colors[i] == colors[indexStart]) {
                continue;
            }
            stringBuilder.append(colorToString.get(colors[indexStart])).append(string.substring(indexStart, i));
            indexStart = i;
        }
        JsonObject result = new JsonObject();
        JsonArray rawText = new JsonArray();
        JsonObject text = new JsonObject();
        text.addProperty("text", stringBuilder.toString());
        rawText.add(text);
        result.add("rawtext", rawText);
        return result.toString();
    }

}
