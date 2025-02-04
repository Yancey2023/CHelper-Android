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

package yancey.chelper.network.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.brotli.BrotliInterceptor;

public class NetworkUtil {

    private static final boolean isEnableLog = false;
    private static OkHttpClient CLIENT;
    private static final Gson GSON = new Gson();
    private static final String TAG = "CommandLibraryAPI";
    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");

    /**
     * 输出错误信息
     *
     * @param isSuccess 网络请求是否成功
     * @param url       URL
     * @param reason    错误原因
     * @param content   内容
     * @param throwable 错误
     */
    private static void logError(boolean isSuccess, HttpUrl url, Integer code, String reason, String content, Throwable throwable) {
        if (isEnableLog) {
            String message = String.format(Locale.getDefault(),
                    "[NetworkException] isSuccess: %s, url: %s, code: %s, reason: %s, content: %s",
                    isSuccess, url, code == null ? "未知" : String.valueOf(code), reason, content);
            if (throwable != null) {
                Log.e(TAG, message, throwable);
            } else {
                Log.e(TAG, message);
            }
        }
    }

    /**
     * 读取网络请求回复的数据
     *
     * @param response 网络请求的回复
     * @return 读取到的数据
     */
    public static byte[] readRequestBodyAllBytes(@NotNull Response response) throws IOException {
        InputStream inputStream;
        inputStream = Objects.requireNonNull(response.body()).byteStream();
        if (Objects.equals(response.headers().get("content-encoding"), "gzip")) {
            inputStream = new GZIPInputStream(inputStream);
        }
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(buffer, 0, read);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * 解析网络请求得到的回复
     *
     * @param request  网络请求
     * @param response 回复
     */
    public static Optional<String> handleResponseAndReadString(@NotNull Request request, @NotNull Response response) {
        String result = null;
        if (response.body() != null) {
            try {
                result = new String(readRequestBodyAllBytes(response), StandardCharsets.UTF_8);
            } catch (IOException e) {
                logError(true, request.url(), response.code(), "read response body error", null, e);
                return Optional.empty();
            }
        }
        if (response.code() != 200 || response.body() == null) {
            String message = result;
            if (message == null) {
                message = response.message();
            }
            if (response.body() == null) {
                logError(true, request.url(), response.code(), "response body is empty", message, null);
            } else {
                logError(true, request.url(), response.code(), "response code isn't 200", message, null);
            }
            return Optional.empty();
        }
        return Optional.of(result);
    }

    /**
     * 异步操作，把网络请求加入到队列，受到回复后进行解析，得到字符串
     *
     * @param requestBuilder 网络请求
     * @param callback       回调函数
     */
    public static void enqueueAndReadString(@NotNull Request.Builder requestBuilder, @NotNull Consumer<Optional<String>> callback) {
        if (CLIENT == null) {
            CLIENT = new OkHttpClient.Builder()
                    .addInterceptor(BrotliInterceptor.INSTANCE)
                    .build();
        }
        Request request = requestBuilder
                .headers(new Headers.Builder()
                        .add("accept", "application/json, text/plain, */*")
                        .add("Accept-Language", "zh-CN,zh")
                        .add("Accept-Encoding", "gzip, deflate, br")
                        .add("User-Agent", "CHelper")
                        .build())
                .build();
        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                logError(false, request.url(), null, "network error", e.getMessage(), e);
                callback.accept(Optional.empty());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                Optional<String> result = handleResponseAndReadString(request, response);
                if (isEnableLog && result.isPresent()) {
                    Log.i(TAG, request.url() + "\n" + result.get());
                }
                callback.accept(result);
            }
        });
    }

    /**
     * 异步操作，把网络请求加入到队列，受到回复后进行解析，得到Json解析后的对象
     *
     * @param requestBuilder 网络请求
     * @param callback       回调函数
     */
    public static <T> void enqueueAndReadJson(@NotNull Request.Builder requestBuilder, @NotNull Consumer<Optional<T>> callback, TypeToken<T> typeToken) {
        enqueueAndReadString(requestBuilder, result -> {
            if (result.isPresent()) {
                try {
                    callback.accept(Optional.of(GSON.fromJson(result.get(), typeToken)));
                } catch (JsonSyntaxException e) {
                    logError(false, requestBuilder.getUrl$okhttp(), 200, "fail to parse json", result.get(), e);
                    callback.accept(Optional.empty());
                }
            } else {
                callback.accept(Optional.empty());
            }
        });
    }

    /**
     * 异步操作，把网络请求加入到队列，受到回复后进行解析，得到Json解析后的对象
     *
     * @param requestBuilder 网络请求
     * @param callback       回调函数
     */
    public static <T> void enqueueAndReadJson(@NotNull Request.Builder requestBuilder, @NotNull Consumer<Optional<T>> callback, Class<T> tClass) {
        enqueueAndReadString(requestBuilder, result -> {
            if (result.isPresent()) {
                try {
                    callback.accept(Optional.of(GSON.fromJson(result.get(), tClass)));
                } catch (JsonSyntaxException e) {
                    logError(false, requestBuilder.getUrl$okhttp(), 200, "fail to parse json", result.get(), e);
                    callback.accept(Optional.empty());
                }
            } else {
                callback.accept(Optional.empty());
            }
        });
    }

}
