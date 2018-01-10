package ru.lextop.steamcalculator.ui

import android.animation.LayoutTransition
import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.widget.LinearLayout
import org.jetbrains.anko.*
import ru.lextop.steamcalculator.R
import ru.lextop.steamcalculator.binding.borderlessButton
import ru.lextop.steamcalculator.binding.startOf

class RateView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {
    var onRatedListener: ((success: Boolean, positive: Boolean) -> Unit)? = null
    private var positive = true
    private var success = true
    private var nextState: Int = 0
    private val transitionListener = object : LayoutTransition.TransitionListener {
        override fun startTransition(transition: LayoutTransition, container: ViewGroup, target: View, transitionType: Int) {}
        override fun endTransition(transition: LayoutTransition, container: ViewGroup, target: View, transitionType: Int) {
            if (transitionType == LayoutTransition.DISAPPEARING) {
                setContent(nextState)
            }
        }
    }

    init {
        // by default LinearLayout has transparent background
        backgroundColor = TypedValue().also { context.theme.resolveAttribute(android.R.attr.windowBackground, it, true) }.data
        layoutTransition = LayoutTransition().apply {
            R.style.Widget_Design_Snackbar
            addTransitionListener(transitionListener)
        }
        imageView(R.mipmap.ic_launcher_round) {
            setPadding(dip(8), dip(8), 0, dip(8))
        }
        setContent(ASK_LIKE)
    }

    companion object {
        private const val COMPLETED = -1
        private const val ASK_LIKE = 0
        private const val ASK_GP_FEEDBACK = 1
        private const val ASK_EMAIL_FEEDBACK = 2
    }

    private fun setState(state: Int) {
        nextState = state
        this.removeViewAt(1)
    }

    private fun ViewManager.yesNo(string: String, yes: (View) -> Unit, no: (View) -> Unit) {
        relativeLayout {
            textView(string) {
                singleLine = true
                ellipsize = TextUtils.TruncateAt.MARQUEE
                marqueeRepeatLimit = -1
                textAppearance = R.style.TextAppearance_AppCompat_Body2
                isSelected = true
            }.lparams(matchParent, matchParent).lparams {
                alignParentStart()
                baselineOf(R.id.rateViewYesButton)
                startOf(R.id.rateViewNoButton)
            }
            borderlessButton(R.string.rateViewNo) {
                id = R.id.rateViewNoButton
                setOnClickListener(no)
            }.lparams {
                startOf(R.id.rateViewYesButton)
                baselineOf(R.id.rateViewYesButton)
            }
            borderlessButton(R.string.rateViewYes) {
                id = R.id.rateViewYesButton
                setOnClickListener(yes)
            }.lparams {
                alignParentEnd()
            }
        }
    }

    private fun setContent(state: Int) {
        when (state) {
            ASK_LIKE -> yesNo(context.getString(R.string.rateViewAskLike, context.getString(R.string.app_name)),
                    yes = { positive = true; setState(ASK_GP_FEEDBACK) },
                    no = { positive = false; setState(ASK_EMAIL_FEEDBACK) })
            ASK_GP_FEEDBACK -> yesNo(context.getString(R.string.rateViewAskGPFeedback),
                    yes = { success = true; setState(COMPLETED) },
                    no = { success = false; setState(COMPLETED) })
            ASK_EMAIL_FEEDBACK -> yesNo(context.getString(R.string.rateViewAskEmailFeedback),
                    yes = { success = true; setState(COMPLETED) },
                    no = { success = false; setState(COMPLETED) })
            COMPLETED -> onRatedListener?.invoke(success, positive)
        }
    }
}