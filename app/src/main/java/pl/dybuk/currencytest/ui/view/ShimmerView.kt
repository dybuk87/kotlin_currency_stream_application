package pl.dybuk.currencytest.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

internal class ShimmerView : View {

    val paint = Paint()

    constructor(context: Context) : super(context) {
        init(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {

    }


    override fun onDraw(canvas: Canvas) {
        val w = width
        val h = height

        var globalTimePassed = System.currentTimeMillis() - startTime
        globalTimePassed /= 16 // remove lower part of time - all views will have better sync toleration
        var globalProgress = 16f * 1.5f * globalTimePassed.toFloat()
        globalProgress = globalProgress.toInt() % 1000 / 1000.0f

        val gradient = RadialGradient(
            3f * globalProgress * width.toFloat() - width,
            (height / 2).toFloat(),
            (width * 2).toFloat(),
            -0x151516,
            -0x29292a,
            Shader.TileMode.CLAMP
        )

        paint.isDither = true
        paint.shader = gradient

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        postDelayed({ invalidate() }, 16)
    }

    companion object {

        var startTime = System.currentTimeMillis()
    }
}
