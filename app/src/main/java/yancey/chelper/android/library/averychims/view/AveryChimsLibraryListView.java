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
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import yancey.chelper.R;
import yancey.chelper.android.common.util.TextWatcherUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.library.averychims.adapter.AveryChimsLibraryListAdapter;
import yancey.chelper.network.api.library.averychims.CommandLibraryAPI;

/**
 * Avery Chims的命令库
 */
@SuppressLint("ViewConstructor")
public class AveryChimsLibraryListView extends CustomView {

    private AveryChimsLibraryListAdapter adapter;
    private List<String> libraryNames;
    private EditText ed_search;

    public AveryChimsLibraryListView(
            @NonNull Context context,
            @NonNull Consumer<CustomView> openView,
            @NonNull Supplier<Boolean> bacKView,
            @NonNull Environment environment
    ) {
        super(context, openView, bacKView, environment, R.layout.layout_averychims_library_list);
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
        View tv_not_allow_upload_in_floating_window = view.findViewById(R.id.tv_library_not_allow_upload_in_floating_window);
        View btn_upload = view.findViewById(R.id.btn_upload);
        btn_upload.setOnClickListener(v -> {
            if (environment == Environment.APPLICATION) {
                openView((context2, openView, backView, environment) -> new AveryChimsLibraryUploadView(context2, openView, backView, environment, libraryNames));
            } else {
                tv_not_allow_upload_in_floating_window.setVisibility(VISIBLE);
                btn_upload.setVisibility(GONE);
            }
        });
        ed_search = view.findViewById(R.id.search);
        ed_search.addTextChangedListener(TextWatcherUtil.onTextChanged(this::update));
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        adapter = new AveryChimsLibraryListAdapter(context, libraryName -> openView((context1, openView, backView, environment) ->
                new AveryChimsLibraryView(context1, openView, backView, environment, libraryName)));
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        Handler handler = new Handler(Looper.getMainLooper());
        CommandLibraryAPI.getAllLibraryNames(result -> handler.post(() -> {
            libraryNames = result;
            update(ed_search.getText());
        }));
    }

}
