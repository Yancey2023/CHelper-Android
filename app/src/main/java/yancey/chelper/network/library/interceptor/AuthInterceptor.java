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

package yancey.chelper.network.library.interceptor;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;
import yancey.chelper.network.library.util.LoginUtil;

public class AuthInterceptor implements Interceptor {

    public static final AuthInterceptor INSTANCE = new AuthInterceptor();

    private AuthInterceptor() {

    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        if (chain.request().url().host().equals("abyssous.site")) {
            String token = LoginUtil.getToken();
            if (token != null && !token.isEmpty()) {
                return chain.proceed(chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build());
            }
        }
        return chain.proceed(chain.request());
    }

}
