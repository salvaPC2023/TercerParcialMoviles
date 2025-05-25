package com.salvador.framework.service


import com.salvador.framework.dto.BookSearchResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface IApiService {

    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String
    ): BookSearchResponseDto
}
