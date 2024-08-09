package yancey.chelper.android.completion.adater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import yancey.chelper.R;
import yancey.chelper.core.CHelperGuiCore;
import yancey.chelper.core.Suggestion;

/**
 * 补全提示列表适配器，用于管理列表
 */
public class SuggestionListAdapter extends RecyclerView.Adapter<SuggestionListAdapter.CommandListViewHolder> {

    private final Context context;
    private final CHelperGuiCore core;
    private final boolean isCrowed;
    private String structure, description;

    public SuggestionListAdapter(Context context, CHelperGuiCore core, boolean isCrowed) {
        this.context = context;
        this.core = core;
        this.isCrowed = isCrowed;
        this.structure = context.getString(R.string.tv_write_command_title);
        this.description = context.getString(R.string.tv_write_command_description);
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getItemViewType(int position) {
        if (!isCrowed) {
            return 0;
        } else if (position > 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public CommandListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommandListViewHolder(LayoutInflater.from(context).inflate(switch (viewType) {
            case 0 -> R.layout.layout_suggestion;
            case 1 -> R.layout.layout_suggestion_crowded_item;
            case 2 -> R.layout.layout_suggestion_crowded_header_item;
            default -> throw new IllegalStateException("Unexpected value: " + viewType);
        }, parent, false));
    }

    @Override
    @SuppressLint("SetTextI18n")
    public void onBindViewHolder(@NonNull CommandListViewHolder holder, int position) {
        if (isCrowed && position == 0) {
            holder.mTv_commandName.setText(structure);
            holder.mTv_commandDescription.setText(description);
            return;
        }
        Suggestion data = core.getSuggestion(isCrowed ? position - 1 : position);
        if (data == null) {
            holder.mTv_commandName.setText(null);
            if (holder.mTv_commandDescription != null) {
                holder.mTv_commandDescription.setText(null);
            }
            holder.itemView.setOnClickListener(null);
        } else {
            if (holder.mTv_commandDescription == null) {
                holder.mTv_commandName.setText(data.name + " - " + data.description);
            } else {
                holder.mTv_commandName.setText(data.name);
                holder.mTv_commandDescription.setText(data.description);
            }
            holder.itemView.setOnClickListener(view -> core.onItemClick(isCrowed ? position - 1 : position));
        }
    }

    @Override
    public int getItemCount() {
        return isCrowed ? core.getSuggestionsSize() + 1 : core.getSuggestionsSize();
    }

    public static class CommandListViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;
        private final TextView mTv_commandName, mTv_commandDescription;

        public CommandListViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.mTv_commandName = itemView.findViewById(R.id.command_list_tv_command_name);
            this.mTv_commandDescription = itemView.findViewById(R.id.command_list_tv_command_description);
        }
    }
}
