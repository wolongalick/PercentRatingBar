package com.wolongalick.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.wolongalick.annotation.RatingStep
import com.wolongalick.widget.prb.R
import kotlin.math.roundToInt


/**
 * @createTime 2020/11/9 10:19
 * @author 崔兴旺
 * @description
 */
class PercentRatingBar : View {

    companion object {
        const val TAG: String = "PercentRatingBar"

        const val RATING_STEP_FULL = 0
        const val RATING_STEP_HALF = 1
        const val RATING_STEP_EXACTLY = 2
    }

    private val defaultRatingMaxCount = 5                   //默认星星最大个数为5星
    private var starImgWidth = 0                            //星星宽度
    private var starImgHeight = 0                           //星星高度
    private var mContext: Context                           //上下文
    private lateinit var staredBitmap: Bitmap               //选中的星星bitmap
    private lateinit var notStarBitmap: Bitmap              //未选中的星星bitmap

    private var ratingMaxCount: Int = defaultRatingMaxCount //最大星星个数
    private var ratingSelectedCount: Float = 0f             //选中的星星个数(支持小数)
    private var mRatingStep: Int = 1
    private var mStaredImageRes = 0                          //选中的星星图片资源id
    private var mNotStarImageRes = 0                         //未选中的星星图片资源id
    private var mRatingPadding = 0                           //星星之间的间距,单位px
    private var mRatingIsSupportDrag = true                  //是否支持拖动

    var onRatingChangeListener = { ratingValue: Float -> Unit }

    constructor(context: Context) : super(context) {
        mContext = context
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.PercentRatingBar)
        mStaredImageRes = array.getResourceId(R.styleable.PercentRatingBar_ratingStaredImg, R.drawable.selected_star)
        mNotStarImageRes = array.getResourceId(R.styleable.PercentRatingBar_ratingNotStarImg, R.drawable.not_select_star)
        ratingMaxCount = array.getInteger(R.styleable.PercentRatingBar_ratingMaxCount, defaultRatingMaxCount)
        ratingSelectedCount = array.getFloat(R.styleable.PercentRatingBar_ratingSelectedCount, 0f)
        mRatingPadding = array.getDimensionPixelSize(R.styleable.PercentRatingBar_ratingPadding, 0)
        mRatingStep = array.getInteger(R.styleable.PercentRatingBar_ratingStep, RATING_STEP_FULL)
        mRatingIsSupportDrag = array.getBoolean(R.styleable.PercentRatingBar_ratingIsSupportDrag, true)

        array.recycle()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(!mRatingIsSupportDrag){
            return super.onTouchEvent(event)
        }
        //将星星和间距作为一组控件块
        val chunkWidth = starImgWidth + mRatingPadding

        //计算出包含多少个控件块,也就是占多少颗星,多少分
        var newCount = ((event.x - paddingStart.toFloat()) / chunkWidth)

        //计算出多滑出的百分比(一组控件块的)
        val starPaddingPercent: Float = (newCount - newCount.toInt())

        //计算出多滑出的百分比(一颗星的)
        var starPercent: Float = chunkWidth * starPaddingPercent / starImgWidth

        //将一颗星的百分比强制限制到1也就是100%
        if (starPercent > 1) {
            starPercent = 1f
        }

        newCount = newCount.toInt() + starPercent

        newCount = adjustRatingSelectedCount(newCount)


        if (ratingSelectedCount != newCount) {
            onRatingChangeListener(newCount)
        }
        ratingSelectedCount = newCount
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (ratingSelectedCount > ratingMaxCount) {
            //限制评分,最高只能设置为最大值
            ratingSelectedCount = ratingMaxCount.toFloat()
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        //绘制底部灰色星(未选中的)
        for (i in ratingSelectedCount.toInt() until ratingMaxCount) {
            canvas.drawBitmap(notStarBitmap, paddingStart + i * (starImgWidth.toFloat() + mRatingPadding), paddingTop.toFloat(), paint)
        }

        //绘制黄色星(选中的)
        for (i in 0 until ratingSelectedCount.toInt()) {
            canvas.drawBitmap(staredBitmap, paddingStart + i * (starImgWidth.toFloat() + mRatingPadding), paddingTop.toFloat(), paint)
        }

        //绘制半颗星
        val fractional = ratingSelectedCount - ratingSelectedCount.toInt()
        if (fractional > 0) {
            val left = paddingStart + ratingSelectedCount.toInt() * (starImgWidth.toFloat() + mRatingPadding).toInt()
            val right = left + (starImgWidth * fractional).toInt()
            //裁剪半颗星
            canvas.clipRect(left, paddingTop, right, paddingTop + staredBitmap.height)
            canvas.drawBitmap(staredBitmap, left.toFloat(), paddingTop.toFloat(), paint)
        }
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {

    }

    private fun adjustRatingSelectedCount(ratingSelectedCount: Float): Float {
        var newRatingSelectedCount: Float
        when (mRatingStep) {
            RATING_STEP_FULL -> {
                newRatingSelectedCount = ratingSelectedCount.roundToInt().toFloat()
            }
            RATING_STEP_HALF -> {
                var fractional = ratingSelectedCount - ratingSelectedCount.toInt()
                fractional = if (fractional <= 0.5) {
                    0f
                } else {
                    0.5f
                }
                newRatingSelectedCount = ratingSelectedCount.toInt() + fractional
            }
            RATING_STEP_EXACTLY -> {
                newRatingSelectedCount = ratingSelectedCount
            }
            else -> {
                newRatingSelectedCount = ratingSelectedCount.roundToInt().toFloat()
            }
        }

        if (newRatingSelectedCount < 0) {
            newRatingSelectedCount = 0f
        } else if (newRatingSelectedCount > ratingMaxCount) {
            newRatingSelectedCount = ratingMaxCount.toFloat()
        }

        return newRatingSelectedCount
    }

    @Override
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 获取宽-测量规则的模式和大小
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        // 获取高-测量规则的模式和大小
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        staredBitmap = BitmapFactory.decodeResource(resources, mStaredImageRes)
        notStarBitmap = BitmapFactory.decodeResource(resources, mNotStarImageRes)
        starImgWidth = staredBitmap.width
        starImgHeight = staredBitmap.height

        val mWidth = ratingMaxCount * (starImgWidth.toFloat() + mRatingPadding).toInt() - mRatingPadding + paddingStart + paddingEnd
        val mHeight = starImgHeight + paddingTop + paddingBottom

        // 当布局参数设置为wrap_content时，设置默认值
        if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight)
            // 宽 / 高任意一个布局参数为= wrap_content时，都设置默认值
        } else if (layoutParams.width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize)
        } else if (layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight)
        }
    }


    fun setRatingMaxCount(value: Int) {
        ratingMaxCount = value
        requestLayout()
        invalidate()
    }

    fun setRatingSelectedCount(value: Float) {
        val newValue = adjustRatingSelectedCount(value)
        if (ratingSelectedCount != newValue) {
            onRatingChangeListener(newValue)
        }
        ratingSelectedCount = newValue
        invalidate()
    }

    fun setRatingStep(@RatingStep ratingStep: Int) {
        mRatingStep = ratingStep
        invalidate()
    }

    fun setStaredImageRes(staredImageRes: Int, notStarImageRes: Int) {
        mStaredImageRes = staredImageRes
        mNotStarImageRes = notStarImageRes
        requestLayout()
        invalidate()
    }

    fun setRatingPadding(ratingPadding: Int) {
        mRatingPadding = ratingPadding
        requestLayout()
        invalidate()
    }

    fun getRatingMaxCount(): Int {
        return ratingMaxCount
    }

    fun getRatingSelectedCount(): Float {
        return ratingSelectedCount
    }

}