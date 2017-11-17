package ru.lextop.steamcalculator

import android.app.Activity
import android.app.Application
import android.support.v7.preference.PreferenceManager
import com.google.android.gms.ads.MobileAds
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import ru.lextop.steamcalculator.di.AppInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector{
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713")
        AppInjector.init(this)
    }
}