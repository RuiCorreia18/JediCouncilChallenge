package com.example.jedicouncilchallenge.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.jedicouncilchallenge.data.local.FavouritesLocalDataSource
import com.example.jedicouncilchallenge.data.local.PreferencesFavouritesDataSource
import com.example.jedicouncilchallenge.data.local.PreferencesThemeDataSource
import com.example.jedicouncilchallenge.data.local.ThemePreferences
import com.example.jedicouncilchallenge.data.remote.datasource.RemoteDataSource
import com.example.jedicouncilchallenge.data.remote.datasource.RetrofitRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "star_wars_prefs")

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindRemoteDataSource(impl: RetrofitRemoteDataSource): RemoteDataSource

    @Binds
    @Singleton
    abstract fun bindFavouritesLocalDataSource(impl: PreferencesFavouritesDataSource): FavouritesLocalDataSource

    @Binds
    @Singleton
    abstract fun bindThemePreferences(impl: PreferencesThemeDataSource): ThemePreferences

    companion object {
        @Provides
        @Singleton
        fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
            context.dataStore
    }
}
