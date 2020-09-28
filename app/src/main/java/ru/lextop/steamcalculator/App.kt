package ru.lextop.steamcalculator

import android.app.Application
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.MobileAds
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import ru.lextop.steamcalculator.di.AppInjector
import java.util.*
import javax.inject.Inject

class App : Application(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        updatePreferences()
        MobileAds.initialize(this, getString(R.string.adAppId))
        AppInjector.init(this)
    }

    private fun updatePreferences() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val launchCounter = prefs.getInt(getString(R.string.preferenceKeyAppLaunchCounter), 0) + 1
        val launchTime = Date().time
        prefs.edit()
            .putInt(getString(R.string.preferenceKeyAppLaunchCounter), launchCounter)
            .putLong(getString(R.string.preferenceKeyAppLastLaunchTime), launchTime)
            .apply()
    }
}