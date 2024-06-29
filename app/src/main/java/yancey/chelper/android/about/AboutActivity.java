package yancey.chelper.android.about;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import yancey.chelper.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about);
        TextView tv_updateNote = findViewById(R.id.update_note);
        TextView tv_permissions = findViewById(R.id.permissions);
        TextView tv_dependencies = findViewById(R.id.dependencies);
        TextView tv_thanks = findViewById(R.id.thanks);
        tv_updateNote.setText("""
                更新日志
                
                v0.2.18 20240628
                1. 新增：支持切换内置资源包
                2. 新增：未完工的原始Json文本生成器
                3. 修改：引导用户启用并切换输入法
                4. 修复：camera set ... 命令分支的错误注释
                5. 修复：目标选择器部分参数使用小数会解析错误
                6. 修复：目标选择器部分参数应该使用局部坐标没有报错
                7. 修复：旧版execute命令语法转新版语法的错误
                8. 修复：scoreboard命令的分数参数使用*会解析错误
                
                v0.2.17 20240615
                1. 新增：命令转换输入法
                2. 修改：去除了一些execute命令转换后的不必要参数
                
                v0.2.16 20240614
                1. 新增：1.18.30命令转1.20.10命令
                2. 修改：将软件内置的资源包从1.21.0.23版本降为1.20.80.05版本
                3. 修改：对软件页面进行了一些小编辑
                4. 修复：目标选择器m参数使用整数值会解析错误
                5. 修复：loot命令使用的战利品ID应该使用双引号
                
                v0.2.15 20240606
                1. 新增：loot命令的战利品表补全提示
                2. 修改：最低sdk降为24，从安卓7开始支持
                3. 修复：xp命令的经验等级不能识别以L结尾的经验等级
                4. 修复：局部坐标的补全提示有误
                5. 修复：补全单个符号时会替换掉后面的符号
                
                v0.2.14 20240531
                1. 修复：软件在部分情况会崩溃
                2. 修复：execute命令的解析
                
                v0.2.13 20240530
                1. 修复：目标选择器有些参数其实不可以用=!
                2. 修复：execute只有if|unless分支的命令才可以作为命令的结尾
                3. 修复：对于坐标参数，补全~或^之后，不应该自动加空格
                4. 修复：方块状态的字符串参数应该使用双引号
                5. 修复：replace命令使用replace时报错错误
                6. 修改：悬浮窗图标缩小
                7. 修改：在悬浮窗模式去掉收藏和穷举图标，因为它们在悬浮窗模式中有bug
                8. 新增：空格补全提示
                9. 新增：支持资源包的二进制读写，资源包读取速度加快
                
                v0.2.12 20240514
                1. 修复：对于一些命令参数，解析带有数字和符号的内容会产生错误
                2. 修复：悬浮窗可以拖到屏幕外面
                
                v0.2.11 20240511
                1. 新增：请求悬浮窗权限的弹窗
                2. 修复：部分设备使用悬浮窗会崩溃
                
                v0.2.10 20240510
                1. 新增：关于界面
                2. 新增：支持进入应用后自动不恢复上次输入的命令，在设置界面关闭
                3. 新增：紧凑模式，在设置界面开启
                4. 修改：部分界面布局和样式
                5. 修复：通过注释搜索不出命令名字
                
                v0.2.9 20240509
                1. 新增：粒子ID注释（因为wiki上的注释不全，所以仍然有许多的粒子ID没有注释）
                2. 新增：music命令的曲目名称支持补全提示
                3. 修复：重复的实体ID
                
                v0.2.8 20240508
                1. 修改：将方块ID和方块状态更新至1.21.0.23命令
                
                v0.2.7 20240505
                1. 新增：设置界面，有2个设置：根据光标位置获取补全提示 复制后隐藏悬浮窗
                2. 修复：返回到桌面再进入获取不了补全提示
                
                v0.2.6 20240505
                1. 修复：打开页面会闪退
                2. 修复：悬浮窗会导致其它应用的输入框打不开
                
                v0.2.5 20240505
                1. 新增：悬浮窗功能
                2. 修改：删除了ability命令，增加了script和scriptevent命令
                3. 修改：除了方块ID和方块状态，所有ID更新至1.21.0.23，并增加了配方表ID
                4. 修复：使用中文获取补全提示只有省略了命名空间的ID
                5. 修复：difficulty命令的难度等级有错误
                6. 修复：function命令的函数名错误地需要转义字符
                7. 修复：scoreboard命令的运算操作识别错误
                8. 修复：recipe命令的配方不支持*
                9. 修复：使用撤回按钮复原后输入框的光标错误
                
                v0.2.5以前
                无记录""");
        tv_permissions.setText("""
                权限
                
                android.permission.SYSTEM_ALERT_WINDOW
                描述：在其他应用上方显示
                用途：显示悬浮窗
                备注：非必须权限。用户可以拒绝，但拒绝后无法提供悬浮窗服务。
                
                """);
        tv_dependencies.setText("""
                依赖
                
                链接：https://github.com/nlohmann/json
                协议：MIT license(https://github.com/nlohmann/json/blob/master/LICENSE.MIT)
                介绍：一个c++的json库
                用途：资源包的读写
                
                链接：https://github.com/nemequ/hedley
                协议：Creative Commons Zero v1.0 Universal(https://github.com/nemequ/hedley/blob/master/COPYING)
                介绍：一些用于开启编译器特性的c++的宏
                用途：开启一些编译器特性，优化程序性能，提高代码质量
                
                链接：https://github.com/boxbeam/Crunch
                协议：MIT license(https://github.com/boxbeam/Crunch/blob/master/LICENSE)
                介绍：一个java计算数学表达式的库
                用途：穷举界面的数学表达式计算
                
                链接：https://github.com/princekin-f/EasyFloat
                协议：Apache-2.0 license(https://github.com/princekin-f/EasyFloat/blob/master/LICENSE)
                介绍：安卓悬浮窗框架
                用途：方便地实现安卓悬浮窗
                
                链接：https://github.com/google/gson
                协议：Apache-2.0 license(https://github.com/google/gson/blob/main/LICENSE)
                介绍：一个Java的json库，可将Java对象序列化
                用途：读写软件设置
                
                链接：https://github.com/androidx/androidx
                协议：Apache-2.0 license(https://github.com/androidx/androidx/blob/androidx-main/LICENSE.txt)
                介绍：androidx命名空间下的Android Jetpack扩展库的开发环境
                用途：提供安卓开发环境""");
        tv_thanks.setText("""
                致谢
                
                链接：https://github.com/XeroAlpha/caidlist
                协议：GPL-3.0 license(https://github.com/XeroAlpha/caidlist/blob/master/LICENSE)
                帮助：提供我的世界基岩版中的各种ID及其注释
                
                链接：https://zh.minecraft.wiki
                协议：CC BY-NC-SA 3.0(https://creativecommons.org/licenses/by-nc-sa/3.0/deed.zh-hans)
                帮助：
                     1. 提供基岩版命令文档
                     2. 提供我的世界基岩版中的方块状态数据及其注释
                     3. 提供我的世界基岩版中的粒子ID注释
                
                也要感谢大家的支持，你们是我继续创作的最大动力！！！""");
    }

}
