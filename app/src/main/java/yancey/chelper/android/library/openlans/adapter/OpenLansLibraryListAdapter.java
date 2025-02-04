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
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import yancey.chelper.R;
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.network.api.library.openlans.CommandLabAPI;
import yancey.chelper.network.data.openlans.LibraryFunction;
import yancey.chelper.network.data.openlans.LibraryLikeState;

/**
 * OpenLANS的命令库列表适配器
 */
public class OpenLansLibraryListAdapter extends RecyclerView.Adapter<OpenLansLibraryListAdapter.CommandListViewHolder> {

    private final Context context;
    private List<LibraryFunction> libraryNames;
    private final Consumer<LibraryFunction> onLibraryChoose;

    public OpenLansLibraryListAdapter(Context context, Consumer<LibraryFunction> onLibraryChoose) {
        this.context = context;
        this.onLibraryChoose = onLibraryChoose;
    }

    @NonNull
    @Override
    public CommandListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommandListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_openlans_library_list_item, parent, false));
    }

    @Override
    @SuppressLint({"SetTextI18n", "HardwareIds"})
    public void onBindViewHolder(@NonNull CommandListViewHolder holder, int position) {
        LibraryFunction libraryFunction = libraryNames.get(position);
        holder.mTv_name.setText(libraryFunction.name);
        holder.mTv_author.setText("作者：" + libraryFunction.author);
        if (Boolean.TRUE.equals(libraryFunction.is_liked)) {
            holder.mBtn_like.setBackgroundResource(R.drawable.icon_liked);
        } else {
            holder.mBtn_like.setBackgroundResource(R.drawable.icon_like);
        }
        if (libraryFunction.id != null) {
            holder.mBtn_like.setOnClickListener(view1 -> CommandLabAPI.like(
                    libraryFunction.id,
                    Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID),
                    result1 -> new Handler(Looper.getMainLooper()).post(() -> {
                        if (result1.isPresent()) {
                            LibraryLikeState libraryLikeState = result1.get().data;
                            if (libraryLikeState == null) {
                                ToastUtil.show(context, "点赞失败");
                            } else {
                                libraryFunction.is_liked = !Objects.equals(libraryLikeState.action, "unlike");
                                libraryFunction.like_count = libraryLikeState.like_count;
                                notifyItemChanged(position);
                            }
                        } else {
                            ToastUtil.show(context, "点赞失败，请检查您的网络状况");
                        }
                    })));
        }
        holder.mTv_likeCount.setText(String.valueOf(Objects.requireNonNullElse(libraryFunction.like_count, 0)));
        holder.itemView.setOnClickListener(view -> onLibraryChoose.accept(libraryFunction));
    }

    @Override
    public int getItemCount() {
        if (libraryNames == null) {
            return 0;
        }
        return libraryNames.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setRawJsonFunctions(List<LibraryFunction> libraryNames) {
        this.libraryNames = libraryNames;
        notifyDataSetChanged();
    }

    public static class CommandListViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private final TextView mTv_name;
        private final TextView mTv_author;
        private final View mBtn_like;
        private final TextView mTv_likeCount;

        public CommandListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mTv_name = itemView.findViewById(R.id.name);
            mTv_author = itemView.findViewById(R.id.author);
            mBtn_like = itemView.findViewById(R.id.btn_like);
            mTv_likeCount = itemView.findViewById(R.id.tv_like_count);
        }
    }
}
