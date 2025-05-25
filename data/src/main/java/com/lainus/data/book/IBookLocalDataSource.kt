package com.lainus.data.book

interface IBookLocalDataSource {
    suspend fun saveBook(book: Book): Boolean
    suspend fun getFavoriteBooks(): List<Book>
}