package yancey.chelper.android.expression;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
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
import yancey.chelper.android.util.DataVariable;
import yancey.chelper.expression.CustomDoubleSupplier;

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
        holder.mEd_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                variableList.get(holder.getLayoutPosition()).name = s.toString();
            }
        });
        holder.mEd_start.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                variableList.get(holder.getLayoutPosition()).start = s.toString();
            }
        });
        holder.mEd_interval.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                variableList.get(holder.getLayoutPosition()).interval = s.toString();
            }
        });
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

    public List<Pair<String, CustomDoubleSupplier>> getValue() throws NumberFormatException{
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
