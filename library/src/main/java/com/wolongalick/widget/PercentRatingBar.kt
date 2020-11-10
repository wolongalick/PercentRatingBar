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

        const val RATING_STEP_FULL = 0                          //整颗星
        const val RATING_STEP_HALF = 1                          //半颗星
        const val RATING_STEP_EXACTLY = 2                       //精确到具体刻度比例
    }

    private val defaultRatingTotalCount = 5                     //默认星星总个数为5星
    private var mStarImgWidth = 0                               //星星宽度
    private var mStarImgHeight = 0                              //星星高度
    private var mContext: Context                               //上下文
    private lateinit var staredBitmap: Bitmap                   //选中的星星bitmap
    private lateinit var notStarBitmap: Bitmap                  //未选中的星星bitmap

    private var mTotalScore: Int = defaultRatingTotalCount      //总分数
    private var mSelectedCount: Float = 0f                      //评分(支持小数)
    private var mStep: Int = 1                                  //步长(渲染整颗星、半颗星、根据具体滑动比例)
    private var mSelectedImg = 0                                //选中的星星图片资源id
    private var mNotSelectImg = 0                               //未选中的星星图片资源id
    private var mRatingPadding = 0                              //星星之间的间距,单位px
    private var mIsSupportDrag = true                           //是否支持拖动

    var onRatingChangeListener = { ratingValue: Float -> Unit }

    constructor(context: Context) : super(context) {
        mContext = context
        initView(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mContext = context
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        mContext = context
        initView(attrs)
    }

    private fun initView(attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.PercentRatingBar)
        mSelectedImg = array.getResourceId(
            R.styleable.PercentRatingBar_ratingSelectedImg, R.drawable.selected_star
        )
        mNotSelectImg = array.getResourceId(
            R.styleable.PercentRatingBar_ratingNotSelectImg, R.drawable.not_select_star
        )
        mTotalScore =
            array.getInteger(R.styleable.PercentRatingBar_ratingTotalScore, defaultRatingTotalCount)
        mSelectedCount = array.getFloat(R.styleable.PercentRatingBar_ratingSelectedScore, 0f)
        mRatingPadding = array.getDimensionPixelSize(R.styleable.PercentRatingBar_ratingPadding, 0)
        mStep = array.getInteger(R.styleable.PercentRatingBar_ratingStep, RATING_STEP_FULL)
        mIsSupportDrag = array.getBoolean(R.styleable.PercentRatingBar_ratingIsSupportDrag, true)

        array.recycle()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mIsSupportDrag) {
            return super.onTouchEvent(event)
        }
        //将星星和间距作为一组控件块
        val chunkWidth = mStarImgWidth + mRatingPadding
        //计算出包含多少个控件块,也就是占多少颗星,多少分
        var newCount = ((event.x - paddingStart.toFloat()) / chunkWidth)
        //计算出多滑出的百分比(一组控件块的)
        val starPaddingPercent: Float = (newCount - newCount.toInt())
        //计算出多滑出的百分比(一颗星的)
        var starPercent: Float = chunkWidth * starPaddingPercent / mStarImgWidth
        //将一颗星的百分比强制限制到1也就是100%
        if (starPercent > 1) {
            starPercent = 1f
        }
        //加上画出的百分比,得出新的分数
        newCount = newCount.toInt() + starPercent
        //最后根据步长类型,调整分数
        newCount = adjustRatingSelectedCount(newCount)
        if (mSelectedCount != newCount) {
            onRatingChangeListener(newCount)
        }
        mSelectedCount = newCount
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mSelectedCount > mTotalScore) {
            //限制评分,最高只能设置为星星总个数
            mSelectedCount = mTotalScore.toFloat()
        }

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        //绘制底部灰色星(未选中的)
        for (i in mSelectedCount.toInt() until mTotalScore) {
            canvas.drawBitmap(
                notStarBitmap,
                paddingStart + i * (mStarImgWidth.toFloat() + mRatingPadding),
                paddingTop.toFloat(),
                paint
            )
        }

        //绘制黄色星(选中的整颗星)
        for (i in 0 until mSelectedCount.toInt()) {
            canvas.drawBitmap(
                staredBitmap,
                paddingStart + i * (mStarImgWidth.toFloat() + mRatingPadding),
                paddingTop.toFloat(),
                paint
            )
        }

        //绘制半颗星
        val fractional = mSelectedCount - mSelectedCount.toInt()
        if (fractional > 0) {
            val left =
                paddingStart + mSelectedCount.toInt() * (mStarImgWidth.toFloat() + mRatingPadding).toInt()
            val right = left + (mStarImgWidth * fractional).toInt()
            //裁剪半颗星
            canvas.clipRect(left, paddingTop, right, paddingTop + staredBitmap.height)
            canvas.drawBitmap(staredBitmap, left.toFloat(), paddingTop.toFloat(), paint)
        }
    }

    /**
     * 调整选中的星星个数
     */
    private fun adjustRatingSelectedCount(ratingSelectedCount: Float): Float {
        var newRatingSelectedCount: Float
        when (mStep) {
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
        } else if (newRatingSelectedCount > mTotalScore) {
            newRatingSelectedCount = mTotalScore.toFloat()
        }

        return newRatingSelectedCount
    }

    /**
     * 设置总分数
     */
    fun setTotalScore(value: Int) {
        mTotalScore = value
        requestLayout()
        invalidate()
    }

    /**
     * 设置分数
     */
    fun setScore(value: Float) {
        val newValue = adjustRatingSelectedCount(value)
        if (mSelectedCount != newValue) {
            onRatingChangeListener(newValue)
        }
        mSelectedCount = newValue
        invalidate()
    }

    /**
     * 设置步长
     * @see PercentRatingBar.RATING_STEP_FULL
     * @see PercentRatingBar.RATING_STEP_HALF
     * @see PercentRatingBar.RATING_STEP_EXACTLY
     */
    fun setStep(@RatingStep ratingStep: Int) {
        mStep = ratingStep
        invalidate()
    }

    /**
     * 设置星星图片资源id
     */
    fun setImageRes(selectedImg: Int, notSelectImg: Int) {
        mSelectedImg = selectedImg
        mNotSelectImg = notSelectImg
        requestLayout()
        invalidate()
    }

    /**
     * 设置星星间距(单位:px)
     */
    fun setRatingPadding(ratingPadding: Int) {
        mRatingPadding = ratingPadding
        requestLayout()
        invalidate()
    }

    /**
     * 获取总分数
     */
    fun getTotalScore(): Int {
        return mTotalScore
    }

    /**
     * 获取分数
     */
    fun getScore(): Float {
        return mSelectedCount
    }

    /**
     * 设置是否支持拖动(默认支持)
     */
    fun setRatingIsSupportDrag(ratingIsSupportDrag: Boolean) {
        mIsSupportDrag = ratingIsSupportDrag
    }

    /**
     * 是否支持拖动(默认支持)
     */
    fun getRatingIsSupportDrag(): Boolean {
        return mIsSupportDrag
    }


    @Override
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // 获取宽-测量规则的模式和大小
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        // 获取高-测量规则的模式和大小
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        staredBitmap = BitmapFactory.decodeResource(resources, mSelectedImg)
        notStarBitmap = BitmapFactory.decodeResource(resources, mNotSelectImg)
        mStarImgWidth = staredBitmap.width
        mStarImgHeight = staredBitmap.height

        val mWidth =
            mTotalScore * (mStarImgWidth.toFloat() + mRatingPadding).toInt() - mRatingPadding + paddingStart + paddingEnd
        val mHeight = mStarImgHeight + paddingTop + paddingBottom

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
}