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

package yancey.chelper.android.enumeration.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import redempt.crunch.data.Pair;
import yancey.chelper.R;
import yancey.chelper.android.common.util.TextWatcherUtil;
import yancey.chelper.android.enumeration.core.CustomDoubleSupplier;
import yancey.chelper.android.enumeration.data.DataVariable;

/**
 * 穷举界面的参数列表适配器
 */
public class VariableListAdapter extends RecyclerView.Adapter<VariableListAdapter.VariableListViewHolder> {

    private final Context context;
    private final List<DataVariable> variableList;

    public VariableListAdapter(Context context, List<DataVariable> variableList) {
        this.context = context;
        this.variableList = variableList;
    }

    @NonNull
    @Override
    public VariableListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VariableListViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_variable_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VariableListViewHolder holder, int position) {
        holder.mEd_name.addTextChangedListener(TextWatcherUtil.afterTextChanged(s ->
                variableList.get(holder.getLayoutPosition()).name = s.toString()));
        holder.mEd_start.addTextChangedListener(TextWatcherUtil.afterTextChanged(s ->
                variableList.get(holder.getLayoutPosition()).start = s.toString()));
        holder.mEd_interval.addTextChangedListener(TextWatcherUtil.afterTextChanged(s ->
                variableList.get(holder.getLayoutPosition()).interval = s.toString()));
        holder.btn_delete.setOnClickListener(v -> {
            delete(holder.getLayoutPosition());
            holder.btn_delete.setOnClickListener(null);
        });
    }

    @Override
    public int getItemCount() {
        if (variableList == null) {
            return 0;
        }
        return variableList.size();
    }

    public void delete(int which) {
        variableList.remove(which);
        notifyItemRemoved(which);
    }

    public void add(DataVariable variable) {
        variableList.add(variable);
        notifyItemInserted(variableList.size() - 1);
    }

    public List<Pair<String, CustomDoubleSupplier>> getValue() throws NumberFormatException {
        List<Pair<String, CustomDoubleSupplier>> result = new ArrayList<>();
        for (DataVariable dataVariable : variableList) {
            result.add(dataVariable.getValue());
        }
        return result;
    }

    public static class VariableListViewHolder extends RecyclerView.ViewHolder {
        private final EditText mEd_name, mEd_start, mEd_interval;
        private final View btn_delete;

        public VariableListViewHolder(View itemView) {
            super(itemView);
            mEd_name = itemView.findViewById(R.id.mEd_name);
            mEd_start = itemView.findViewById(R.id.mEd_start);
            mEd_interval = itemView.findViewById(R.id.mEd_interval);
            btn_delete = itemView.findViewById(R.id.delete);
        }
    }
}
