package ru.lextop.steamcalculator.ui

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ImageSpan
import android.widget.TextView
import org.jetbrains.anko.append
import org.jetbrains.anko.dip
import org.jetbrains.anko.singleLine

class TickerView(context: Context) : TextView(context) {
    var appearanceWidth = dip(8)
    var disappearanceWidth = dip(8)

    private var shapeDrawable = ShapeDrawable().apply {
        this.paint.apply {
            color = Color.WHITE
            xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
        }
        shaderFactory = object : ShapeDrawable.ShaderFactory() {
            override fun resize(width: Int, height: Int): Shader {
                val appW = appearanceWidth.toFloat() / width
                val disappW = disappearanceWidth.toFloat() / width
                val transparent = Color.argb(0x00, 0xFF, 0xFF, 0xFF)
                val nonTransparent = Color.argb(0xFF, 0xFF, 0xFF, 0xFF)
                return LinearGradient(0f, 0f, width.toFloat(), 0f,
                        intArrayOf(transparent, nonTransparent, nonTransparent, transparent),
                        floatArrayOf(0f, appW, 1f - disappW, 1f), Shader.TileMode.CLAMP)
            }
        }
    }

    fun addAppearanceIndent(text: CharSequence): CharSequence =
            SpannableStringBuilder().apply {
                append(" ", ImageSpan(ShapeDrawable().apply {
                    setBounds(0, 0, appearanceWidth, 0)
                }))
                append(text)
            }

    fun removeAppearanceIndent(text: CharSequence) {
        (text as SpannableStringBuilder).delete(0, 1)
    }

    private var bitmapBuffer = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    private val frameBufferObject = Canvas()
    private val bitmapPaint = Paint()

    init {
        singleLine = true
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        isSelected = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        shapeDrawable.setBounds(0, 0, w, h)
        shapeDrawable.paint.shader = shapeDrawable.shaderFactory.resize(w, h)
        if (bitmapBuffer.allocationByteCount < w * h * 4) {
            bitmapBuffer.recycle()
            bitmapBuffer = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            frameBufferObject.setBitmap(bitmapBuffer)
        } else {
            bitmapBuffer.reconfigure(w, h, Bitmap.Config.ARGB_8888)
        }
    }

    override fun onDraw(canvas: Canvas) {
        bitmapBuffer.eraseColor(Color.TRANSPARENT)
        super.onDraw(frameBufferObject)
        shapeDrawable.draw(frameBufferObject)
        canvas.drawBitmap(bitmapBuffer, 0f, 0f, bitmapPaint)
    }
}
