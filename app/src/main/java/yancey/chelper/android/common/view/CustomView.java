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

package yancey.chelper.android.common.view;

import android.app.Service;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 使用单个View组成的界面
 * 悬浮窗只能使用单个View显示界面，所以为了方便在悬浮窗模式和应用模式共享界面代码，所以设计了CustomView
 * 相比于普通的View，它有完整的声明周期监听事件
 */
public abstract class CustomView extends FrameLayout {

    /**
     * 运行环境：应用 / 悬浮窗
     */
    public enum Environment {
        APPLICATION, FLOATING_WINDOW
    }

    /**
     * 打开新界面的相关代码
     */
    protected final Consumer<CustomView> openView;

    /**
     * 返回界面的相关代码
     */
    protected final Supplier<Boolean> backView;

    /**
     * 运行环境：应用 / 悬浮窗
     */
    protected final Environment environment;

    /**
     * @param context     上下文
     * @param openView    打开新界面的相关代码
     * @param environment 运行环境
     * @param layoutId    视图界面ID
     * @param privateData 私有数据
     */
    public CustomView(
            @NonNull Context context,
            @NonNull Consumer<CustomView> openView,
            @NonNull Supplier<Boolean> onBackPress,
            @NonNull Environment environment,
            @LayoutRes int layoutId,
            @Nullable Object privateData
    ) {
        super(context);
        this.openView = openView;
        this.backView = onBackPress;
        this.environment = environment;
        // 添加界面
        View view = LayoutInflater.from(context).inflate(layoutId, this, false);
        addView(view);
        // 调用创建界面的事件
        onCreateView(context, view, privateData);
    }

    /**
     * @param context     上下文
     * @param openView    打开新界面的相关代码
     * @param environment 运行环境
     * @param layoutId    视图界面ID
     */
    public CustomView(
            @NonNull Context context,
            @NonNull Consumer<CustomView> openView,
            @NonNull Supplier<Boolean> backView,
            @NonNull Environment environment,
            @LayoutRes int layoutId
    ) {
        this(context, openView, backView, environment, layoutId, null);
    }

    /**
     * 界面创建事件
     *
     * @param context 上下文
     * @param view    创建的视图
     */
    public void onCreateView(@NonNull Context context, @NonNull View view, @Nullable Object privateData) {

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

    public interface ViewInterface {

        CustomView createView(Context context, Consumer<CustomView> openView, Supplier<Boolean> backView, Environment environment);
    }

    /**
     * 打开新界面
     *
     * @param createView 新界面的创建方法，可以使用lambda表达式提供
     */
    protected void openView(@NonNull ViewInterface createView) {
        // 隐藏输入法软键盘
        ((InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(getWindowToken(), 0);
        // 打开界面
        openView.accept(createView.createView(getContext(), openView, backView, environment));
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        super.dispatchTouchEvent(motionEvent);
        return true;
    }

}
