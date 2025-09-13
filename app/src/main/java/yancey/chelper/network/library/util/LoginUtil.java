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

package yancey.chelper.network.library.util;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

import retrofit2.Response;
import yancey.chelper.android.common.util.FileUtil;
import yancey.chelper.network.ServiceManager;
import yancey.chelper.network.library.data.BaseResult;
import yancey.chelper.network.library.data.User;
import yancey.chelper.network.library.service.CommandLabUserService;

public class LoginUtil {

    private static File file;
    public static User user;

    public static void init(File file, Consumer<Throwable> onError) {
        LoginUtil.file = file;
        if (file.exists()) {
            try {
                user = ServiceManager.GSON.fromJson(FileUtil.readString(file), User.class);
            } catch (Throwable throwable) {
                onError.accept(throwable);
            }
        }
    }

    /**
     * 获取jwt令牌
     */
    public static String getToken() throws IOException {
        if (user == null) {
            return null;
        }
        if (user.token != null && user.lastLoginTimestamp != null && System.currentTimeMillis() - user.lastLoginTimestamp < 60000) {
            return user.token;
        }
        CommandLabUserService.LoginRequest request = new CommandLabUserService.LoginRequest();
        request.account = user.phoneNumber;
        request.password = user.password;
        Response<BaseResult<CommandLabUserService.LoginResponse>> response = ServiceManager.COMMAND_LAB_USER_SERVICE.login(request).execute();
        if (response.body() != null && Objects.equals(response.body().status, "success")) {
            user.lastLoginTimestamp = System.currentTimeMillis();
            FileUtil.writeString(file, ServiceManager.GSON.toJson(user));
            return user.token;
        } else {
            return null;
        }
    }

}
