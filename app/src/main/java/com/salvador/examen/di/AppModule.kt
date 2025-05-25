package com.salvador.examen.di

import android.content.Context
import com.salvador.data.BookRepository
import com.salvador.data.book.IBookLocalDataSource
import com.salvador.data.book.IBookRemoteDataSource
import com.salvador.framework.service.RetrofitClient
import com.salvador.framework.book.BookRemoteDataSource
import com.salvador.framework.book.BookLocalDataSource
import com.salvador.usecases.SearchBooks

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ppModule {


    @Provides
    @Singleton
    fun provideRetrofitClient(): RetrofitClient = RetrofitClient

    @Provides
    @Singleton
    fun provideBookRemoteDataSource(retrofitClient: RetrofitClient): IBookRemoteDataSource {
        return BookRemoteDataSource(retrofitClient)
    }

    @Provides
    @Singleton
    fun provideBookRepository(remoteDataSource: IBookRemoteDataSource, localDataSource: IBookLocalDataSource): BookRepository {
        return BookRepository(remoteDataSource, localDataSource)
    }

    @Provides
    @Singleton
    fun provideSearchBooks(repository: BookRepository): SearchBooks {
        return SearchBooks(repository)
    }

    @Provides
    @Singleton
    fun provideBookLocalDataSource(@ApplicationContext context: Context) : IBookLocalDataSource {
        return BookLocalDataSource(context)
    }

    @Provides
    @Singleton
    fun provideAddToMyFavorites(repository: BookRepository): AddFavorites {
        return AddFavorites(repository)
    }

    @Provides
    @Singleton
    fun provideGetMyFavoriteBooks(repository: BookRepository): GetFavoriteBooks {
        return GetFavoriteBooks(repository)
    }

}
