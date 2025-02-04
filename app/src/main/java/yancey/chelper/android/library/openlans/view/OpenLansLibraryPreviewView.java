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

package yancey.chelper.android.library.openlans.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import yancey.chelper.R;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.library.openlans.adapter.OpenLansLibraryAdapter;
import yancey.chelper.network.data.openlans.LibraryFunction;

/**
 * OpenLANS的命令库
 */
@SuppressLint("ViewConstructor")
public class OpenLansLibraryPreviewView extends CustomView {

    private View btn_like;
    private TextView tv_like_count;

    public OpenLansLibraryPreviewView(
            @NonNull Context context,
            @NonNull Consumer<CustomView> openView,
            @NonNull Supplier<Boolean> backView,
            @NonNull Environment environment,
            @NonNull LibraryFunction library
    ) {
        super(context, openView, backView, environment, R.layout.layout_openlans_library, library);
    }

    void updateLike(LibraryFunction libraryFunction) {
        if (Boolean.TRUE.equals(libraryFunction.is_liked)) {
            btn_like.setBackgroundResource(R.drawable.icon_liked);
        } else {
            btn_like.setBackgroundResource(R.drawable.icon_like);
        }
        tv_like_count.setText(String.valueOf(libraryFunction.like_count));
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        LibraryFunction libraryFunction = (LibraryFunction) Objects.requireNonNull(privateData);
        libraryFunction.is_liked = false;
        libraryFunction.like_count = 520;
        btn_like = view.findViewById(R.id.btn_like);
        tv_like_count = view.findViewById(R.id.tv_like_count);
        updateLike(libraryFunction);
        btn_like.setOnClickListener(view1 -> {
            if (libraryFunction.is_liked) {
                libraryFunction.like_count--;
            } else {
                libraryFunction.like_count++;
            }
            libraryFunction.is_liked = !libraryFunction.is_liked;
            updateLike(libraryFunction);
        });
        TextView tv_name = view.findViewById(R.id.name);
        tv_name.setText(libraryFunction.name);
        OpenLansLibraryAdapter adapter = new OpenLansLibraryAdapter(context, libraryFunction);
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
    }

}
