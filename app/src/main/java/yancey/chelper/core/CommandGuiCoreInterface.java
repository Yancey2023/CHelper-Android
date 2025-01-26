package yancey.chelper.core;

import org.jetbrains.annotations.Nullable;

public interface CommandGuiCoreInterface {

    /**
     * 是否更新命令语法结构
     */
    boolean isUpdateStructure();

    /**
     * 是否更新命令参数介绍
     */
    boolean isUpdateDescription();

    /**
     * 是否更新命令错误原因
     */
    boolean isUpdateErrorReason();

    /**
     * 是否根据光标位置提供补全提示
     */
    boolean isCheckingBySelection();

    /**
     * 是否语法高亮
     */
    boolean isSyntaxHighlight();

    /**
     * 更新命令语法结构
     */
    void updateStructure(@Nullable String structure);

    /**
     * 更新命令参数介绍
     */
    void updateDescription(@Nullable String description);

    /**
     * 更新命令错误原因
     */
    void updateErrorReason(@Nullable ErrorReason[] errorReasons);

    /**
     * 补全建议列表更新要执行的操作
     */
    void updateSuggestions();

    /**
     * 获取输入框文字
     */
    SelectedString getSelectedString();

    /**
     * 设置输入框文字
     */
    void setSelectedString(SelectedString selectedString);

    /**
     * 更新语法高亮
     */
    void updateSyntaxHighlight(int[] colors);

}