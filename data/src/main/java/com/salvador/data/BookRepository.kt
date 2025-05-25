package com.salvador.data

import com.salvador.data.book.IBookLocalDataSource
import com.salvador.data.book.IBookRemoteDataSource

class BookRepository (
    val bookRemoteDataSource: IBookRemoteDataSource,
    val bookLocalDataSource: IBookLocalDataSource
) {
    suspend fun searchByQuery(query: String): List<Book> {
        return bookRemoteDataSource.searchByQuery(query)
    }

    suspend fun saveBook(book: Book): Boolean {
        bookLocalDataSource.saveBook(book)
        return true
    }

    suspend fun getFavoriteBooks(): List<Book> {
        return bookLocalDataSource.getFavoriteBooks()
    }
}