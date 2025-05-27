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

package yancey.chelper.android.favorites.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yancey.chelper.R;
import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.android.favorites.adapter.FavoriteListAdapter;
import yancey.chelper.android.favorites.data.DataFavorite;

/**
 * 收藏界面
 */
@SuppressLint("ViewConstructor")
public class FavoritesView extends CustomView<Object> {

    private static final String TAG = "FavoriteActivity";
    private FavoriteListAdapter adapter;

    public FavoritesView(@NonNull CustomContext customContext) {
        super(customContext, R.layout.layout_favorites);
    }

    @Override
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {
        File file = FileUtil.getFile(context.getFilesDir().getAbsolutePath(), "favorites", "favorites.dat");
        List<DataFavorite> dataFavoriteList;
        if (file.canRead()) {
            try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                dataFavoriteList = new ArrayList<>();
                int length = dataInputStream.readInt();
                for (int i = 0; i < length; i++) {
                    dataFavoriteList.add(new DataFavorite(dataInputStream));
                }
            } catch (IOException exception) {
                dataFavoriteList = new ArrayList<>();
            }
        } else {
            dataFavoriteList = new ArrayList<>();
        }
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_favorite);
        adapter = new FavoriteListAdapter(context, dataFavoriteList);
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        view.findViewById(R.id.back).setOnClickListener(v -> backView());
        view.findViewById(R.id.btn_favorite_select_all).setOnClickListener(v -> adapter.selectAll());
        view.findViewById(R.id.btn_favorite_invert).setOnClickListener(v -> adapter.invert());
        view.findViewById(R.id.btn_favorite_deselect).setOnClickListener(v -> adapter.deselect());
        view.findViewById(R.id.btn_new).setOnClickListener(v -> adapter.newOne(false));
        view.findViewById(R.id.btn_delete).setOnClickListener(v -> adapter.delete());
        view.findViewById(R.id.btn_new_folder).setOnClickListener(v -> adapter.newOne(true));
//        view.findViewById(R.id.btn_bulk_copy).setOnClickListener(v -> adapter.bulkCopy());
        //findViewById(R.id.btn_move).setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        File file = FileUtil.getFile(getContext().getFilesDir().getAbsolutePath(), "favorites", "favorites.dat");
        if (!FileUtil.createParentFile(file)) {
            Log.e(TAG, "could not create parent dir : " + file.getAbsolutePath());
            return;
        }
        try (DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            dataOutputStream.writeInt(adapter.dataFavoriteList.size());
            for (DataFavorite dataFavorite : adapter.dataFavoriteList) {
                dataFavorite.writeToFile(dataOutputStream);
            }
        } catch (IOException exception) {
            Log.e(TAG, "could not save file : " + file.getAbsolutePath());
        }
    }

    @Override
    public boolean onBackPressed() {
        return adapter.back();
    }
}
