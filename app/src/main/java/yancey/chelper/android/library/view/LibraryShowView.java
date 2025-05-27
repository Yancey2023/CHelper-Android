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
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.Toaster;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import yancey.chelper.R;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.library.adapter.LibraryShowAdapter;
import yancey.chelper.network.ServiceManager;
import yancey.chelper.network.library.data.LibraryFunction;
import yancey.chelper.network.library.service.CommandLabPublicService;

/**
 * 命令库显示视图
 */
@SuppressLint("ViewConstructor")
public class LibraryShowView extends CustomView<LibraryFunction> {

    private LibraryFunction libraryFunction, library;
    private LibraryShowAdapter adapter;
    private View btn_like;
    private TextView tv_like_count;
    private Disposable loadData, doLike;

    public LibraryShowView(
            @NonNull CustomContext customContext,
            @NonNull LibraryFunction libraryFunction
    ) {
        super(customContext, R.layout.layout_library_show, libraryFunction);
    }

    void updateLike(LibraryFunction libraryFunction) {
        if (Boolean.TRUE.equals(libraryFunction.is_liked)) {
            btn_like.setBackgroundResource(R.drawable.heart_filled);
        } else {
            btn_like.setBackgroundResource(R.drawable.heart);
        }
        tv_like_count.setText(String.valueOf(libraryFunction.like_count));
    }

    @Override
    @SuppressLint("HardwareIds")
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable LibraryFunction libraryFunction) {
        this.libraryFunction = Objects.requireNonNull(libraryFunction);
        view.findViewById(R.id.back).setOnClickListener(v -> backView());
        btn_like = view.findViewById(R.id.btn_like);
        tv_like_count = view.findViewById(R.id.tv_like_count);
        TextView tv_name = view.findViewById(R.id.name);
        adapter = new LibraryShowAdapter(context, libraryFunction);
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        if (libraryFunction.id == null) {
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
        } else {
            if (loadData != null) {
                loadData.dispose();
            }
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            loadData = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE
                    .getFunction(libraryFunction.id, androidId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                        if (!Objects.equals(result.status, "success") || result.data == null) {
                            LibraryFunction function = new LibraryFunction();
                            function.version = "数据获取失败";
                            adapter.setLibraryFunction(function);
                            Toaster.show(result.message);
                            return;
                        }
                        library = result.data;
                        updateLike(library);
                        btn_like.setOnClickListener(view1 -> doLike());
                        adapter.setLibraryFunction(library);
                    }, throwable -> {
                        LibraryFunction loadError = new LibraryFunction();
                        loadError.version = "数据获取失败";
                        adapter.setLibraryFunction(loadError);
                        Toaster.show(throwable.getMessage());
                    });
        }
        tv_name.setText(libraryFunction.name);
        updateLike(libraryFunction);
    }

    @SuppressLint("HardwareIds")
    void doLike() {
        if (doLike != null) {
            doLike.dispose();
        }
        CommandLabPublicService.LikeFunctionRequest request = new CommandLabPublicService.LikeFunctionRequest();
        request.android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        doLike = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE
                .like(Objects.requireNonNull(libraryFunction.id), request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result2 -> {
                    if (!Objects.equals(result2.status, "success") || result2.data == null) {
                        Toaster.show(result2.message);
                        return;
                    }
                    CommandLabPublicService.LibraryLikeResponse libraryLikeState = result2.data;
                    library.is_liked = !Objects.equals(libraryLikeState.action, "unlike");
                    library.like_count = libraryLikeState.like_count;
                    libraryFunction.is_liked = library.is_liked;
                    libraryFunction.like_count = library.like_count;
                    updateLike(library);
                }, throwable -> Toaster.show(throwable.getMessage()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loadData != null) {
            loadData.dispose();
        }
        if (doLike != null) {
            doLike.dispose();
        }
    }
}
