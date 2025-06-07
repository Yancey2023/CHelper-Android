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
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.Toaster;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import yancey.chelper.R;
import yancey.chelper.android.common.view.BaseView;
import yancey.chelper.android.library.adapter.LibraryShowAdapter;
import yancey.chelper.network.ServiceManager;
import yancey.chelper.network.library.data.LibraryFunction;
import yancey.chelper.network.library.service.CommandLabPublicService;

/**
 * 命令库显示视图
 */
@SuppressLint("ViewConstructor")
public class PublicLibraryShowView extends BaseView {

    private final LibraryFunction before;
    private LibraryFunction after;
    private final View btn_like;
    private final TextView tv_like_count;
    private Disposable loadData, doLike;

    @SuppressLint("HardwareIds")
    public PublicLibraryShowView(
            @NonNull FWSContext fwsContext,
            @NonNull LibraryFunction before
    ) {
        super(fwsContext, R.layout.layout_library_show);
        this.before = before;
        this.after = before;
        view.findViewById(R.id.back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        btn_like = view.findViewById(R.id.btn_like);
        tv_like_count = view.findViewById(R.id.tv_like_count);
        TextView tv_name = view.findViewById(R.id.name);
        LibraryShowAdapter adapter = new LibraryShowAdapter(context, before);
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        if (before.id == null) {
            after.is_liked = false;
            after.like_count = 520;
            btn_like.setOnClickListener(view1 -> {
                if (after.is_liked) {
                    after.like_count--;
                } else {
                    after.like_count++;
                }
                after.is_liked = !after.is_liked;
                updateLike(after);
            });
        } else {
            if (loadData != null) {
                loadData.dispose();
            }
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            loadData = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE
                    .getFunction(before.id, androidId)
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
                        after = result.data;
                        updateLike(after);
                        btn_like.setOnClickListener(view1 -> doLike());
                        adapter.setLibraryFunction(after);
                    }, throwable -> {
                        LibraryFunction loadError = new LibraryFunction();
                        loadError.version = "数据获取失败";
                        adapter.setLibraryFunction(loadError);
                        Toaster.show(throwable.getMessage());
                    });
        }
        tv_name.setText(before.name);
        updateLike(before);
    }

    @Override
    protected String gePageName() {
        return "PublicLibraryShow";
    }

    void updateLike(LibraryFunction libraryFunction) {
        if (Boolean.TRUE.equals(libraryFunction.is_liked)) {
            btn_like.setBackgroundResource(R.drawable.heart_filled);
        } else {
            btn_like.setBackgroundResource(R.drawable.heart);
        }
        tv_like_count.setText(String.valueOf(libraryFunction.like_count));
    }

    @SuppressLint("HardwareIds")
    void doLike() {
        if (doLike != null) {
            doLike.dispose();
        }
        CommandLabPublicService.LikeFunctionRequest request = new CommandLabPublicService.LikeFunctionRequest();
        request.android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        doLike = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE
                .like(Objects.requireNonNull(before.id), request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result2 -> {
                    if (!Objects.equals(result2.status, "success") || result2.data == null) {
                        Toaster.show(result2.message);
                        return;
                    }
                    CommandLabPublicService.LibraryLikeResponse libraryLikeState = result2.data;
                    after.is_liked = !Objects.equals(libraryLikeState.action, "unlike");
                    after.like_count = libraryLikeState.like_count;
                    before.is_liked = after.is_liked;
                    before.like_count = after.like_count;
                    updateLike(after);
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
