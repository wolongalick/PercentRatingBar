package com.wolongalick.annotation;

import androidx.annotation.IntDef;


import com.wolongalick.widget.PercentRatingBar;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({PercentRatingBar.RATING_STEP_FULL, PercentRatingBar.RATING_STEP_HALF, PercentRatingBar.RATING_STEP_EXACTLY})
@Retention(RetentionPolicy.SOURCE)
public @interface RatingStep {

}

