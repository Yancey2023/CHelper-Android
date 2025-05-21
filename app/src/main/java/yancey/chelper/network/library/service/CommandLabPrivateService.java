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

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import yancey.chelper.network.library.data.BaseResult;
import yancey.chelper.network.library.data.LibraryFunction;

@SuppressWarnings("unused")
public interface CommandLabPrivateService {

    class UploadPrivateFunctionRequest {
        public String content;
        public Boolean skip_check;// 可选，跳过审核但需要后续审核流程
    }

    class UploadPrivateFunctionResponse {
        @Nullable
        public Integer id;
        @Nullable
        public Boolean checked;
    }

    @POST("private/functions")
    Call<BaseResult<UploadPrivateFunctionResponse>> uploadPrivateFunction(
            @Body UploadPrivateFunctionRequest request
    );

    class GetPrivateFunctionsResponse {

        @Nullable
        public List<LibraryFunction> functions;
        @Nullable
        public Pagination pagination;

        public static class Pagination {
            @Nullable
            public Integer current_page;// 当前页码
            @Nullable
            public Integer per_page;// 每页数量
            @Nullable
            public Integer total_pages;// 总页数
            @Nullable
            public Integer total_count;// 总记录数
        }

    }

    @GET("private/functions")
    Call<BaseResult<GetPrivateFunctionsResponse>> getPrivateFunctions();

    @GET("private/functions/{id}")
    Call<BaseResult<LibraryFunction>> getPrivateFunction(
            @Path("id") int id
    );

    class UpdatePrivateFunctionRequest {
        public String content;
    }

    @PUT("private/functions/{id}")
    Call<BaseResult<UploadPrivateFunctionResponse>> updatePrivateFunction(
            @Path("id") int id,
            @Body UpdatePrivateFunctionRequest request
    );

    @DELETE("private/functions/{id}")
    Call<BaseResult<Void>> deletePrivateFunction(
            @Path("id") int id
    );

}
