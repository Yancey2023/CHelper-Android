package yancey.chelper.core;

import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import yancey.chelper.android.common.util.SelectedString;
import yancey.chelper.android.common.view.CommandEditText;
import yancey.chelper.android.completion.data.Settings;

/**
 * 对CHelperCore的包装，增加了Gui相关的逻辑
 */
public class CHelperGuiCore implements Closeable {

    /**
     * 软件内核
     */
    private @Nullable CHelperCore core;
    /**
     * 补全建议列表更新要执行的操作
     */
    private @Nullable Runnable updateSuggestions;
    /**
     * 更新命令语法结构时要执行的操作
     */
    private @Nullable Consumer<String> updateStructure;
    /**
     * 更新命令参数介绍时要执行的操作
     */
    private @Nullable Consumer<String> updateDescription;
    /**
     * 输入框
     */
    private @Nullable CommandEditText commandEditText;
    /**
     * 错误原因显示
     */
    private @Nullable TextView mtv_errorReasons;
    // TODO 未来要把这个类与安卓组件彻底隔绝开，不要存储commandEditText和mtv_errorReasons

    public CHelperGuiCore(@Nullable CHelperCore core) {
        this.core = core;
    }

    public CHelperGuiCore() {
        this(null);
    }

    public void setCommandEditText(@NotNull CommandEditText commandEditText) {
        this.commandEditText = commandEditText;
    }

    public void setUpdateSuggestions(@NotNull Runnable updateSuggestions) {
        this.updateSuggestions = updateSuggestions;
    }

    public void setUpdateStructure(@NotNull Consumer<String> updateStructure) {
        this.updateStructure = updateStructure;
    }

    public void setUpdateDescription(@NotNull Consumer<String> updateDescription) {
        this.updateDescription = updateDescription;
    }

    public void setTvErrorReasons(@NotNull TextView mtv_errorReasons) {
        this.mtv_errorReasons = mtv_errorReasons;
    }

    /**
     * 上一次输入的命令
     */
    private String lastInput = "";
    /**
     * 上一次的光标位置
     */
    private int lastSelection = 0;

    /**
     * 当光标改变时要执行的内容
     */
    public void onSelectionChanged() {
        if (commandEditText == null) {
            return;
        }
        String text = Objects.requireNonNull(commandEditText.getText()).toString();
        if (text.isEmpty()) {
            System.out.println("onSelectionChanged(empty text): " + text);
            // 输入内容为空
            lastInput = text;
            // 显示欢迎词
            if (updateStructure != null) {
                updateStructure.accept("欢迎使用CHelper");
            }
            // 显示作者信息
            if (updateDescription != null) {
                updateDescription.accept("作者：Yancey");
            }
            // 隐藏错误原因视图
            if (mtv_errorReasons != null) {
                mtv_errorReasons.setVisibility(View.GONE);
            }
            // 通知内核
            if (core != null) {
                core.onTextChanged(text, 0);
            }
            // 更新补全提示
            if (updateSuggestions != null) {
                updateSuggestions.run();
            }
            lastSelection = 0;
            return;
        }
        if (core == null) {
            return;
        }
        if (text.equals(lastInput)) {
            if (lastSelection == commandEditText.getSelectionStart()) {
                System.out.println("onSelectionChanged(same text): " + text);
                return;
            }
            lastSelection = commandEditText.getSelectionStart();
            // 文本内容不变和光标都改变了
            // 如果关闭了"根据光标位置提供补全提示"，就什么都不做
            if (!Settings.getInstance(commandEditText.getContext()).isCheckingBySelection) {
                return;
            }
            // 因为c++内核使用utf-8，所以传给c++内核之前要获取真正的光标位置
            int selectionStart = text.substring(0, commandEditText.getSelectionStart()).getBytes(StandardCharsets.UTF_8).length;
            // 通知内核
            core.onSelectionChanged(selectionStart);
        } else {
            lastInput = text;
            lastSelection = commandEditText.getSelectionStart();
            // 文本内容和光标都改变了
            // 因为c++内核使用utf-8，所以传给c++内核之前要获取真正的光标位置
            // 如果关闭了"根据光标位置提供补全提示"，就在通知内核时把光标位置当成在文本最后面
            System.out.println("onSelectionChanged(changed text): " + text);
            int selectionStart;
            if (Settings.getInstance(commandEditText.getContext()).isCheckingBySelection) {
                selectionStart = text.substring(0, commandEditText.getSelectionStart()).getBytes(StandardCharsets.UTF_8).length;
            } else {
                selectionStart = text.getBytes(StandardCharsets.UTF_8).length;
            }
            // 通知内核
            core.onTextChanged(text, selectionStart);
            if (updateStructure != null) {
                // 更新命令语法结构
                updateStructure.accept(core.getStructure());
            }
            if (mtv_errorReasons != null) {
                // 更新错误原因，如果没有错误，就隐藏视图
                String errorReasons = core.getErrorReasons();
                if (errorReasons == null || errorReasons.isEmpty()) {
                    mtv_errorReasons.setVisibility(View.GONE);
                } else {
                    mtv_errorReasons.setText(errorReasons);
                    mtv_errorReasons.setVisibility(View.VISIBLE);
                }
            }
            // 更新颜色
            commandEditText.setColors(core.getColors());
        }
        if (updateDescription != null) {
            // 更新命令参数介绍
            updateDescription.accept(core.getDescription());
        }
        if (updateSuggestions != null) {
            // 更新补全提示列表
            updateSuggestions.run();
        }
    }

    /**
     * 当补全提示被使用时要执行的内容
     *
     * @param which 第几个补全提示，从0开始
     */
    public void onItemClick(int which) {
        if (core == null || commandEditText == null) {
            return;
        }
        String string = core.onSuggestionClick(which);
        if (string != null) {
            commandEditText.setSelectedString(new SelectedString(new SpannableStringBuilder(string), string.length(), string.length()));
        }
    }

    /**
     * 获取当前命令的补全提示数量
     */
    public int getSuggestionsSize() {
        if (core == null) {
            return 0;
        }
        return core.getSuggestionsSize();
    }

    /**
     * 获取当前命令其中一个补全提示
     *
     * @param which 第几个补全提示，从0开始
     */
    public @Nullable Suggestion getSuggestion(int which) {
        if (core == null) {
            return null;
        }
        return core.getSuggestion(which);
    }

    /**
     * 获取当前命令的所有补全提示
     * 由于性能原因，不建议使用这个方法，建议按需获取
     *
     * @return 所有补全提示
     */
    public @Nullable List<Suggestion> getSuggestions() {
        if (core == null) {
            return null;
        }
        return core.getSuggestions();
    }

    /**
     * 获取软件内核
     *
     * @return 软件内核
     */
    @Nullable
    public CHelperCore getCore() {
        return core;
    }

    /**
     * 设置软件内核
     *
     * @param core 软件内核
     */
    public void setCore(@Nullable CHelperCore core) {
        // 先关闭之前的内核
        if (this.core != null) {
            this.core.close();
        }
        // 设置新内核
        this.core = core;
        // 更新界面
        lastInput = "";
        onSelectionChanged();
    }

    /**
     * 关闭内核，释放内存
     */
    @Override
    public void close() {
        if (core != null) {
            core.close();
            core = null;
        }
        updateSuggestions = null;
        updateStructure = null;
        updateDescription = null;
        commandEditText = null;
        mtv_errorReasons = null;
        lastInput = "";
    }

}
