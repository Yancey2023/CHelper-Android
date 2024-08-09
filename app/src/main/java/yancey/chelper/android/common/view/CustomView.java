package yancey.chelper.android.common.view;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * 使用单个View组成的界面
 * 悬浮窗只能使用单个View显示界面，所以为了方便在悬浮窗模式和应用模式共享界面代码，所以设计了CustomView
 * 相比于普通的View，它有完整的声明周期监听事件
 */
public abstract class CustomView extends FrameLayout {

    /**
     * 打开新界面的相关代码
     */
    protected final Consumer<CustomView> openView;

    /**
     * @param context  上下文
     * @param openView 打开新界面的相关代码
     * @param layoutId 视图界面ID
     */
    public CustomView(@NonNull Context context, Consumer<CustomView> openView, @LayoutRes int layoutId) {
        super(context);
        this.openView = openView;
        // 添加界面
        View view = LayoutInflater.from(context).inflate(layoutId, this, false);
        addView(view);
        // 调用创建界面的事件
        onCreateView(context, view);
    }

    /**
     * 界面创建事件
     *
     * @param context 上下文
     * @param view    创建的视图
     */
    public void onCreateView(Context context, View view) {

    }

    /**
     * 界面切换后台事件
     */
    public void onPause() {

    }

    /**
     * 界面恢复前台事件
     */
    public void onResume() {

    }

    /**
     * 界面关闭事件
     */
    public void onDestroy() {

    }

    /**
     * 返回键按下事件
     *
     * @return 是否不回到上一个界面
     */
    public boolean onBackPressed() {
        return false;
    }

    /**
     * 打开新界面
     *
     * @param createView 新界面的创建方法，可以使用lambda表达式提供
     */
    protected void openView(BiFunction<Context, Consumer<CustomView>, CustomView> createView) {
        // 隐藏输入法软键盘
        ((InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getWindowToken(), 0);
        // 打开界面
        openView.accept(createView.apply(getContext(), openView));
    }

}
