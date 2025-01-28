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

import yancey.chelper.R;
import yancey.chelper.android.common.util.ClipboardUtil;
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.network.data.Library;

/**
 * 收藏界面列表适配器
 */
public class PublicLibraryAdapter extends RecyclerView.Adapter<PublicLibraryAdapter.CommandListViewHolder> {

    private final Context context;
    private List<Library.Command> commands;

    public PublicLibraryAdapter(Context context, List<Library.Command> commands) {
        this.context = context;
        this.commands = commands;
    }

    @NonNull
    @Override
    public CommandListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommandListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_public_library_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommandListViewHolder holder, int position) {
        Library.Command command = commands.get(position);
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

    @Override
    public int getItemCount() {
        if (commands == null) {
            return 0;
        }
        return commands.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCommands(List<Library.Command> commands) {
        this.commands = commands;
        notifyDataSetChanged();
    }

    public static class CommandListViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTv_content;
        private final TextView mTv_description;
        private final View mBtn_copy;

        public CommandListViewHolder(View itemView) {
            super(itemView);
            mTv_content = itemView.findViewById(R.id.content);
            mTv_description = itemView.findViewById(R.id.description);
            mBtn_copy = itemView.findViewById(R.id.btn_copy);
        }
    }
}
