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
import android.util.Log;

import androidx.annotation.NonNull;
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
import yancey.chelper.android.common.util.MonitorUtil;
import yancey.chelper.android.common.view.BaseView;
import yancey.chelper.android.favorites.adapter.FavoriteListAdapter;
import yancey.chelper.android.favorites.data.DataFavorite;

/**
 * 收藏界面
 */
@SuppressLint("ViewConstructor")
public class FavoritesView extends BaseView {

    private static final String TAG = "FavoriteActivity";
    private final FavoriteListAdapter adapter;

    public FavoritesView(@NonNull FWSContext fwsContext) {
        super(fwsContext, R.layout.layout_favorites);
        File file = FileUtil.getFile(context.getFilesDir().getAbsolutePath(), "favorites", "favorites.dat");
        List<DataFavorite> dataFavoriteList;
        if (file.exists()) {
            try (DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                dataFavoriteList = new ArrayList<>();
                int length = dataInputStream.readInt();
                for (int i = 0; i < length; i++) {
                    dataFavoriteList.add(new DataFavorite(dataInputStream));
                }
            } catch (IOException e) {
                dataFavoriteList = new ArrayList<>();
                MonitorUtil.generateCustomLog(e, "IOException");
            }
        } else {
            dataFavoriteList = new ArrayList<>();
        }
        RecyclerView rv_favoriteList = view.findViewById(R.id.rv_favorite);
        adapter = new FavoriteListAdapter(context, dataFavoriteList, getOnBackPressedDispatcher());
        rv_favoriteList.setLayoutManager(new LinearLayoutManager(context));
        rv_favoriteList.setAdapter(adapter);
        view.findViewById(R.id.back).setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        view.findViewById(R.id.btn_favorite_select_all).setOnClickListener(v -> adapter.selectAll());
        view.findViewById(R.id.btn_favorite_invert).setOnClickListener(v -> adapter.invert());
        view.findViewById(R.id.btn_favorite_deselect).setOnClickListener(v -> adapter.deselect());
        view.findViewById(R.id.btn_new).setOnClickListener(v -> adapter.newOne(false));
        view.findViewById(R.id.btn_delete).setOnClickListener(v -> adapter.delete());
        view.findViewById(R.id.btn_new_folder).setOnClickListener(v -> adapter.newOne(true));
        // view.findViewById(R.id.btn_bulk_copy).setOnClickListener(v -> adapter.bulkCopy());
        // view.findViewById(R.id.btn_move).setOnClickListener(this);
    }

    @Override
    protected String gePageName() {
        return "Favorites";
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
            dataOutputStream.writeInt(adapter.root.size());
            for (DataFavorite dataFavorite : adapter.root) {
                dataFavorite.writeToFile(dataOutputStream);
            }
        } catch (IOException e) {
            Log.e(TAG, "could not save file : " + file.getAbsolutePath());
            MonitorUtil.generateCustomLog(e, "IOException");
        }
    }

}
