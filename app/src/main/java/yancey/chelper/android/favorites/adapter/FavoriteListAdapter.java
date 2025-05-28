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

package yancey.chelper.android.favorites.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.Toaster;

import java.util.ArrayList;
import java.util.List;

import yancey.chelper.R;
import yancey.chelper.android.common.util.ClipboardUtil;
import yancey.chelper.android.favorites.data.DataFavorite;
import yancey.chelper.android.favorites.dialog.EditFavoriteDialog;

/**
 * 收藏界面列表适配器
 */
public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.CommandListViewHolder> {

    private final Context context;
    public List<DataFavorite> root;
    public List<DataFavorite> current;
    public final List<List<DataFavorite>> history;

    public FavoriteListAdapter(Context context, List<DataFavorite> root) {
        this.context = context;
        this.root = root;
        this.current = root;
        for (DataFavorite dataFavorite : current) {
            dataFavorite.isChoose = false;
        }
        history = new ArrayList<>();
    }

    @NonNull
    @Override
    public CommandListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommandListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_favorite_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommandListViewHolder holder, int position) {
        DataFavorite data = current.get(position);
        holder.mTv_title.setText(data.title);
        holder.mTv_description.setText(data.description);
        holder.mCb_isChoose.setChecked(data.isChoose);
        holder.mCb_isChoose.setOnClickListener(view -> {
            data.isChoose = !data.isChoose;
            notifyItemChanged(holder.getLayoutPosition());
        });
        if (data.dataFavoriteList == null) {
            holder.mIv_icon.setBackgroundResource(R.drawable.align_justified);
            holder.itemView.setOnClickListener(v -> {
                ClipboardUtil.setText(context, data.title);
                Toaster.show("已复制");
            });
        } else {
            holder.mIv_icon.setBackgroundResource(R.drawable.folder);
            holder.itemView.setOnClickListener(view -> {
                history.add(current);
                System.out.println("history.size(): " + history.size());
                setDataList(data.dataFavoriteList);
            });
        }
        holder.mIv_edit.setOnClickListener(view -> new EditFavoriteDialog(context, this, data, holder.getLayoutPosition(), false).show());
    }

    @Override
    public int getItemCount() {
        if (current == null) {
            return 0;
        }
        return current.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataList(List<DataFavorite> dataFavoriteList) {
        this.current = dataFavoriteList;
        for (DataFavorite dataFavorite : dataFavoriteList) {
            dataFavorite.isChoose = false;
        }
        notifyDataSetChanged();
    }

    public boolean back() {
        System.out.println("history.size(): " + history.size());
        int position = history.size();
        if (position == 0) {
            return false;
        }
        position--;
        setDataList(history.get(position));
        history.remove(position);
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void selectAll() {
        for (DataFavorite dataFavorite : current) {
            dataFavorite.isChoose = true;
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void invert() {
        for (DataFavorite dataFavorite : current) {
            dataFavorite.isChoose = !dataFavorite.isChoose;
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deselect() {
        for (DataFavorite dataFavorite : current) {
            dataFavorite.isChoose = false;
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void delete() {
        DataFavorite dataFavorite;
        int i = 0;
        while (i < current.size()) {
            dataFavorite = current.get(i);
            if (dataFavorite.isChoose) {
                current.remove(i);
            } else {
                i++;
            }
        }
        notifyDataSetChanged();
    }

    public void newOne(boolean isFolder) {
        DataFavorite dataFavorite = new DataFavorite("", "", isFolder ? new ArrayList<>() : null);
        new EditFavoriteDialog(context, this, dataFavorite, 0, true).show();
    }

    public void add(DataFavorite dataFavorite) {
        current.add(dataFavorite);
        notifyItemInserted(current.size() - 1);
    }

//    public void bulkCopy() {
//        for (DataFavorite data : dataFavoriteList) {
//            if (data.dataFavoriteList == null) {
//                ClipboardUtil.setText(context, data.title);
//            }
//        }
//        Toaster.show("已批量复制");
//    }

    public static class CommandListViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private final TextView mTv_title, mTv_description;
        private final View mIv_icon, mIv_edit;
        private final CheckBox mCb_isChoose;

        public CommandListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mTv_title = itemView.findViewById(R.id.tv_title);
            mTv_description = itemView.findViewById(R.id.tv_description);
            mIv_icon = itemView.findViewById(R.id.iv_folder_or_text);
            mIv_edit = itemView.findViewById(R.id.iv_edit);
            mCb_isChoose = itemView.findViewById(R.id.cb_is_choose);
        }
    }
}
