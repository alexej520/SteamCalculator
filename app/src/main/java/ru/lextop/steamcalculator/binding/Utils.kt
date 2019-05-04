@file:Suppress("NOTHING_TO_INLINE")

package ru.lextop.steamcalculator.binding

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.*

inline fun <reified VM : ViewModel> FragmentActivity.viewModel(factory: ViewModelProvider.Factory) =
    ViewModelProviders.of(this, factory).get(VM::class.java)

inline fun <reified VM : ViewModel> FragmentActivity.viewModel(key: String) =
    ViewModelProviders.of(this).get(key, VM::class.java)

inline fun <T> LiveData<T>.observe(lo: LifecycleOwner, crossinline onReceive: (T) -> Unit) {
    this.observe(lo, Observer { onReceive(it) })
}

fun <T> MutableLiveData<T>.setValueIfNotSame(value: T?): Boolean =
    if (this.value == value) {
        false
    } else {
        this.value = value
        true
    }

fun <T : Any> nullIfNotInitialized(lateInitVar: () -> T): T? = try {
    lateInitVar()
} catch (e: UninitializedPropertyAccessException) {
    null
}

class OnItemSelectedListener(private val onItemSelected: (id: Int) -> Unit) :
    AdapterView.OnItemSelectedListener {
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, id: Long) {
        onItemSelected(id.toInt())
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}

fun Context.getSpanned(resId: Int): Spanned {
    val string = getString(resId)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(string)
    }
}

fun Context.getSpanned(resId: Int, vararg formatArgs: Any): Spanned {
    val string = getString(resId, *formatArgs)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(string)
    }
}

inline fun Boolean.toVisibleOrGone() = if (this) View.VISIBLE else View.GONE

var View.startPadding: Int
    inline get() = paddingStart
    set(value) = setPaddingRelative(value, paddingTop, paddingEnd, paddingBottom)

var View.endPadding: Int
    inline get() = paddingStart
    set(value) = setPaddingRelative(paddingStart, paddingTop, value, paddingBottom)

fun ViewGroup.inflate(@LayoutRes resId: Int, attach: Boolean = false): View {
    val layoutInflater = LayoutInflater.from(context)
    return layoutInflater.inflate(resId, this, attach)
}

fun <T : ViewDataBinding> ViewGroup.inflate(
    bindingFactory: (LayoutInflater, ViewGroup, Boolean) -> T,
    attach: Boolean = false
): T {
    val layoutInflater = LayoutInflater.from(context)
    val binding = bindingFactory.invoke(layoutInflater, this, attach)
    return binding as T
}

fun Context.browse(url: String, newTask: Boolean = false): Boolean {
    try {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
        return true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        return false
    }
}

fun Context.email(email: String, subject: String = "", text: String = ""): Boolean {
    val intent = Intent(Intent.ACTION_SENDTO)
    intent.data = Uri.parse("mailto:")
    intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    if (subject.isNotEmpty())
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    if (text.isNotEmpty())
        intent.putExtra(Intent.EXTRA_TEXT, text)
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
        return true
    }
    return false

}