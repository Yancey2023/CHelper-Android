package yancey.chelper.android.favorites.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Objects;

import yancey.chelper.R;
import yancey.chelper.android.favorites.data.DataFavorite;
import yancey.chelper.android.favorites.adapter.FavoriteListAdapter;

/**
 * 修改收藏命令的对话框
 */
public class EditFavoriteDialog extends Dialog {

    private final DataFavorite dataFavorite;
    private final int position;
    private final FavoriteListAdapter adapter;
    private final boolean isCreating;
    private EditText mEd_command, mEd_description;

    public EditFavoriteDialog(@NonNull Context context, FavoriteListAdapter adapter, DataFavorite dataFavorite, int position, boolean isCreating) {
        super(context);
        this.adapter = adapter;
        this.dataFavorite = dataFavorite;
        this.position = position;
        this.isCreating = isCreating;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_favorite);
        mEd_command = findViewById(R.id.ed_command);
        mEd_description = findViewById(R.id.ed_description);
        TextView mTv_title = findViewById(R.id.tv_title);
        if (isCreating) {
            mTv_title.setText(R.string.tv_create);
        } else {
            mTv_title.setText(R.string.tv_edit);
            mEd_command.setText(dataFavorite.title);
            mEd_description.setText(dataFavorite.description);
        }
        ((TextView) findViewById(R.id.tv_content)).setText(dataFavorite.dataFavoriteList == null ? R.string.tv_command_name : R.string.tv_filename);
        findViewById(R.id.btn_confirm).setOnClickListener(v -> {
            dataFavorite.title = mEd_command.getText().toString();
            dataFavorite.description = mEd_description.getText().toString();
            if (isCreating) {
                adapter.add(dataFavorite);
            } else {
                adapter.notifyItemChanged(position);
            }
            dismiss();
        });
        findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());
        Window window = Objects.requireNonNull(getWindow());
        window.setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams attributes = window.getAttributes();
        Point point = new Point();
        window.getWindowManager().getDefaultDisplay().getSize(point);
        attributes.width = (int) (((double) point.x) * 0.95d);
        window.setAttributes(attributes);
    }
}
