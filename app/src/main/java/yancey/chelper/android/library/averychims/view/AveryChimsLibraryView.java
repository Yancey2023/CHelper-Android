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

package yancey.chelper.android.library.averychims.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
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
import yancey.chelper.android.library.averychims.adapter.AveryChimsLibraryAdapter;
import yancey.chelper.network.api.library.averychims.CommandLibraryAPI;

/**
 * Avery Chims的命令库
 */
@SuppressLint("ViewConstructor")
public class AveryChimsLibraryView extends CustomView {

    private AveryChimsLibraryAdapter adapter;

    public AveryChimsLibraryView(
            @NonNull Context context,
            @NonNull Consumer<CustomView> openView,
            @NonNull Supplier<Boolean> bacKView,
            @NonNull Environment environment,
            @NonNull String libraryName
    ) {
        super(context, openView, bacKView, environment, R.layout.layout_averychims_library, libraryName);
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        String libraryName = (String) Objects.requireNonNull(privateData);
        TextView tv_name = view.findViewById(R.id.name);
        tv_name.setText(libraryName);
        adapter = new AveryChimsLibraryAdapter(context);
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        Handler handler = new Handler(Looper.getMainLooper());
        CommandLibraryAPI.getDescriptionAndAuthor(libraryName, library -> handler.post(() ->
                adapter.setDescriptionAndAuthor(library.description, library.author)));
        CommandLibraryAPI.getContent(libraryName, library -> post(() -> adapter.setCommands(library.commands)));
    }

}
