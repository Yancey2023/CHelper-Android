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
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import yancey.chelper.R;
import yancey.chelper.android.common.util.TextWatcherUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.library.adapter.PublicLibraryListAdapter;
import yancey.chelper.network.library.CommandLibraryAPI;

/**
 * 公共命令库
 */
@SuppressLint("ViewConstructor")
public class PublicLibraryListView extends CustomView {

    private PublicLibraryListAdapter adapter;
    private List<String> libraryNames;
    private EditText ed_search;

    public PublicLibraryListView(@NonNull Context context, @NonNull Consumer<CustomView> openView, @NonNull Environment environment) {
        super(context, openView, environment, R.layout.layout_public_library_list);
    }

    public void update(CharSequence keyword) {
        if (libraryNames == null) {
            return;
        }
        if (TextUtils.isEmpty(keyword)) {
            adapter.setLibraryNames(libraryNames);
        } else {
            adapter.setLibraryNames(libraryNames.stream()
                    .filter(s -> s.contains(ed_search.getText()))
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        View tv_not_allow_upload_in_floating_window = view.findViewById(R.id.tv_public_library_not_allow_upload_in_floating_window);
        View btn_upload = view.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(v -> {
            if (environment == Environment.APPLICATION) {
                openView((context2, openView, environment) -> new PublicLibraryUploadView(context2, openView, environment, libraryNames));
            } else {
                tv_not_allow_upload_in_floating_window.setVisibility(VISIBLE);
                btn_upload.setVisibility(GONE);
            }
        });
        ed_search = view.findViewById(R.id.search);
        ed_search.addTextChangedListener(TextWatcherUtil.onTextChanged(this::update));
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        adapter = new PublicLibraryListAdapter(context, libraryName -> openView((context1, openView, environment) ->
                new PublicLibraryView(context1, openView, environment, libraryName)));
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        CommandLibraryAPI.getAllLibraryNames(result -> post(() -> {
            if (getContext() != null) {
                libraryNames = result;
                update(ed_search.getText());
            }
        }));
    }

}
