package yancey.chelper.core;

import java.lang.annotation.Native;

/**
 * 与c++交互时获取的命令的错误原因
 */
public class ErrorReason {
    @Native
    public String errorReason;
    @Native
    public int start, end;
}
