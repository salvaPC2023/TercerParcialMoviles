package com.lainus.usecases


import com.lainus.data.BookRepository

class SearchBooks (
    val bookRepository: BookRepository
) {
    suspend fun invoke(toSearch: String): List<Book> {
        return bookRepository.searchByQuery(toSearch)
    }
}