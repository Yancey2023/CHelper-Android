/**
 * It is part of CHelper. CHelper is a command helper for Minecraft Bedrock Edition.
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

package yancey.chelper.android.enumeration.core;

import java.util.ArrayList;
import java.util.List;

import redempt.crunch.Crunch;
import redempt.crunch.data.Pair;
import redempt.crunch.functional.EvaluationEnvironment;

/**
 * 穷举表达式的工具类
 */
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
            params.add(new StringParam(command.substring(start, a)));
            params.add(new ExpressionParam(Crunch.compileExpression(expression, env), isFloat));
            start = b + 1;
        }
        if (start != command.length()) {
            params.add(new StringParam(command.substring(start)));
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
        // 返回结果
        return stringBuilder.toString();
    }

}
