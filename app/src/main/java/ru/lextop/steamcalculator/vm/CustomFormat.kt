package ru.lextop.steamcalculator.vm

import android.icu.text.DecimalFormat
import android.icu.util.ULocale
import android.os.Build
import java.text.Format
import java.text.ParseException
import java.util.*

object CustomFormat {
    private val format1: Format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        (DecimalFormat.getInstance(ULocale.US) as DecimalFormat).apply {
            applyLocalizedPattern("@####")
        }
    } else {
        (java.text.DecimalFormat.getInstance(Locale.US) as java.text.DecimalFormat).apply {
            applyLocalizedPattern("@####")
        }
    }

    private val format2: Format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        (DecimalFormat.getInstance(ULocale.US) as DecimalFormat).apply {
            applyLocalizedPattern("0.####E0")
        }
    } else {
        (java.text.DecimalFormat.getInstance(Locale.US) as java.text.DecimalFormat).apply {
            applyLocalizedPattern("0.####E0")
        }
    }

    fun formatIgnoreNaN(double: Double): String =
            when {
                double.isNaN() -> ""
                double < 1e5 && double > 1e-2 -> format1.format(double)
                else -> format2.format(double)
            }

    fun format(double: Double): String =
            when {
                double < 1e5 && double > 1e-2 -> format1.format(double)
                else -> format2.format(double)
            }

    fun parse(string: String): Double = try {
        (format1.parseObject(string) as Number).toDouble()
    } catch (e: ParseException) {
        Double.NaN
    }
}