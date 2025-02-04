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

package yancey.chelper.network.data.openlans;

import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class LibraryFunction {

    @Nullable
    public Integer id;// 函数ID
    @Nullable
    public String uuid;// 函数UUID
    @Nullable
    public String name;// 函数名称
    @Nullable
    public String content;// 函数内容
    @Nullable
    public String author;// 作者
    @Nullable
    public String note;// 说明
    @Nullable
    public List<String> tags;// 标签
    @Nullable
    public String version;// 版本号
    @Nullable
    public String created_at;// 创建时间，例：2025-02-03 18:45:43
    @Nullable
    public String preview;// 命令预览
    @Nullable
    public Integer like_count;// 点赞总数
    @Nullable
    public Boolean is_liked;// 当前设备是否已点赞
    @Nullable
    public String user_key;// 随机生成的密钥
    @Nullable
    public String backup_file;// 备份文件名

}
