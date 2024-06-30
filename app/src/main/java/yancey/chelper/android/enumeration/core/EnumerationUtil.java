package yancey.chelper.android.enumeration.core;

import java.util.ArrayList;
import java.util.List;

import redempt.crunch.Crunch;
import redempt.crunch.data.Pair;
import redempt.crunch.functional.EvaluationEnvironment;

public class EnumerationUtil {

    private EnumerationUtil() {

    }

    /**
     * 使用${}作为表达式
     * <p>${a*(b+c)+10,int},输出整数</p>
     * <p>${sin(x)+pi,float},输出小数</p>
     *
     * @param command 带有表达式的命令
     * @param pairs   表达式变量
     * @param times   次数
     * @return 经过处理的字符串
     */
    public static String run(String command, List<Pair<String, CustomDoubleSupplier>> pairs, int times) {
        //新建一个运行环境,把变量名传入
        EvaluationEnvironment env = new EvaluationEnvironment();
        for (Pair<String, CustomDoubleSupplier> pair : pairs) {
            env.addLazyVariable(pair.getFirst(), pair.getSecond());
        }
        //解析命令中的表达式
        List<Param> params = new ArrayList<>();
        int start = 0;
        while (true) {
            int a = command.indexOf("${", start);
            if (a == -1) {
                break;
            }
            int b = command.indexOf('}', a);
            if (b == -1) {
                break;
            }
            String content = command.substring(a + 2, b).strip();
            boolean isFloat;
            String expression;
            int c = content.lastIndexOf(',');
            if (c == -1) {
                expression = content;
                isFloat = true;
            } else {
                expression = content.substring(0, c);
                isFloat = !content.substring(c + 1).strip().equals("int");
            }
            params.add(new CommonParam(command.substring(start, a)));
            params.add(new ExpressionParam(Crunch.compileExpression(expression, env), isFloat));
            start = b + 1;
        }
        if (start != command.length()) {
            params.add(new CommonParam(command.substring(start)));
        }
        //开始运行
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirst = true;
        for (int i = 0; i < times; i++) {
            if (isFirst) {
                isFirst = false;
            } else {
                stringBuilder.append('\n');
            }
            for (Param param : params) {
                stringBuilder.append(param.getValue());
            }
            for (Pair<String, CustomDoubleSupplier> pair : pairs) {
                pair.getSecond().nextTime();
            }
        }
        return stringBuilder.toString();
    }

}
