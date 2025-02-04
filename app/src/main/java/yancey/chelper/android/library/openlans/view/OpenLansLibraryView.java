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
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.library.openlans.adapter.OpenLansLibraryAdapter;
import yancey.chelper.network.api.library.openlans.CommandLabAPI;
import yancey.chelper.network.data.openlans.LibraryFunction;
import yancey.chelper.network.data.openlans.LibraryLikeState;

/**
 * OpenLANS的命令库
 */
@SuppressLint("ViewConstructor")
public class OpenLansLibraryView extends CustomView {

    private OpenLansLibraryAdapter adapter;
    private View btn_like;
    private TextView tv_like_count;

    public OpenLansLibraryView(
            @NonNull Context context,
            @NonNull Consumer<CustomView> openView,
            @NonNull Supplier<Boolean> backView,
            @NonNull Environment environment,
            LibraryFunction libraryFunction
    ) {
        super(context, openView, backView, environment, R.layout.layout_openlans_library, libraryFunction);
    }

    void updateLike(LibraryFunction libraryFunction) {
        if (Boolean.TRUE.equals(libraryFunction.is_liked)) {
            btn_like.setBackgroundResource(R.drawable.icon_liked);
        } else {
            btn_like.setBackgroundResource(R.drawable.icon_like);
        }
        tv_like_count.setText(String.valueOf(libraryFunction.like_count));
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        LibraryFunction libraryFunction = (LibraryFunction) Objects.requireNonNull(privateData);
        btn_like = view.findViewById(R.id.btn_like);
        tv_like_count = view.findViewById(R.id.tv_like_count);
        TextView tv_name = view.findViewById(R.id.name);
        tv_name.setText(libraryFunction.name);
        updateLike(libraryFunction);
        adapter = new OpenLansLibraryAdapter(context, libraryFunction);
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_list_view);
        rv_favoriteList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        Handler handler = new Handler(Looper.getMainLooper());
        if (libraryFunction.id != null) {
            CommandLabAPI.getFunction(
                    libraryFunction.id,
                    Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID),
                    result -> handler.post(() -> {
                        if (result.isPresent()) {
                            LibraryFunction library = result.get().data;
                            if (library != null) {
                                updateLike(library);
                                btn_like.setOnClickListener(view1 -> CommandLabAPI.like(
                                        libraryFunction.id,
                                        Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID),
                                        result1 -> handler.post(() -> {
                                            if (result1.isPresent()) {
                                                LibraryLikeState libraryLikeState = result1.get().data;
                                                if (libraryLikeState == null) {
                                                    ToastUtil.show(context, "点赞失败");
                                                } else {
                                                    library.is_liked = !Objects.equals(libraryLikeState.action, "unlike");
                                                    library.like_count = libraryLikeState.like_count;
                                                    updateLike(library);
                                                }
                                            } else {
                                                ToastUtil.show(context, "点赞失败，请检查您的网络状况");
                                            }
                                        })));
                                adapter.setRawJsonFunction(library);
                                return;
                            }
                        }
                        LibraryFunction loadError = new LibraryFunction();
                        loadError.version = "数据获取失败";
                        adapter.setRawJsonFunction(loadError);
                    }));
        }
    }

}
