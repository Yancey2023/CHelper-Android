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

package yancey.chelper.network.api.library.averychims;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

import yancey.chelper.network.data.averychims.Library;

public class CommandLibraryAPI {

    /**
     * 获取所有命令包的名字
     *
     * @param callback 回调函数
     */
    public static void getAllLibraryNames(@NotNull Consumer<List<String>> callback) {
        throw new RuntimeException("this method is not open source");
    }


    /**
     * 获取命令包的介绍和作者
     *
     * @param name     命令库的名字
     * @param callback 回调函数
     */
    public static void getDescriptionAndAuthor(@NotNull String name, @NotNull Consumer<Library> callback) {
        throw new RuntimeException("this method is not open source");
    }

    /**
     * 获取命令包的内容
     *
     * @param name     命令库的名字
     * @param callback 回调函数
     */
    public static void getContent(@NotNull String name, @NotNull Consumer<Library> callback) {
        throw new RuntimeException("this method is not open source");
    }

    /**
     * 把命令包提交到审核
     *
     * @param library  命令包
     * @param callback 回调函数
     */
    @SuppressWarnings("CharsetObjectCanBeUsed")
    public static void upload(Library library, Consumer<String> callback) {
        throw new RuntimeException("this method is not open source");
    }

}
