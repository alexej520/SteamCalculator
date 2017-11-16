package ru.lextop.steamcalculator.vm

import android.icu.text.DecimalFormat
import android.icu.util.ULocale
import android.os.Build
import java.text.Format
import java.text.ParseException
import java.util.*

object CustomFormat {
    var scientificFormatOnly: Boolean = false
    private var maxFormat1: Double = 1e5
    var maxSymbols: Int = 5
        set(value) {
            field = value
            maxFormat1 = Math.pow(10.0, value.toDouble())
            val sb = StringBuilder()
            for (i in 1 until value) {
                sb.append('#')
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                (format1 as DecimalFormat).applyLocalizedPattern("@$sb")
                (format2 as DecimalFormat).applyLocalizedPattern("0.${sb}E0")
            } else {
                (format1 as java.text.DecimalFormat).applyLocalizedPattern("@$sb")
                (format2 as java.text.DecimalFormat).applyLocalizedPattern("0.${sb}E0")
            }
        }

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
            if (scientificFormatOnly) {
                format2.format(double)
            } else {
                when {
                    double.isNaN() -> ""
                    double < maxFormat1 && double >= 1e-3 -> format1.format(double)
                    else -> format2.format(double)
                }
            }

    fun format(double: Double): String =
            if (scientificFormatOnly) {
                format2.format(double)
            } else {
                when {
                    double < maxFormat1 && double >= 1e-3 -> format1.format(double)
                    else -> format2.format(double)
                }
            }

    fun parse(string: String): Double = try {
        (format1.parseObject(string) as Number).toDouble()
    } catch (e: ParseException) {
        Double.NaN
    }
}