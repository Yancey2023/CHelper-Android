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

package yancey.chelper.android.library.openlans.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import yancey.chelper.R;
import yancey.chelper.android.common.util.ClipboardUtil;
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.network.data.openlans.LibraryFunction;

/**
 * OpenLANS的命令库列表适配器
 */
public class OpenLansLibraryAdapter extends RecyclerView.Adapter<OpenLansLibraryAdapter.CommandListViewHolder> {

    private final Context context;
    private LibraryFunction library;
    private List<String> content;

    public OpenLansLibraryAdapter(Context context, LibraryFunction library) {
        this.context = context;
        this.library = library;
        this.content = library.content == null ? List.of() :
                Arrays.stream(library.content.split("\n"))
                        .filter(s -> !s.isEmpty())
                        .map(String::strip)
                        .collect(Collectors.toList());
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
    }

    @NonNull
    @Override
    public CommandListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            return new CommandListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_openlans_library_meta_information, parent, false), viewType);
        } else {
            return new CommandListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_openlans_library_item, parent, false), viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CommandListViewHolder holder, int position) {
        if (holder.viewType == 0) {
            if (library == null) {
                holder.mTv_version.setText("加载中");
                holder.mTv_author.setText("加载中");
                holder.mTv_description.setText("加载中");
                holder.mTv_tags.setText("加载中");
            } else {
                holder.mTv_version.setText(library.version);
                holder.mTv_author.setText(library.author);
                holder.mTv_description.setText(library.note);
                holder.mTv_tags.setText(library.tags == null ? null : String.join(",", library.tags));
            }
        } else {
            String command = content.get(position - 1);
            if (command.startsWith("//")) {
                holder.mTv_content.setText(command.substring(2).strip());
                holder.mTv_content.setTextColor(context.getColor(R.color.text_secondary));
                holder.mBtn_copy.setVisibility(View.GONE);
            } else if (command.startsWith("#")) {
                holder.mTv_content.setText(command.substring(1).strip());
                holder.mTv_content.setTextColor(context.getColor(R.color.text_secondary));
                holder.mBtn_copy.setVisibility(View.GONE);
            } else {
                holder.mTv_content.setText(command);
                holder.mTv_content.setTextColor(context.getColor(R.color.main_color));
                holder.mBtn_copy.setVisibility(View.VISIBLE);
                holder.mBtn_copy.setOnClickListener(view -> {
                    if (ClipboardUtil.setText(context, command)) {
                        ToastUtil.show(context, "已复制");
                    } else {
                        ToastUtil.show(context, "复制失败");
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (content == null) {
            return 1;
        }
        return content.size() + 1;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRawJsonFunction(LibraryFunction library) {
        this.library = library;
        this.content = library.content == null ? List.of() :
                Arrays.stream(library.content.split("\n"))
                        .map(String::strip)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    public static class CommandListViewHolder extends RecyclerView.ViewHolder {
        private final int viewType;
        private TextView mTv_version;
        private TextView mTv_author;
        private TextView mTv_description;
        private TextView mTv_tags;
        private TextView mTv_content;
        private View mBtn_copy;

        public CommandListViewHolder(View itemView, int viewType) {
            super(itemView);
            this.viewType = viewType;
            if (viewType == 0) {
                mTv_version = itemView.findViewById(R.id.version);
                mTv_author = itemView.findViewById(R.id.author);
                mTv_description = itemView.findViewById(R.id.description);
                mTv_tags = itemView.findViewById(R.id.tags);
            } else {
                mTv_content = itemView.findViewById(R.id.content);
                mBtn_copy = itemView.findViewById(R.id.btn_copy);
            }
        }
    }
}
