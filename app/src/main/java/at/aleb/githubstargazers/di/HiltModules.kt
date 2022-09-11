package at.aleb.githubstargazers.di

import at.aleb.githubstargazers.data.network.GitHubService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@InstallIn(SingletonComponent::class)
@Module
object Providers {

    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient()

    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun provideGitHubService(retrofit: Retrofit): GitHubService =
        retrofit.create(GitHubService::class.java)

    @Provides
    @QualifyDispatcher.Default
    fun providesDefaultDispatcher() = Dispatchers.Default

    @Provides
    @QualifyDispatcher.Main
    fun providesMainDispatcher() = Dispatchers.Main

    @Provides
    @QualifyDispatcher.IO
    fun providesIODispatcher() = Dispatchers.IO
}