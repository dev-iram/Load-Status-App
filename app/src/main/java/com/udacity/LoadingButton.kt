package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var progressDownload = 0f
    private var progressCircle = 0f
    private lateinit var titleDownload: String
    private val setAnimator = AnimatorSet()
    private val paintButton = Paint(Paint.ANTI_ALIAS_FLAG)

    private var btnColor = context.resources.getColor(R.color.colorPrimary)
    private var btnLoadingColor = context.resources.getColor(R.color.colorPrimaryDark)
    private var circleProgressColor = context.resources.getColor(R.color.colorAccent)

    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 10f
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 50f
    }
    val anim1 = circleProgressAnim()
    val anim2 = horizontalProgressAnim()

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            btnColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            btnLoadingColor = getColor(R.styleable.LoadingButton_loadingColor, 0)
            circleProgressColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }
        resetAnim()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawButtonBackground(canvas)
        val textXPos = (width / 2).toFloat()
        val textYPos = (height / 2 - (textPaint.descent() + textPaint.ascent()) / 2)
        drawButtonProgress(canvas)
        canvas?.apply {
            drawText(titleDownload, textXPos, textYPos, textPaint)
        }
        drawCircleProgress(canvas)
    }


    fun downloadBegin() {
        playAnimTogether()
    }

    fun cancelAnimation() {
        anim1.repeatCount = 0
        anim2.repeatCount = 0
    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true
        playAnimTogether()
        return true
    }

    private fun drawButtonBackground(canvas: Canvas?) {
        paintButton.color = btnColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintButton)
    }

    private fun drawButtonProgress(canvas: Canvas?) {
        paintButton.color = btnLoadingColor
        canvas?.drawRect(0f, 0f, progressDownload, heightSize.toFloat(), paintButton)
        paintButton.reset()
    }

    private fun drawCircleProgress(canvas: Canvas?) {
        paintCircle.color = circleProgressColor
        val rectBounds = Rect()
        textPaint.getTextBounds(titleDownload, 0, titleDownload.length, rectBounds)
        val centerCircle = height / 4f
        val rectF = RectF(0F, heightSize / 2F - centerCircle, 0f, heightSize / 2F + centerCircle)
        rectF.left = widthSize / 2f + rectBounds.right / 2f + 10
        rectF.right = rectF.left + (2 * centerCircle)
        canvas?.drawArc(rectF, 0f, progressCircle, true, paintCircle)
    }


    private fun circleProgressAnim(): ValueAnimator {
        val animator = ValueAnimator.ofFloat(0F, 360F)
        animator.addUpdateListener {
            progressCircle = animator.animatedValue as Float
            invalidate()
        }
        return animator
    }

    private fun horizontalProgressAnim(): ValueAnimator {
        val animator = ValueAnimator.ofFloat(0F, 1000F)
        animator.addUpdateListener {
            progressDownload = animator.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                resetAnim()
            }
        })
        return animator
    }

    private fun resetAnim() {
        paintButton.color = context.resources.getColor(R.color.colorPrimary)
        titleDownload = context.getString(R.string.download_button)
        progressCircle = 0f
        progressDownload = 0f
        invalidate()
    }

    private fun playAnimTogether(durationPlay: Long = 1500L) {
        titleDownload = context.getString(R.string.loading_button)
        setAnimator.apply {
            playTogether(anim1, anim2)
            duration = durationPlay
        }
        setAnimator.addListener(
            object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator, isReverse: Boolean) {
                    anim1.repeatCount = ValueAnimator.INFINITE
                    anim2.repeatCount = ValueAnimator.INFINITE
                    super.onAnimationStart(animation, isReverse)
                }

                override fun onAnimationEnd(animation: Animator) {
                    resetAnim()
                }
            }
        )
        setAnimator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w), heightMeasureSpec, 0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}