package com.example.githubreposapp.model

import io.reactivex.Single
import retrofit2.http.*

interface GithubApi {
    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("https://github.com/login/oauth/access_token")
    fun getAuthToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Single<GithubToken> // Single - observer that emits one value then finishes

    @GET("users/repos")
    fun getRepos(): Single<List<GithubRepo>>

    @GET("repos/{owner}/{repo}/pulls")
    fun getPullRequests(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Single<List<GithubPR>>
}