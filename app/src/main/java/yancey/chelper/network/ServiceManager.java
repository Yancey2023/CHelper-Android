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

package yancey.chelper.network;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.brotli.BrotliInterceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import yancey.chelper.android.common.util.MonitorUtil;
import yancey.chelper.network.library.interceptor.AuthInterceptor;
import yancey.chelper.network.library.service.CommandLabPrivateService;
import yancey.chelper.network.library.service.CommandLabPublicService;
import yancey.chelper.network.library.service.CommandLabUserService;

public class ServiceManager {

    public static Gson GSON;
    public static OkHttpClient CLIENT;
    public static Retrofit COMMAND_LAB_RETROFIT;
    public static CommandLabPublicService COMMAND_LAB_PUBLIC_SERVICE;
    public static CommandLabPrivateService COMMAND_LAB_PRIVATE_SERVICE;
    public static CommandLabUserService COMMAND_LAB_USER_SERVICE;

    private ServiceManager() {

    }

    public static void init() {
        GSON = new Gson();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(BrotliInterceptor.INSTANCE)
                .addInterceptor(AuthInterceptor.INSTANCE)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        MonitorUtil.monitHttp(builder);
        CLIENT = builder.build();
        COMMAND_LAB_RETROFIT = new Retrofit.Builder()
                .baseUrl("https://abyssous.site:443/")
                .client(CLIENT)
                .addConverterFactory(GsonConverterFactory.create(GSON))
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
        COMMAND_LAB_PUBLIC_SERVICE = COMMAND_LAB_RETROFIT.create(CommandLabPublicService.class);
        COMMAND_LAB_PRIVATE_SERVICE = COMMAND_LAB_RETROFIT.create(CommandLabPrivateService.class);
        COMMAND_LAB_USER_SERVICE = COMMAND_LAB_RETROFIT.create(CommandLabUserService.class);
    }

}
