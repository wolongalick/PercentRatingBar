package com.wolongalick.widget.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.wolongalick.annotation.RatingStep
import com.wolongalick.widget.PercentRatingBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        skRatingMaxCount.setOnSeekBarChangeListener(this)
        skRatingSelectedCount.setOnSeekBarChangeListener(this)
        skRatingPadding.setOnSeekBarChangeListener(this)

        percentRatingBar.onRatingChangeListener = {
            tvCurrentRatingValue.text = "当前分值:${it}分"
            Log.i("alick","当前分值:${it}分")
        }
        percentRatingBar.setRatingIsSupportDrag(false)

        testInKotlin()
    }

    /**
     * 在kotlin代码中添加布局
     */
    private fun testInKotlin() {
        val percentRatingBar = PercentRatingBar(this)
        percentRatingBar.setTotalScore(10)
        percentRatingBar.setScore(5.7f)
        percentRatingBar.setRatingPadding(DensityUtils.dp2px(this, 40f))
        percentRatingBar.setImageRes(R.drawable.red_star, R.drawable.black_star)
        percentRatingBar.setStep(PercentRatingBar.RATING_STEP_HALF)
        llRoot.addView(percentRatingBar)
    }

    /**
     * 重置UI
     */
    fun resetUI(view: View) {
        percentRatingBar.setTotalScore(10)
        percentRatingBar.setScore(3.7f)
        percentRatingBar.setImageRes(R.drawable.selected_star, R.drawable.not_select_star)
        percentRatingBar.setRatingPadding(DensityUtils.dp2px(this, 2f))
        percentRatingBar.setStep(PercentRatingBar.RATING_STEP_EXACTLY)

        rg.check(R.id.rb3)

        skRatingMaxCount.progress = 5
        skRatingSelectedCount.progress = 37
        skRatingPadding.progress = 2

    }

    fun stepFull(view: View) {
        percentRatingBar.setStep(PercentRatingBar.RATING_STEP_FULL)
    }

    fun stepHalf(view: View) {
        percentRatingBar.setStep(PercentRatingBar.RATING_STEP_HALF)
    }

    fun stepExactly(view: View) {
        percentRatingBar.setStep(PercentRatingBar.RATING_STEP_EXACTLY)
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (!fromUser) {
            return
        }

        when (seekBar) {

            //调整星星总个数
            skRatingMaxCount -> {
                val ratingSelectedCount = percentRatingBar.getScore()
                val minProgress = (ratingSelectedCount + 0.5f).roundToInt()
                val newProgress = if (progress < minProgress) {
                    minProgress
                } else {
                    progress
                }

                seekBar.progress = newProgress
                percentRatingBar.setTotalScore(newProgress)
            }

            //调整分数
            skRatingSelectedCount -> {
                percentRatingBar.setScore(progress.toFloat() / 100 * percentRatingBar.getTotalScore())
            }

            //调整星星间距
            skRatingPadding -> {
                percentRatingBar.setRatingPadding(DensityUtils.dp2px(this, progress.toFloat()))
            }
        }

    }

    override fun onStartTrackingTouch(p0: SeekBar?) {


    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

    fun updateRatingImageRed(view: View) {
        percentRatingBar.setImageRes(R.drawable.red_star, R.drawable.black_star)

    }

    fun updateRatingImageBlue(view: View) {
        percentRatingBar.setImageRes(R.drawable.blue_star, R.drawable.black_star)

    }

    fun updateRatingImageYellow(view: View) {
        percentRatingBar.setImageRes(R.drawable.yellow_star, R.drawable.black_star)
    }


}