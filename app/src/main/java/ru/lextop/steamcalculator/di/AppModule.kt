package ru.lextop.steamcalculator.di

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import ru.lextop.steamcalculator.db.AppDatabase
import ru.lextop.steamcalculator.db.OnDbCreateCallback
import ru.lextop.steamcalculator.db.SteamDao
import javax.inject.Singleton

@Module(includes = arrayOf(
        ViewModelModule::class
))
abstract class AppModule {
    @Module
    companion object {
        private lateinit var steamDao: SteamDao

        @Provides
        @Singleton
        @JvmStatic
        fun provideDatabase(app: Application): AppDatabase = Room
                .databaseBuilder(app, AppDatabase::class.java, "app-db")
                .allowMainThreadQueries()
                .addCallback(OnDbCreateCallback)
                .build()

        @Provides
        @Singleton
        @JvmStatic
        fun provideSharedPreferences(app: Application): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

        @Provides
        @Singleton
        @JvmStatic
        fun provideApplicationContext(app: Application): Context = app.applicationContext

        @Provides
        @Singleton
        @JvmStatic
        fun provideSteamDao(db: AppDatabase): SteamDao {
            val steamDao = db.steamDao()
            this.steamDao = steamDao
            return steamDao
        }
    }


}