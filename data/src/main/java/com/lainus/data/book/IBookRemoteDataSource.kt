package com.lainus.data.book

interface IBookRemoteDataSource {
    suspend fun searchByQuery(query: String) : List<Book>
}