package com.kucingselfie.jetpackdipractice.api

import androidx.lifecycle.LiveData
import com.kucingselfie.jetpackdipractice.vo.Contributor
import com.kucingselfie.jetpackdipractice.vo.Repo
import com.kucingselfie.jetpackdipractice.vo.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    @GET("users/{login}/repos")
    fun getRepos(@Path("login") login: String): LiveData<ApiResponse<List<Repo>>>

    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String): LiveData<ApiResponse<RepoSearchResponse>>

    @GET("search/repositories")
    fun searchRepos(@Query("q") query: String, @Query("page") page: Int): Call<RepoSearchResponse>

    @GET("repos/{owner}/{name}")
    fun getRepo(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): LiveData<ApiResponse<Repo>>

    @GET("repos/{owner}/{name}/contributors")
    fun getContributors(
        @Path("owner") owner: String,
        @Path("name") name: String
    ): LiveData<ApiResponse<List<Contributor>>>

    @GET("users/{login}")
    fun getUser(@Path("login") login: String): LiveData<ApiResponse<User>>
}