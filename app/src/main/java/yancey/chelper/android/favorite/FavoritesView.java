package yancey.chelper.android.favorite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;

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
import java.util.function.Consumer;

import yancey.chelper.R;
import yancey.chelper.android.common.view.CustomView;
import yancey.chelper.util.FileUtil;

@SuppressLint("ViewConstructor")
public class FavoritesView extends CustomView {

    public static final String TAG = "FavoriteActivity";
    public FavoriteListAdapter adapter;

    public FavoritesView(@NonNull Context context, Consumer<CustomView> openView, boolean isInFloatingWindow) {
        super(context, openView, isInFloatingWindow, R.layout.layout_favorites);
    }

    @Override
    public void onCreateView(Context context, View view) {
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
        view.findViewById(R.id.btn_favorite_select_all).setOnClickListener(v -> adapter.selectAll());
        view.findViewById(R.id.btn_favorite_invert).setOnClickListener(v -> adapter.invert());
        view.findViewById(R.id.btn_favorite_deselect).setOnClickListener(v -> adapter.deselect());
        view.findViewById(R.id.btn_new).setOnClickListener(v -> adapter.newOne(false));
        view.findViewById(R.id.btn_delete).setOnClickListener(v -> adapter.delete());
        view.findViewById(R.id.btn_new_folder).setOnClickListener(v -> adapter.newOne(true));
        view.findViewById(R.id.btn_bulk_copy).setOnClickListener(v -> adapter.bulkCopy());
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
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onBackPressed() {
        return adapter.back();
    }
}
