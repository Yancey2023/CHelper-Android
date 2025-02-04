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

package yancey.chelper.network.api.library.openlans;

import android.text.TextUtils;
import android.util.Pair;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import yancey.chelper.network.data.openlans.LibraryLikeState;
import yancey.chelper.network.data.openlans.LibraryFunction;
import yancey.chelper.network.data.openlans.LibraryFunctionUpload;
import yancey.chelper.network.data.openlans.LibraryFunctions;
import yancey.chelper.network.data.openlans.Result;
import yancey.chelper.network.util.NetworkUtil;

public class CommandLabAPI {

    private static final HttpUrl URL_BASE = Objects.requireNonNull(HttpUrl.parse("https://abyssous.site:6378"));

    private static final TypeToken<Result<LibraryFunctionUpload>> TYPE_FUNCTION_UPLOAD = new TypeToken<>() {
    };

    private static final TypeToken<Result<LibraryFunctions>> TYPE_FUNCTIONS = new TypeToken<>() {
    };

    private static final TypeToken<Result<LibraryFunction>> TYPE_FUNCTION = new TypeToken<>() {
    };

    private static final TypeToken<Result<LibraryLikeState>> TYPE_LIKE_STATE = new TypeToken<>() {
    };

    private static final TypeToken<Result<Object>> TYPE_OBJECT = new TypeToken<>() {
    };

    /**
     * 把命令包提交到审核
     *
     * @param library  命令包
     * @param callback 回调函数
     */
    public static void upload(
            LibraryFunction library,
            Consumer<Pair<String, Result<LibraryFunctionUpload>>> callback
    ) {
        if (library.author == null ||
            library.name == null ||
            library.note == null ||
            library.version == null ||
            library.tags == null ||
            library.content == null
        ) {
            callback.accept(new Pair<>("发送失败，请检查您填写的参数", null));
            return;
        }
        String content = "@author = " + library.author + '\n' +
                         "@name = " + library.name + '\n' +
                         "@note = " + library.note + '\n' +
                         "@version = " + library.version + '\n' +
                         "@tags = " + String.join(",", library.tags) + '\n' +
                         "###Function###\n" +
                         library.content + '\n' +
                         "###End###";
        if (content.getBytes(StandardCharsets.UTF_8).length > 1000000) {
            callback.accept(new Pair<>("内容长度过长，请减少字数", null));
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        NetworkUtil.enqueueAndReadJson(
                new Request.Builder()
                        .url(URL_BASE.newBuilder()
                                .addPathSegment("upload")
                                .build())
                        .post(RequestBody.create(jsonObject.toString(), NetworkUtil.JSON_TYPE)),
                result -> callback.accept(result
                        .map(result0 -> new Pair<>((String) null, result0))
                        .orElse(new Pair<>("发送失败，请检查您的网络状况", null))
                ),
                TYPE_FUNCTION_UPLOAD
        );
    }

    public enum SortType {
        SORT_BY_CREATE_TIME, // 最新更新的函数排在最前面
        SORT_BY_ID, // ID较大的函数（较新创建的）排在最前面
    }

    /**
     * 获取函数列表
     *
     * @param page     页码（从1开始）
     * @param per_page 每页数量（默认10）
     * @param author   作者筛选（可选）
     * @param keyword  函数名称和注释搜索（可选）
     * @param tags     标签筛选（可选，逗号分隔）
     * @param sort     排序方式（可选）
     */
    public static void getAllFunctions(
            int page,
            int per_page,
            @Nullable String author,
            @Nullable String keyword,
            @Nullable String tags,
            @Nullable SortType sort,
            @Nullable String androidId,
            @NotNull Consumer<Optional<Result<LibraryFunctions>>> callback
    ) {
        HttpUrl.Builder urlBuilder = URL_BASE.newBuilder()
                .addPathSegment("functions")
                .addQueryParameter("page", String.valueOf(page))
                .addQueryParameter("per_page", String.valueOf(per_page));
        if (!TextUtils.isEmpty(author)) {
            urlBuilder.addQueryParameter("author", author);
        }
        if (!TextUtils.isEmpty(keyword)) {
            urlBuilder.addQueryParameter("search", keyword);
        }
        if (!TextUtils.isEmpty(tags)) {
            urlBuilder.addQueryParameter("tags", String.join(",", tags));
        }
        if (sort != null) {
            urlBuilder.addQueryParameter("sort", switch (sort) {
                case SORT_BY_CREATE_TIME -> "time";
                case SORT_BY_ID -> "id";
            });
        }
        if (!TextUtils.isEmpty(androidId)) {
            urlBuilder.addQueryParameter("android_id", androidId);
        }
        NetworkUtil.enqueueAndReadJson(
                new Request.Builder().url(urlBuilder.build()).get(),
                callback,
                TYPE_FUNCTIONS
        );
    }

    /**
     * 获取函数
     *
     * @param id ID
     */
    public static void getFunction(
            int id,
            @NotNull String androidId,
            @NotNull Consumer<Optional<Result<LibraryFunction>>> callback
    ) {
        NetworkUtil.enqueueAndReadJson(
                new Request.Builder()
                        .url(URL_BASE.newBuilder()
                                .addPathSegment("function")
                                .addPathSegment(String.valueOf(id))
                                .addQueryParameter("android_id", androidId)
                                .build())
                        .get(),
                callback,
                TYPE_FUNCTION
        );
    }

    /**
     * 通过密钥获取函数
     *
     * @param user_key 用户密钥
     */
    public static void getFunctionByKey(
            @NotNull String user_key,
            @NotNull Consumer<Optional<Result<LibraryFunction>>> callback
    ) {
        NetworkUtil.enqueueAndReadJson(
                new Request.Builder()
                        .url(URL_BASE.newBuilder()
                                .addPathSegment("function")
                                .addPathSegment("key")
                                .addPathSegment(user_key)
                                .build())
                        .get(),
                callback,
                TYPE_FUNCTION
        );
    }

    /**
     * 删除函数
     *
     * @param id       ID
     * @param auth_key 用户密钥或管理员密钥
     */
    public static void deleteFunction(
            int id,
            @NotNull String auth_key,
            @NotNull Consumer<String> callback
    ) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("auth_key", auth_key);
        NetworkUtil.enqueueAndReadJson(
                new Request.Builder()
                        .url(URL_BASE.newBuilder()
                                .addPathSegment("function")
                                .addPathSegment(String.valueOf(id))
                                .build())
                        .delete(RequestBody.create(jsonObject.toString(), NetworkUtil.JSON_TYPE)),
                result -> callback.accept(result
                        .map(result0 -> Objects.equals(result0.status, "success") ? "删除成功" : "密钥错误")
                        .orElse("网络请求失败，请检查您的网络状况或稍后重试")),
                TYPE_OBJECT
        );
    }


    /**
     * 把命令包提交到审核
     *
     * @param library  命令包
     * @param auth_key 用户密钥或管理员密钥
     * @param callback 回调函数
     */
    public static void update(
            LibraryFunction library,
            String auth_key,
            Consumer<Pair<String, Result<LibraryFunctionUpload>>> callback
    ) {
        if (library.author == null ||
            library.name == null ||
            library.note == null ||
            library.version == null ||
            library.tags == null ||
            library.content == null
        ) {
            callback.accept(new Pair<>("发送失败，请检查您填写的参数", null));
            return;
        }
        String content = "@author = " + library.author + '\n' +
                         "@name = " + library.name + '\n' +
                         "@note = " + library.note + '\n' +
                         "@version = " + library.version + '\n' +
                         "@tags = " + String.join(",", library.tags) + '\n' +
                         "###Function###\n" +
                         library.content + '\n' +
                         "###End###";
        if (content.getBytes(StandardCharsets.UTF_8).length > 1000000) {
            callback.accept(new Pair<>("内容长度过长，请减少字数", null));
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("content", content);
        jsonObject.addProperty("auth_key", auth_key);
        NetworkUtil.enqueueAndReadJson(
                new Request.Builder()
                        .url(URL_BASE.newBuilder()
                                .addPathSegment("function")
                                .addPathSegment(String.valueOf(library.id))
                                .build())
                        .put(RequestBody.create(jsonObject.toString(), NetworkUtil.JSON_TYPE)),
                result -> callback.accept(result
                        .map(result0 -> new Pair<>((String) null, result0))
                        .orElse(new Pair<>("发送失败，请检查您的网络状况", null))
                ),
                TYPE_FUNCTION_UPLOAD
        );
    }

    public static void like(
            int id,
            @NotNull String androidId,
            Consumer<Optional<Result<LibraryLikeState>>> callback
    ) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("android_id", androidId);
        NetworkUtil.enqueueAndReadJson(
                new Request.Builder()
                        .url(URL_BASE.newBuilder()
                                .addPathSegment("function")
                                .addPathSegment(String.valueOf(id))
                                .addPathSegment("like")
                                .build())
                        .post(RequestBody.create(jsonObject.toString(), NetworkUtil.JSON_TYPE)),
                callback,
                TYPE_LIKE_STATE
        );
    }

}
