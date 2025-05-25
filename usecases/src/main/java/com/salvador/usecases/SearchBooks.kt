package com.salvador.usecases


import com.salvador.data.BookRepository

class SearchBooks (
    val bookRepository: BookRepository
) {
    suspend fun invoke(toSearch: String): List<Book> {
        return bookRepository.searchByQuery(toSearch)
    }
}