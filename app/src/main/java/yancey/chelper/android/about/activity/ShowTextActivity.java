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

package yancey.chelper.android.about.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import yancey.chelper.R;

/**
 * 文本展示界面
 */
public class ShowTextActivity extends AppCompatActivity {

    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_text);
        Bundle extras = Objects.requireNonNull(getIntent().getExtras());
        String title = extras.getString(TITLE);
        String content = extras.getString(CONTENT);
        View btn_back = findViewById(R.id.back);
        TextView tv_title = findViewById(R.id.title);
        TextView tv_content = findViewById(R.id.content);
        btn_back.setOnClickListener(v -> finish());
        tv_title.setText(title);
        tv_content.setText(content);
    }

}
