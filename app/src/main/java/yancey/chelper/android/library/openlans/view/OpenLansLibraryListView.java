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
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
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
import yancey.chelper.android.common.dialog.InputStringDialog;
import yancey.chelper.android.common.util.TextWatcherUtil;
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.library.openlans.adapter.OpenLansLibraryListAdapter;
import yancey.chelper.network.api.library.openlans.CommandLabAPI;
import yancey.chelper.network.data.openlans.LibraryFunction;
import yancey.chelper.network.data.openlans.LibraryFunctions;

/**
 * OpenLANS的命令库
 */
@SuppressLint("ViewConstructor")
public class OpenLansLibraryListView extends CustomView {

    private OpenLansLibraryListAdapter adapter;
    private List<LibraryFunction> libraryFunctions;
    private EditText ed_search;
    private boolean isDirty = false;

    public OpenLansLibraryListView(
            @NonNull Context context,
            @NonNull Consumer<CustomView> openView,
            @NonNull Supplier<Boolean> backView,
            @NonNull Environment environment
    ) {
        super(context, openView, backView, environment, R.layout.layout_openlans_library_list);
    }

    public void update(CharSequence keyword) {
        if (libraryFunctions == null) {
            return;
        }
        if (TextUtils.isEmpty(keyword)) {
            adapter.setRawJsonFunctions(libraryFunctions);
        } else {
            adapter.setRawJsonFunctions(libraryFunctions.stream()
                    .filter(rawJsonFunction -> rawJsonFunction.name != null &&
                                               rawJsonFunction.name.contains(ed_search.getText()))
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        View tv_not_allow_edit_in_floating_window = view.findViewById(R.id.tv_library_not_allow_edit_in_floating_window);
        View tv_not_allow_upload_in_floating_window = view.findViewById(R.id.tv_library_not_allow_upload_in_floating_window);
        View btn_update = view.findViewById(R.id.btn_update);
        View btn_upload = view.findViewById(R.id.btn_upload);
        btn_update.setOnClickListener(v -> {
            if (environment == Environment.APPLICATION) {
                new InputStringDialog(context)
                        .title("请输入密钥")
                        .onConfirm(auth_key -> CommandLabAPI.getFunctionByKey(auth_key, rawJsonFunctionResult -> post(() -> {
                            if (rawJsonFunctionResult.isPresent()) {
                                LibraryFunction data = rawJsonFunctionResult.get().data;
                                if (data != null) {
                                    openView((context2, openView, backView, environment) -> new OpenLansLibraryUpdateView(
                                            context2, openView, backView, environment, auth_key, () -> isDirty = true, data));
                                    return;
                                }
                            }
                            ToastUtil.show(context, "命令库寻找失败");
                        })))
                        .show();
            } else {
                tv_not_allow_edit_in_floating_window.setVisibility(VISIBLE);
                btn_update.setVisibility(GONE);
                btn_upload.setVisibility(GONE);
            }
        });
        btn_upload.setOnClickListener(v -> {
            if (environment == Environment.APPLICATION) {
                openView((context3, openView, backView, environment) -> new OpenLansLibraryUploadView(
                        context3, openView, backView, environment, () -> isDirty = true));
            } else {
                tv_not_allow_upload_in_floating_window.setVisibility(VISIBLE);
                btn_update.setVisibility(GONE);
                btn_upload.setVisibility(GONE);
            }
        });
        ed_search = view.findViewById(R.id.search);
        ed_search.addTextChangedListener(TextWatcherUtil.onTextChanged(this::update));
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        adapter = new OpenLansLibraryListAdapter(context, rawJsonFunction -> openView((context1, openView, backView, environment) ->
                new OpenLansLibraryView(context1, openView, backView, environment, rawJsonFunction)));
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        update();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isDirty) {
            isDirty = false;
            update();
        }
    }

    @SuppressLint("HardwareIds")
    void update() {
        Handler handler = new Handler(Looper.getMainLooper());
        CommandLabAPI.getAllFunctions(
                1,
                Integer.MAX_VALUE,
                null,
                null,
                null,
                CommandLabAPI.SortType.SORT_BY_CREATE_TIME,
                Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID),
                result -> handler.post(() -> {
                    if (result.isPresent()) {
                        LibraryFunctions libraryFunctions0 = result.get().data;
                        if (libraryFunctions0 != null) {
                            libraryFunctions = libraryFunctions0.functions;
                            update(ed_search.getText());
                        }
                    }
                }));
    }
}
