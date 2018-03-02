package ru.lextop.steamcalculator.binding

import android.content.Context
import android.databinding.BindingAdapter
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.widget.AdapterView

fun dp(context: Context, size: Float): Int =
    (context.resources.displayMetrics.density * size).toInt()

fun sp(context: Context, size: Float): Int =
    (context.resources.displayMetrics.scaledDensity * size).toInt()

fun getSpanned(context: Context, resId: Int): Spanned {
    val string = context.getString(resId)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(string)
    }
}

@BindingAdapter
