package com.wolongalick.percent_rating_bar.annotation;

import androidx.annotation.IntDef;


import com.wolongalick.percent_rating_bar.PercentRatingBar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({PercentRatingBar.RATING_STEP_FULL, PercentRatingBar.RATING_STEP_HALF, PercentRatingBar.RATING_STEP_EXACTLY})
@Retention(RetentionPolicy.SOURCE)
public @interface RatingStep {

}

