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
import yancey.chelper.android.library.adapter.LibraryListAdapter;
import yancey.chelper.android.library.util.LocalLibraryManager;
import yancey.chelper.fws.view.FWSView;
import yancey.chelper.network.ServiceManager;
import yancey.chelper.network.library.data.LibraryFunction;

/**
 * 命令库列表视图
 */
@SuppressLint("ViewConstructor")
public class LibraryListView extends BaseView {

    private final LibraryListAdapter adapter;
    private List<LibraryFunction> libraryFunctions;
    private final EditText ed_search;
    private boolean isDirty = false;
    private Disposable loadData, getFunctionByKey;
    private final boolean isLocal;
    private AtomicReference<Disposable> doLike;

    public LibraryListView(@NonNull FWSView.CustomContext customContext, boolean isLocal) {
        super(customContext, R.layout.layout_library_list);
        this.isLocal = isLocal;
        view.findViewById(R.id.back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        TextView tv_title = view.findViewById(R.id.title);
        View btn_update = view.findViewById(R.id.btn_update);
        View btn_upload = view.findViewById(R.id.btn_upload);
        if (isLocal) {
            tv_title.setText(R.string.local_library);
            btn_update.setVisibility(View.GONE);
            if (getEnvironment() == Environment.APPLICATION) {
                btn_upload.setBackgroundResource(R.drawable.plus);
                btn_upload.setContentDescription(getContext().getString(R.string.library_add));
                btn_upload.setOnClickListener(v -> openView(customContext1 ->
                        new LibraryEditView(customContext1, null, new LibraryEditView.OnEditListener() {
                            @Override
                            public void onCreate(@NonNull LibraryFunction libraryFunction) {
                                libraryFunctions.add(libraryFunction);
                                adapter.notifyItemInserted(libraryFunctions.size() - 1);
                                LocalLibraryManager.INSTANCE.save();
                            }

                            @Override
                            public void onUpdate(@Nullable Integer position, @NonNull LibraryFunction before, @NonNull LibraryFunction after) {
                                throw new RuntimeException("it is impossible to call onUpdate() when add");
                            }

                            @Override
                            public void onDelete(@Nullable Integer position, @NonNull LibraryFunction libraryFunction) {
                                throw new RuntimeException("it is impossible to call onDelete() when add");
                            }
                        }, null, null, true)));
            } else {
                btn_upload.setVisibility(View.GONE);
            }
        } else {
            tv_title.setText(R.string.public_library);
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
                                                new LibraryEditView(customContext1, authKey, new LibraryEditView.OnEditListener() {
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
                                                }, null, data, false));
                                    }, throwable -> Toaster.show(throwable.getMessage()));
                        })
                        .show());
                btn_upload.setBackgroundResource(R.drawable.upload);
                btn_upload.setContentDescription(getContext().getString(R.string.library_upload));
                btn_upload.setOnClickListener(v -> openView(customContext1 ->
                        new LibraryEditView(customContext1, null, new LibraryEditView.OnEditListener() {
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
                        }, null, null, true)));
            } else {
                btn_upload.setVisibility(View.GONE);
            }
        }
        ed_search = view.findViewById(R.id.search);
        ed_search.addTextChangedListener(TextWatcherUtil.onTextChanged(this::update));
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        if (!isLocal) {
            doLike = new AtomicReference<>();
        }
        adapter = new LibraryListAdapter(
                context,
                doLike,
                libraryFunction -> openView(customContext1 ->
                        new LibraryShowView(customContext1, libraryFunction, isLocal)),
                getEnvironment() == Environment.FLOATING_WINDOW ? null : libraryFunction -> openView(customContext1 -> new LibraryEditView(customContext1, null, new LibraryEditView.OnEditListener() {
                    @Override
                    public void onCreate(@NonNull LibraryFunction libraryFunction) {
                        throw new RuntimeException("it is impossible to call onCreate() when edit");
                    }

                    @Override
                    public void onUpdate(@Nullable Integer position, @NonNull LibraryFunction before, @NonNull LibraryFunction after) {
                        if (position != null && position > 0 && position < libraryFunctions.size() && libraryFunctions.get(position) == before) {
                            libraryFunctions.set(position, after);
                            adapter.notifyItemChanged(position);
                        } else {
                            boolean isFind = false;
                            for (int i = 0; i < libraryFunctions.size(); i++) {
                                if (libraryFunctions.get(i) == before) {
                                    libraryFunctions.set(i, after);
                                    adapter.notifyItemChanged(i);
                                    isFind = true;
                                    break;
                                }
                            }
                            if (!isFind) {
                                libraryFunctions.add(after);
                                adapter.notifyItemInserted(libraryFunctions.size() - 1);
                            }
                        }
                        LocalLibraryManager.INSTANCE.save();
                    }

                    @Override
                    public void onDelete(@Nullable Integer position, @NonNull LibraryFunction libraryFunction) {
                        if (position != null && position > 0 && position < libraryFunctions.size() && libraryFunctions.get(position) == libraryFunction) {
                            libraryFunctions.remove((int) position);
                            adapter.notifyItemRemoved(position);
                        } else {
                            for (int i = 0; i < libraryFunctions.size(); i++) {
                                if (libraryFunctions.get(i) == libraryFunction) {
                                    libraryFunctions.remove(i);
                                    adapter.notifyItemRemoved(i);
                                    break;
                                }
                            }
                        }
                        LocalLibraryManager.INSTANCE.save();
                    }
                }, null, libraryFunction, false)));
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        update();
    }

    @Override
    protected String gePageName() {
        return isLocal ? "LocalLibraryList" : "PublicLibraryList";
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
        if (doLike != null) {
            Disposable disposable = doLike.get();
            if (disposable != null) {
                disposable.dispose();
            }
        }
    }

    @SuppressLint("HardwareIds")
    void update() {
        if (loadData != null) {
            loadData.dispose();
        }
        if (isLocal) {
            loadData = LocalLibraryManager.INSTANCE.getFunctions()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(libraryFunctions -> {
                        this.libraryFunctions = libraryFunctions;
                        update(ed_search.getText());
                    }, throwable -> Toaster.show(throwable.getMessage()));
        } else {
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
}
