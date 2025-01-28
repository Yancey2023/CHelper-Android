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

package yancey.chelper.network.library;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.zip.GZIPInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.brotli.BrotliInterceptor;
import yancey.chelper.network.data.Library;

public class CommandLibraryAPI {

    private static final boolean isEnableLog = false;
    private static OkHttpClient CLIENT;
    private static final String TAG = "CommandLibraryAPI";

    /**
     * 读取网络请求回复的数据
     *
     * @param response 网络请求的回复
     * @return 读取到的数据
     */
    private static byte[] readRequestBodyAllBytes(@NotNull Response response) throws IOException {
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
    private static Optional<String> handleResponseAndReadString(@NotNull Request request, @NotNull Response response) {
        String result = null;
        if (response.body() != null) {
            try {
                result = new String(readRequestBodyAllBytes(response), StandardCharsets.UTF_8);
            } catch (IOException e) {
                if (isEnableLog) {
                    Log.e(TAG, String.format(Locale.getDefault(), "[NetworkException] isSuccess: %s, url: %s, code: %d, reason: read response body error", true, request.url(), response.code()));
                }
                return Optional.empty();
            }
        }
        if (response.code() != 200 || response.body() == null) {
            String message = result;
            if (message == null) {
                message = response.message();
            }
            if (isEnableLog) {
                if (response.body() == null) {
                    Log.e(TAG, String.format(Locale.getDefault(), "[NetworkException] isSuccess: %s, url: %s, code: %d, message: %s, reason: response body is empty", true, request.url(), response.code(), message));
                } else {
                    Log.e(TAG, String.format(Locale.getDefault(), "[NetworkException] isSuccess: %s, url: %s, code: %d, message: %s, reason: response code isn't 200", true, request.url(), response.code(), message));
                }
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
    private static void enqueueAndReadString(@NotNull Request.Builder requestBuilder, @NotNull Consumer<Optional<String>> callback) {
        if (CLIENT == null) {
            CLIENT = new OkHttpClient.Builder()
                    .addInterceptor(BrotliInterceptor.INSTANCE)
                    .build();
        }
        Request request = requestBuilder.headers(new Headers.Builder().add("User-Agent", "CHelper").build()).build();
        CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (isEnableLog) {
                    Log.e(TAG, String.format(Locale.getDefault(), "[NetworkException] isSuccess: %s, url: %s, message: %s", false, request.url(), e.getMessage()));
                }
                callback.accept(Optional.empty());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                callback.accept(handleResponseAndReadString(request, response));
            }
        });
    }

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
    public static void upload(Library library, Consumer<String> callback) {
        throw new RuntimeException("this method is not open source");
    }

}
