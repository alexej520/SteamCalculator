@file:Suppress("NOTHING_TO_INLINE")

package ru.lextop.steamcalculator.binding

import androidx.lifecycle.*
import android.content.Context
import android.os.Build
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.cardview.widget.CardView
import android.text.Html
import android.text.Spanned
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewManager
import android.widget.*
import com.google.android.gms.ads.AdView
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.textAppearance
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.ui.RateView

inline fun <reified VM : ViewModel> FragmentActivity.viewModel(factory: ViewModelProvider.Factory) =
        ViewModelProviders.of(this, factory).get(VM::class.java)

inline fun <reified VM : ViewModel> FragmentActivity.viewModel(key: String) =
        ViewModelProviders.of(this).get(key, VM::class.java)

inline fun <T> LiveData<T>.observe(lo: LifecycleOwner, crossinline onReceive: (T?) -> Unit) {
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

class OnItemSelectedListener(private val onItemSelected: (id: Int) -> Unit)
    : AdapterView.OnItemSelectedListener {
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

inline fun ViewManager.textCaption(init: TextView.() -> Unit): TextView =
        ankoView({ TextView(it).apply { textAppearance = R.style.TextAppearance_AppCompat_Caption } }, 0, init)

inline fun ViewManager.textSubheading(init: TextView.() -> Unit): TextView =
        ankoView({ TextView(it).apply { textAppearance = R.style.TextAppearance_AppCompat_Subhead } }, 0, init)

inline fun ViewManager.textBody1(init: TextView.() -> Unit): TextView =
        ankoView({ TextView(it).apply { textAppearance = R.style.TextAppearance_AppCompat_Body1 } }, 0, init)

inline fun ViewManager.textBody2(init: TextView.() -> Unit): TextView =
        ankoView({ TextView(it).apply { textAppearance = R.style.TextAppearance_AppCompat_Body2 } }, 0, init)

inline fun ViewManager.editTextMaterial(init: EditText.() -> Unit): EditText =
        ankoView({ EditText(it, null, R.attr.editTextStyle) }, 0, init)

inline fun ViewManager.adView(init: AdView.() -> Unit): AdView =
        ankoView({ AdView(it) }, 0, init)

inline fun ViewManager.rateView(init: RateView.() -> Unit): RateView =
        ankoView({ RateView(ContextThemeWrapper(it, R.style.PreferenceFixTheme), null, 0) }, 0, init)

// because Button(context: Context) does not apply the style
inline fun ViewManager.borderlessButton(textRes: Int = 0, init: Button.() -> Unit) =
        ankoView({ Button(ContextThemeWrapper(it, R.style.Widget_AppCompat_Button_Borderless_Colored), null, 0) }, 0) {
            if (textRes != 0) setText(textRes)
            init()
        }

var View.startPadding: Int
    inline get() = paddingStart
    set(value) = setPaddingRelative(value, paddingTop, paddingEnd, paddingBottom)

var View.endPadding: Int
    inline get() = paddingStart
    set(value) = setPaddingRelative(paddingStart, paddingTop, value, paddingBottom)

var CardView.elevationCompat
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        elevation
    } else {
        cardElevation
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            elevation = value
        } else {
            cardElevation = value
        }
    }

inline fun RelativeLayout.LayoutParams.startOf(@IdRes id: Int) = addRule(RelativeLayout.START_OF, id)
inline fun RelativeLayout.LayoutParams.endOf(@IdRes id: Int) = addRule(RelativeLayout.END_OF, id)