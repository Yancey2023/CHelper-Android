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

package yancey.chelper.network.library.service;

import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import yancey.chelper.network.library.data.BaseResult;

@SuppressWarnings("unused")
public interface CommandLabUserService {

    class SendVerifyCodeRequest {
        public String special_code;
        public Integer type;
        public String email;
        public String phone;
        public String lang;
    }

    @POST("register/sendCode")
    Call<BaseResult<Void>> sendVerifyCode(
            @Body SendVerifyCodeRequest request
    );

    class RegisterRequest {
        public String email;
    }

    class RegisterResponse {
        @Nullable
        public String message;
    }

    @POST("user/register")
    Call<BaseResult<RegisterResponse>> register(
            @Body RegisterRequest request
    );

    class VerifyRegistrationRequest {
        public String email;
        public String code;
        public String password;
        public String android_id;
    }

    class VerifyRegistrationResponse {
        @Nullable
        public Integer user_id;
        @Nullable
        public String token;
    }

    @POST("user/verify")
    Call<BaseResult<VerifyRegistrationResponse>> verifyRegistration(
            @Body VerifyRegistrationRequest request
    );

    class LoginRequest {
        public String account;
        public String password;
    }

    class User {
        @Nullable
        Integer id;
        @Nullable
        public String email;
        @Nullable
        public String nickname;
        @Nullable
        public Boolean is_admin;
        @Nullable
        public Boolean is_moderator;
    }

    class LoginResponse {
        @Nullable
        public Integer user_id;
        @Nullable
        public String token;
        @Nullable
        public User user;
    }

    @POST("user/login")
    Call<BaseResult<LoginResponse>> login(
            @Body LoginRequest request
    );

    @GET("web/user_info")
    Call<BaseResult<User>> getUserInfo();

    class CheckLoginResponse {
        @Nullable
        public Boolean logged_in;
        @Nullable
        public User user;
    }

    @GET("web/check_login")
    Call<BaseResult<CheckLoginResponse>> checkLogin();

    class SendResetCodeRequest {
        public String email;
    }

    class SendResetCodeResponse {
        @Nullable
        public String message;
    }

    @POST("user/send_reset_code")
    Call<BaseResult<SendResetCodeResponse>> sendResetCode(
            @Body SendResetCodeRequest request
    );

    class ResetPasswordRequest {
        public String email;
        public String code;
        public String new_password;
    }

    class ResetPasswordResponse {
        @Nullable
        public String message;
    }

    @POST("user/reset_password")
    Call<BaseResult<ResetPasswordResponse>> resetPassword(
            @Body ResetPasswordRequest request
    );

    class UpdateSettingRequest {
        public String nickname;
        public String old_password;
        public String new_password;
    }

    class UpdateSettingResponse {
        @Nullable
        public String message;
    }

    @POST("web/update_settings")
    Call<BaseResult<UpdateSettingResponse>> updateSetting(
            @Body UpdateSettingRequest request
    );

    class LogoutResponse {
        @Nullable
        public String message;
    }

    @POST("user/logout")
    Call<BaseResult<LogoutResponse>> logout();

    class VerifySensitiveRequest {
        public String operation;
        public String code;
    }

    class VerifySensitiveResponse {
        @Nullable
        public Boolean verified;
    }

    @POST("user/verify_sensitive")
    Call<BaseResult<VerifySensitiveResponse>> verifySensitive(
            @Body VerifySensitiveRequest request
    );

}
