package ru.lextop.steamcalculator.binding

import android.arch.lifecycle.*
import android.os.Build
import android.support.v4.app.FragmentActivity
import android.text.Html
import android.view.View
import android.widget.AdapterView

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

fun spannedFromHtml(string: String) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(string, Html.FROM_HTML_MODE_COMPACT)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(string)
        }