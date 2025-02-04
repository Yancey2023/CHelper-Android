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

package yancey.chelper.android.library.averychims.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.common.util.ClipboardUtil;
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.network.data.averychims.Library;

/**
 * Avery Chims的命令库列表适配器
 */
public class AveryChimsLibraryAdapter extends RecyclerView.Adapter<AveryChimsLibraryAdapter.CommandListViewHolder> {

    private final Context context;
    private List<Library.Command> commands;
    private String description;
    private String author;

    public AveryChimsLibraryAdapter(Context context) {
        this.context = context;
    }

    public AveryChimsLibraryAdapter(Context context, Library library) {
        this.context = context;
        this.description = library.description;
        this.author = library.author;
        this.commands = library.commands;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    @NonNull
    @Override
    public CommandListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new CommandListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_averychims_library_meta_information, parent, false), viewType);
        } else {
            return new CommandListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_averychims_library_item, parent, false), viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CommandListViewHolder holder, int position) {
        if (holder.viewType == 0) {
            holder.mTv_author.setText(Objects.requireNonNullElse(author, "加载中"));
            holder.mTv_description.setText(Objects.requireNonNullElse(description, "加载中"));
        } else {
            Library.Command command = commands.get(position - 1);
            holder.mTv_content.setText(command.content);
            holder.mTv_description.setText(command.description);
            holder.mBtn_copy.setOnClickListener(view -> {
                if (ClipboardUtil.setText(context, command.content)) {
                    ToastUtil.show(context, "已复制");
                } else {
                    ToastUtil.show(context, "复制失败");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (commands == null) {
            return 1;
        }
        return commands.size() + 1;
    }

    public void setDescriptionAndAuthor(String description, String author) {
        this.description = description;
        this.author = author;
        notifyItemChanged(0);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCommands(List<Library.Command> commands) {
        this.commands = commands;
        notifyDataSetChanged();
    }

    public static class CommandListViewHolder extends RecyclerView.ViewHolder {
        private final int viewType;
        private TextView mTv_author;
        private TextView mTv_content;
        private final TextView mTv_description;
        private View mBtn_copy;

        public CommandListViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == 0) {
                mTv_description = itemView.findViewById(R.id.description);
                mTv_author = itemView.findViewById(R.id.author);
            } else {
                mTv_content = itemView.findViewById(R.id.content);
                mTv_description = itemView.findViewById(R.id.description);
                mBtn_copy = itemView.findViewById(R.id.btn_copy);
            }
        }
    }
}
