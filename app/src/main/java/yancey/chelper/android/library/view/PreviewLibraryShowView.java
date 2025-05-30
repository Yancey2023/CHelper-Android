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

package yancey.chelper.android.library.view;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import yancey.chelper.R;
import yancey.chelper.android.common.view.BaseView;
import yancey.chelper.android.library.adapter.LibraryShowAdapter;
import yancey.chelper.network.library.data.LibraryFunction;

/**
 * 命令库显示视图
 */
@SuppressLint("ViewConstructor")
public class PreviewLibraryShowView extends BaseView {

    private final View btn_like;
    private final TextView tv_like_count;

    @SuppressLint("HardwareIds")
    public PreviewLibraryShowView(
            @NonNull CustomContext customContext,
            @NonNull LibraryFunction libraryFunction
    ) {
        super(customContext, R.layout.layout_library_show);
        view.findViewById(R.id.back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        btn_like = view.findViewById(R.id.btn_like);
        tv_like_count = view.findViewById(R.id.tv_like_count);
        TextView tv_name = view.findViewById(R.id.name);
        LibraryShowAdapter adapter = new LibraryShowAdapter(context, libraryFunction);
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        libraryFunction.is_liked = false;
        libraryFunction.like_count = 520;
        btn_like.setOnClickListener(view1 -> {
            if (libraryFunction.is_liked) {
                libraryFunction.like_count--;
            } else {
                libraryFunction.like_count++;
            }
            libraryFunction.is_liked = !libraryFunction.is_liked;
            updateLike(libraryFunction);
        });
        tv_name.setText(libraryFunction.name);
        updateLike(libraryFunction);
    }

    @Override
    protected String gePageName() {
        return "PreviewLibraryShowView";
    }

    void updateLike(LibraryFunction libraryFunction) {
        if (Boolean.TRUE.equals(libraryFunction.is_liked)) {
            btn_like.setBackgroundResource(R.drawable.heart_filled);
        } else {
            btn_like.setBackgroundResource(R.drawable.heart);
        }
        tv_like_count.setText(String.valueOf(libraryFunction.like_count));
    }

}
