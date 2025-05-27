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
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.Toaster;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import yancey.chelper.R;
import yancey.chelper.android.common.dialog.InputStringDialog;
import yancey.chelper.android.common.util.TextWatcherUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.library.adapter.LibraryListAdapter;
import yancey.chelper.network.ServiceManager;
import yancey.chelper.network.library.data.LibraryFunction;

/**
 * 命令库列表视图
 */
@SuppressLint("ViewConstructor")
public class LibraryListView extends CustomView<Boolean> {

    private LibraryListAdapter adapter;
    private List<LibraryFunction> libraryFunctions;
    private EditText ed_search;
    private boolean isDirty = false;
    private Disposable loadData, getFunctionByKey;
    private AtomicReference<Disposable> doLike;

    public LibraryListView(@NonNull CustomView.CustomContext customContext, boolean isLocal) {
        super(customContext, R.layout.layout_library_list, isLocal);
    }

    public void update(CharSequence keyword) {
        if (libraryFunctions == null) {
            return;
        }
        if (TextUtils.isEmpty(keyword)) {
            adapter.setLibraryFunctions(libraryFunctions);
        } else {
            adapter.setLibraryFunctions(libraryFunctions.stream()
                    .filter(libraryFunction -> libraryFunction.name != null &&
                                               libraryFunction.name.contains(ed_search.getText()))
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Boolean isLocal) {
        Objects.requireNonNull(isLocal);
        view.findViewById(R.id.back).setOnClickListener(v -> backView());
        View btn_update = view.findViewById(R.id.btn_update);
        View btn_upload = view.findViewById(R.id.btn_upload);
        if (isLocal) {
            btn_update.setVisibility(View.GONE);
        } else {
            btn_update.setVisibility(View.VISIBLE);
            btn_update.setOnClickListener(v -> {
                if (getEnvironment() == Environment.APPLICATION) {
                    new InputStringDialog(context)
                            .title("请输入密钥")
                            .onConfirm(authKey -> {
                                if (getFunctionByKey != null) {
                                    getFunctionByKey.dispose();
                                }
                                getFunctionByKey = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE
                                        .getFunctionByKey(authKey)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(result -> {
                                            if (!Objects.equals(result.status, "success")) {
                                                Toaster.show(result.message);
                                                return;
                                            }
                                            LibraryFunction data = result.data;
                                            if (data == null) {
                                                Toaster.show("命令库寻找失败");
                                                return;
                                            }
                                            openView(customContext -> new LibraryEditView(customContext, authKey, () -> isDirty = true, data));
                                        }, throwable -> Toaster.show(throwable.getMessage()));
                            })
                            .show();
                } else {
                    Toaster.show(R.string.library_not_allow_edit_in_floating_window);
                    btn_update.setVisibility(View.GONE);
                    btn_upload.setVisibility(View.GONE);
                }
            });
        }
        if (isLocal) {
            btn_upload.setBackgroundResource(R.drawable.plus);
            btn_upload.setContentDescription(getContext().getString(R.string.library_add));
        } else {
            btn_upload.setBackgroundResource(R.drawable.upload);
            btn_upload.setContentDescription(getContext().getString(R.string.library_upload));
        }
        btn_upload.setOnClickListener(v -> {
            if (getEnvironment() == Environment.APPLICATION) {
                openView(customContext -> new LibraryEditView(
                        customContext, null, () -> isDirty = true, null));
            } else {
                Toaster.show(R.string.library_not_allow_upload_in_floating_window);
                btn_update.setVisibility(View.GONE);
                btn_upload.setVisibility(View.GONE);
            }
        });
        ed_search = view.findViewById(R.id.search);
        ed_search.addTextChangedListener(TextWatcherUtil.onTextChanged(this::update));
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        doLike = new AtomicReference<>();
        adapter = new LibraryListAdapter(
                context,
                doLike,
                libraryFunction -> openView(customContext -> new LibraryShowView(customContext, libraryFunction)),
                libraryFunction -> openView(customContext -> new LibraryEditView(customContext, null, () -> isDirty = true, libraryFunction))
        );
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
        } else {
            update(ed_search.getText());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loadData != null) {
            loadData.dispose();
        }
        if (getFunctionByKey != null) {
            getFunctionByKey.dispose();
        }
        Disposable disposable = doLike.get();
        if (disposable != null) {
            disposable.dispose();
        }
    }

    @SuppressLint("HardwareIds")
    void update() {
        if (loadData != null) {
            loadData.dispose();
        }
        String androidId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        loadData = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE
                .getFunctions(1, Integer.MAX_VALUE, null, null, null, "time", androidId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (!Objects.equals(result.status, "success")) {
                        Toaster.show(result.message);
                        return;
                    }
                    libraryFunctions = Objects.requireNonNull(result.data).functions;
                    update(ed_search.getText());
                }, throwable -> Toaster.show(throwable.getMessage()));
    }
}
