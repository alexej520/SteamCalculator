package ru.lextop.steamcalculator.binding

import android.arch.lifecycle.*
import android.content.Context
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.text.Html
import android.text.Spanned
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import org.jetbrains.anko.custom.ankoView
import ru.lextop.steamcalculator.R

inline fun <reified VM : ViewModel> FragmentActivity.viewModel(factory: ViewModelProvider.Factory) =
        ViewModelProviders.of(this, factory).get(VM::class.java)

inline fun <reified VM : ViewModel> FragmentActivity.viewModel(key: String) =
        ViewModelProviders.of(this).get(key, VM::class.java)

inline fun <T> LiveData<T>.observe(lo: LifecycleOwner, crossinline onReceive: (T?) -> Unit) {
    this.observe(lo, android.arch.lifecycle.Observer { onReceive(it) })
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

class OnItemSelectedListener(private val onItemSelected: (id: Int) -> Unit)
    : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, id: Long) {
        onItemSelected(id.toInt())
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}
}

fun <VM : Any> View.getBinding(): Binding<VM> = Binding.getForView(this)

fun Context.getSpanned(resId: Int): Spanned {
    val string = getString(resId)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(string)
    }
}

inline fun Boolean.toVisibleOrGone() = if (this) View.VISIBLE else View.GONE

inline fun ViewManager.textSubhead(init: TextView.() -> Unit): TextView {
    return ankoView({
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TextView(it, null, 0, R.style.TextBody1)
        } else {
            TextView(ContextThemeWrapper(it, R.style.TextBody1))
        }
    }, 0, init)
}

inline fun ViewManager.textCaption(init: TextView.() -> Unit): TextView =
        ankoView({ TextView(it, null, 0, R.style.TextCaption) }, 0, init)

inline fun ViewManager.textBody1(init: TextView.() -> Unit): TextView =
        ankoView({ TextView(it, null, 0, R.style.TextBody1) }, 0, init)
inline fun ViewManager.textBody1() = textBody1 {  }

inline fun ViewManager.textBody2(init: TextView.() -> Unit): TextView =
        ankoView({ TextView(it, null, 0, R.style.TextBody2) }, 0, init)

inline fun ViewManager.editTextCompat(init: EditText.() -> Unit): EditText =
        ankoView({ EditText(it, null, 0, R.style.EditText) }, 0, init)

var View.startPadding: Int
    inline get() = paddingStart
    set(value) = setPaddingRelative(value, paddingTop, paddingEnd, paddingBottom)

var View.endPadding: Int
    inline get() = paddingStart
    set(value) = setPaddingRelative(paddingStart, paddingTop, value, paddingBottom)