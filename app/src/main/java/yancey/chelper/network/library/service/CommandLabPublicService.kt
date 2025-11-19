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

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import yancey.chelper.network.library.data.BaseResult;
import yancey.chelper.network.library.data.LibraryFunction;

@SuppressWarnings("unused")
public interface CommandLabPublicService {

    class GetFunctionsResponse {

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

    @GET("functions")
    Observable<BaseResult<GetFunctionsResponse>> getFunctions(
            @Query("page") int page,
            @Query("per_page") int per_page,
            @Query("search") String search,
            @Query("author") String author,
            @Query("tags") String tags,
            @Query("sort") String sortType,
            @Query("android_id") String android_id
    );

    @GET("function/{id}")
    Observable<BaseResult<LibraryFunction>> getFunction(
            @Path("id") int id,
            @Query("android_id") String android_id
    );

    @GET("function/key/{user_key}")
    Observable<BaseResult<LibraryFunction>> getFunctionByKey(
            @Path("user_key") String user_key
    );

    class UploadFunctionRequest {
        public String content;
    }

    class UploadFunctionResponse {
        @Nullable
        public Integer id;
        @Nullable
        public String uuid;
        @Nullable
        public List<LibraryFunction> functions;
        @Nullable
        public String backup_file;
    }

    @POST("upload")
    Observable<BaseResult<UploadFunctionResponse>> uploadFunction(
            @Body UploadFunctionRequest request
    );

    class UpdateFunctionRequest {
        public String content;
        public String auth_key;
    }

    @PUT("function/{id}")
    Observable<BaseResult<UploadFunctionResponse>> updateFunction(
            @Path("id") int id,
            @Body UpdateFunctionRequest request
    );

    class DeleteFunctionRequest {
        public String auth_key;
    }

    @DELETE("function/{id}")
    Observable<BaseResult<Void>> deleteFunction(
            @Path("id") int id,
            @Body DeleteFunctionRequest request
    );

    class LikeFunctionRequest {
        public String android_id;
    }

    class LibraryLikeResponse {
        @Nullable
        public String action;// like或unlike
        @Nullable
        public Integer like_count;// 当前点赞总数
    }

    @POST("function/{id}/like")
    Observable<BaseResult<LibraryLikeResponse>> like(
            @Path("id") int id,
            @Body LikeFunctionRequest request
    );

}
