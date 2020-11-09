package com.wolongalick.percent_rating_bar

import android.content.Context

/**
 * @createTime 2020/11/9 14:05
 * @author 崔兴旺
 * @description
 */
class DensityUtils {
    companion object {
        /**
         * dp转px
         */
        fun dp2px(context: Context, dp: Float): Int {
            return (dp * context.resources.displayMetrics.density + 0.5f).toInt()
        }
    }
}