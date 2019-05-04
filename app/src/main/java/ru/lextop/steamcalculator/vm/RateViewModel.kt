package ru.lextop.steamcalculator.vm

import android.content.Context
import android.preference.PreferenceManager
import ru.lextop.steamcalculator.R
import java.util.*

object RateViewModel {
    private val firstReleaseWithRateDialogTime = 1515527018000
    fun mustRate(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val launchCounter =
            prefs.getInt(context.getString(R.string.preferenceKeyAppLaunchCounter), 0)
        val lastRateRequestTime =
            prefs.getLong(context.getString(R.string.preferenceKeyRateLastRequestTime), 0L)
        val lastRateRequestLaunch =
            prefs.getInt(context.getString(R.string.preferenceKeyRateLastRequestLaunch), 0)
        val completed =
            prefs.getBoolean(context.getString(R.string.preferenceKeyRateCompleted), false)
        val success = prefs.getBoolean(context.getString(R.string.preferenceKeyRateSuccess), false)
        val positive =
            prefs.getBoolean(context.getString(R.string.preferenceKeyRatePositive), false)
        val now = Date().time
        val firstInstallTime =
            context.packageManager.getPackageInfo(context.packageName, 0).firstInstallTime
        return if (lastRateRequestLaunch == 0) {
            (now - firstInstallTime >= 48 * 60 * 60 * 1000 /* 48 Hours */)
                    &&
                    (firstReleaseWithRateDialogTime >= firstInstallTime
                            || launchCounter >= 3)
        } else if (completed) {
            (!success && positive
                    && now - lastRateRequestTime >= 15L * 24 * 60 * 60 * 1000  // 15 days
                    && launchCounter - lastRateRequestLaunch >= 5)
                    ||
                    (!success && !positive
                            && now - lastRateRequestTime >= 30L * 24 * 60 * 60 * 1000  // 30 days
                            && launchCounter - lastRateRequestLaunch >= 5)
                    ||
                    (success && !positive
                            && now - lastRateRequestTime >= 45L * 24 * 60 * 60 * 1000  // 45 days
                            && launchCounter - lastRateRequestLaunch >= 5)
                    ||
                    (success && positive
                            && now - lastRateRequestTime >= 180L * 24 * 60 * 60 * 1000  // 180 days
                            && launchCounter - lastRateRequestLaunch >= 5)
        } else {
            (launchCounter == lastRateRequestLaunch)  // same session
                    ||
                    (now - lastRateRequestTime >= 3 * 24 * 60 * 60 * 1000  // 3 days
                            && launchCounter - lastRateRequestLaunch >= 5)
        }
    }

    fun onRateDialogStarted(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val now = Date().time
        val launchCounter =
            prefs.getInt(context.getString(R.string.preferenceKeyAppLaunchCounter), 0)
        prefs.edit()
            .putLong(context.getString(R.string.preferenceKeyRateLastRequestTime), now)
            .putInt(context.getString(R.string.preferenceKeyRateLastRequestLaunch), launchCounter)
            .putBoolean(context.getString(R.string.preferenceKeyRateCompleted), false)
            .apply()
    }

    fun onRateDialogCompleted(context: Context, success: Boolean, positive: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putBoolean(context.getString(R.string.preferenceKeyRateCompleted), true)
            .putBoolean(context.getString(R.string.preferenceKeyRateSuccess), success)
            .putBoolean(context.getString(R.string.preferenceKeyRatePositive), positive)
            .apply()
    }
}