package yancey.chelper.android.favorites.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import yancey.chelper.R;
import yancey.chelper.android.common.util.ToastUtil;
import yancey.chelper.android.favorites.data.DataFavorite;
import yancey.chelper.android.favorites.dialog.EditFavoriteDialog;

/**
 * 收藏界面列表适配器
 */
public class FavoriteListAdapter extends RecyclerView.Adapter<FavoriteListAdapter.CommandListViewHolder> {

    private final Context context;
    public List<DataFavorite> dataFavoriteList;
    public final List<List<DataFavorite>> history;
    private final ClipboardManager clipboardManager;

    public FavoriteListAdapter(Context context, List<DataFavorite> dataFavoriteList) {
        this.context = context;
        this.dataFavoriteList = dataFavoriteList;
        for (DataFavorite dataFavorite : dataFavoriteList) {
            dataFavorite.isChoose = false;
        }
        history = new ArrayList<>();
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @NonNull
    @Override
    public CommandListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommandListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_favorite_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommandListViewHolder holder, int position) {
        DataFavorite data = dataFavoriteList.get(position);
        holder.mTv_title.setText(data.title);
        holder.mTv_description.setText(data.description);
        holder.mCb_isChoose.setChecked(data.isChoose);
        holder.mCb_isChoose.setOnClickListener(view -> {
            data.isChoose = !data.isChoose;
            notifyItemChanged(holder.getLayoutPosition());
        });
        if (data.dataFavoriteList == null) {
            holder.mIv_icon.setBackgroundResource(R.drawable.icon_text);
            holder.itemView.setOnClickListener(v -> {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, data.title));
                ToastUtil.show(context, "已复制");
            });
        } else {
            holder.mIv_icon.setBackgroundResource(R.drawable.icon_folder);
            holder.itemView.setOnClickListener(view -> {
                history.add(dataFavoriteList);
                setDataList(data.dataFavoriteList);
            });
        }
        holder.mIv_edit.setOnClickListener(view -> new EditFavoriteDialog(context, this, data, holder.getLayoutPosition(), false).show());
    }

    @Override
    public int getItemCount() {
        if (dataFavoriteList == null) {
            return 0;
        }
        return dataFavoriteList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataList(List<DataFavorite> dataFavoriteList) {
        this.dataFavoriteList = dataFavoriteList;
        for (DataFavorite dataFavorite : dataFavoriteList) {
            dataFavorite.isChoose = false;
        }
        notifyDataSetChanged();
    }

    public boolean back() {
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
        for (DataFavorite dataFavorite : dataFavoriteList) {
            dataFavorite.isChoose = true;
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void invert() {
        for (DataFavorite dataFavorite : dataFavoriteList) {
            dataFavorite.isChoose = !dataFavorite.isChoose;
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deselect() {
        for (DataFavorite dataFavorite : dataFavoriteList) {
            dataFavorite.isChoose = false;
        }
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void delete() {
        DataFavorite dataFavorite;
        int i = 0;
        while (i < dataFavoriteList.size()) {
            dataFavorite = dataFavoriteList.get(i);
            if (dataFavorite.isChoose) {
                dataFavoriteList.remove(i);
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
        dataFavoriteList.add(dataFavorite);
        notifyItemInserted(dataFavoriteList.size() - 1);
    }

    public void bulkCopy() {
        for (DataFavorite dataFavorite : dataFavoriteList) {
            if (dataFavorite.dataFavoriteList == null) {
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, dataFavorite.title));
            }
        }
        ToastUtil.show(context, "已批量复制");
    }

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
