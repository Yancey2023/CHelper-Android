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

package yancey.chelper.android.library.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.function.Consumer;

import yancey.chelper.R;

/**
 * 收藏界面列表适配器
 */
public class PublicLibraryListAdapter extends RecyclerView.Adapter<PublicLibraryListAdapter.CommandListViewHolder> {

    private final Context context;
    private List<String> libraryNames;
    private final Consumer<String> onLibraryChoose;

    public PublicLibraryListAdapter(Context context, Consumer<String> onLibraryChoose) {
        this.context = context;
        this.onLibraryChoose = onLibraryChoose;
    }

    @NonNull
    @Override
    public CommandListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommandListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_public_library_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommandListViewHolder holder, int position) {
        String name = libraryNames.get(position);
        holder.mTv_name.setText(name);
        holder.itemView.setOnClickListener(view -> onLibraryChoose.accept(name));
    }

    @Override
    public int getItemCount() {
        if (libraryNames == null) {
            return 0;
        }
        return libraryNames.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setLibraryNames(List<String> libraryNames) {
        this.libraryNames = libraryNames;
        notifyDataSetChanged();
    }

    public static class CommandListViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private final TextView mTv_name;

        public CommandListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mTv_name = itemView.findViewById(R.id.name);
        }
    }
}
