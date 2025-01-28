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

package yancey.chelper.android.library.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;
import java.util.function.Consumer;

import yancey.chelper.R;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.library.adapter.PublicLibraryAdapter;
import yancey.chelper.network.data.Library;

/**
 * 公共命令库
 */
@SuppressLint("ViewConstructor")
public class PublicLibraryPreviewView extends CustomView {

    public PublicLibraryPreviewView(@NonNull Context context, @NonNull Consumer<CustomView> openView, @NonNull Environment environment, @NonNull Library library) {
        super(context, openView, environment, R.layout.layout_public_library, library);
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        Library library = (Library) Objects.requireNonNull(privateData);
        TextView tv_name = view.findViewById(R.id.name);
        tv_name.setText(library.name);
        TextView tv_description = view.findViewById(R.id.description);
        tv_description.setText(library.description);
        TextView tv_author = view.findViewById(R.id.author);
        tv_author.setText(library.author);
        PublicLibraryAdapter adapter = new PublicLibraryAdapter(context, library.commands);
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
    }

}
