package com.kucingselfie.jetpackdipractice.api

import com.google.gson.annotations.SerializedName
import com.kucingselfie.jetpackdipractice.vo.Repo

data class RepoSearchResponse(
    @SerializedName("total_count")
    val total: Int = 0,
    @SerializedName("items")
    val items: List<Repo>
) {
    var nextPage: Int? = null
}