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
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
import yancey.chelper.android.common.view.BaseView;
import yancey.chelper.android.library.adapter.PublicLibraryListAdapter;
import yancey.chelper.android.library.util.OnEditListener;
import yancey.chelper.network.ServiceManager;
import yancey.chelper.network.library.data.LibraryFunction;

/**
 * 命令库列表视图
 */
@SuppressLint("ViewConstructor")
public class PublicLibraryListView extends BaseView {

    private final PublicLibraryListAdapter adapter;
    private List<LibraryFunction> libraryFunctions;
    private final EditText ed_search;
    private boolean isDirty = false;
    private Disposable loadData, getFunctionByKey;
    private final AtomicReference<Disposable> doLike;

    public PublicLibraryListView(@NonNull FWSContext fwsContext) {
        super(fwsContext, R.layout.layout_library_list);
        view.findViewById(R.id.back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        TextView tv_title = view.findViewById(R.id.title);
        View btn_update = view.findViewById(R.id.btn_update);
        View btn_upload = view.findViewById(R.id.btn_upload);
        View btn_export = view.findViewById(R.id.btn_export);
        View btn_import = view.findViewById(R.id.btn_import);
        tv_title.setText(R.string.public_library);
        btn_export.setVisibility(View.GONE);
        btn_import.setVisibility(View.GONE);
        ed_search = view.findViewById(R.id.search);
        ed_search.addTextChangedListener(TextWatcherUtil.onTextChanged(this::update));
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        doLike = new AtomicReference<>();
        adapter = new PublicLibraryListAdapter(
                context,
                doLike,
                libraryFunction -> openView(customContext1 ->
                        new PublicLibraryShowView(customContext1, libraryFunction)));
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        if (getEnvironment() == Environment.APPLICATION) {
            btn_update.setOnClickListener(v -> new InputStringDialog(context)
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
                                    openView(customContext1 ->
                                            new PublicLibraryEditView(customContext1, authKey, new OnEditListener() {
                                                @Override
                                                public void onCreate(@NonNull LibraryFunction libraryFunction) {
                                                    throw new RuntimeException("it is impossible to call onCreate() when edit");
                                                }

                                                @Override
                                                public void onUpdate(@Nullable Integer position, @NonNull LibraryFunction before, @NonNull LibraryFunction after) {
                                                    isDirty = true;
                                                }

                                                @Override
                                                public void onDelete(@Nullable Integer position, @NonNull LibraryFunction libraryFunction) {
                                                    isDirty = true;
                                                }
                                            }, null, data));
                                }, throwable -> Toaster.show(throwable.getMessage()));
                    })
                    .show());
            btn_upload.setBackgroundResource(R.drawable.upload);
            btn_upload.setContentDescription(getContext().getString(R.string.library_upload));
            btn_upload.setOnClickListener(v -> openView(customContext1 ->
                    new PublicLibraryEditView(customContext1, null, new OnEditListener() {
                        @Override
                        public void onCreate(@NonNull LibraryFunction libraryFunction) {
                            isDirty = true;
                        }

                        @Override
                        public void onUpdate(@Nullable Integer position, @NonNull LibraryFunction before, @NonNull LibraryFunction after) {
                            throw new RuntimeException("it is impossible to call onUpdate() when upload");
                        }

                        @Override
                        public void onDelete(@Nullable Integer position, @NonNull LibraryFunction libraryFunction) {
                            throw new RuntimeException("it is impossible to call onDelete() when upload");
                        }
                    }, null, null)));
        } else {
            btn_upload.setVisibility(View.GONE);
        }
        update();
    }

    @Override
    protected String gePageName() {
        return "PublicLibraryList";
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
