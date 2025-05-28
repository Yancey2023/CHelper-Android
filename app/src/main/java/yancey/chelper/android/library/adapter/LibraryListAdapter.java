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

package yancey.chelper.android.library.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hjq.toast.Toaster;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import yancey.chelper.R;
import yancey.chelper.network.ServiceManager;
import yancey.chelper.network.library.data.LibraryFunction;
import yancey.chelper.network.library.service.CommandLabPublicService;

/**
 * 命令库列表适配器
 */
public class LibraryListAdapter extends RecyclerView.Adapter<LibraryListAdapter.CommandListViewHolder> {

    private final Context context;
    private List<LibraryFunction> libraryNames;
    private final Consumer<LibraryFunction> onLibraryShow;
    private final Consumer<LibraryFunction> onLibraryEdit;
    private final AtomicReference<Disposable> doLike;

    public LibraryListAdapter(Context context, AtomicReference<Disposable> doLike, Consumer<LibraryFunction> onLibraryShow, Consumer<LibraryFunction> onLibraryEdit) {
        this.context = context;
        this.doLike = doLike;
        this.onLibraryShow = onLibraryShow;
        this.onLibraryEdit = onLibraryEdit;
    }

    @NonNull
    @Override
    public CommandListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommandListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_library_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommandListViewHolder holder, int position) {
        LibraryFunction libraryFunction = libraryNames.get(position);
        holder.mTv_name.setText(libraryFunction.name);

        if (doLike == null) {
            holder.mTv_author.setText(libraryFunction.note);
            if (onLibraryEdit == null) {
                holder.mBtn_like.setVisibility(View.GONE);
            } else {
                holder.mBtn_like.setBackgroundResource(R.drawable.pencil);
                holder.mBtn_like.setContentDescription(context.getString(R.string.edit));
                holder.mBtn_like.setOnClickListener(v -> onLibraryEdit.accept(libraryFunction));
            }
            holder.mTv_likeCount.setVisibility(View.GONE);
        } else {
            holder.mTv_author.setText(context.getString(R.string.library_author_formatter, libraryFunction.author));
            if (Boolean.TRUE.equals(libraryFunction.is_liked)) {
                holder.mBtn_like.setBackgroundResource(R.drawable.heart_filled);
            } else {
                holder.mBtn_like.setBackgroundResource(R.drawable.heart);
            }
            holder.mBtn_like.setContentDescription(context.getString(R.string.like));
            if (libraryFunction.id != null) {
                holder.mBtn_like.setOnClickListener(v -> doLike(position));
            }
            holder.mTv_likeCount.setVisibility(View.VISIBLE);
            holder.mTv_likeCount.setText(String.valueOf(Objects.requireNonNullElse(libraryFunction.like_count, 0)));
        }
        holder.itemView.setOnClickListener(v -> onLibraryShow.accept(libraryFunction));
    }

    @SuppressLint("HardwareIds")
    void doLike(int position) {
        Disposable disposable = doLike.get();
        if (disposable != null) {
            disposable.dispose();
        }
        LibraryFunction libraryFunction = libraryNames.get(position);
        CommandLabPublicService.LikeFunctionRequest request = new CommandLabPublicService.LikeFunctionRequest();
        request.android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        disposable = ServiceManager.COMMAND_LAB_PUBLIC_SERVICE
                .like(Objects.requireNonNull(libraryFunction.id), request)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result2 -> {
                    if (!Objects.equals(result2.status, "success") || result2.data == null) {
                        Toaster.show(result2.message);
                        return;
                    }
                    CommandLabPublicService.LibraryLikeResponse libraryLikeState = result2.data;
                    libraryFunction.is_liked = !Objects.equals(libraryLikeState.action, "unlike");
                    libraryFunction.like_count = libraryLikeState.like_count;
                    notifyItemChanged(position);
                }, throwable -> Toaster.show(throwable.getMessage()));
        doLike.set(disposable);
    }

    @Override
    public int getItemCount() {
        if (libraryNames == null) {
            return 0;
        }
        return libraryNames.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setLibraryFunctions(List<LibraryFunction> libraryNames) {
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
