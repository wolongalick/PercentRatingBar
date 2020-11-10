# PercentRatingBar
百分比评分控件
![image](https://github.com/wolongalick/PercentRatingBar/blob/master/img/demo_cover.jpg)
![image](https://github.com/wolongalick/PercentRatingBar/blob/master/img/demo.gif)
# 导入
```
buildscript {
    repositories {
        google()
        jcenter()
    }
}
```


```
dependencies {
    implementation 'com.wolongalick.widget:PercentRatingBar:1.0.1'
}
```
# 快速使用
```xml
<com.wolongalick.widget.PercentRatingBar
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```

# 完整使用
```xml
<com.wolongalick.widget.PercentRatingBar
    android:id="@+id/percentRatingBar"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:padding="20dp"
    app:ratingStaredImg="@drawable/selected_star"
    app:ratingNotStarImg="@drawable/not_select_star"
    app:ratingSelectedCount="3.7"
    app:ratingMaxCount="10"
    app:ratingPadding="2dp"
    app:ratingIsSupportDrag="true"
    app:ratingStep="exactly"
    />
```
# 自定义属性详解

属性名 | 含义| 对应java/kotlin方法
---|---|---
ratingSelectedImg | 选中的星星图片资源id | setImageRes(Int, Int)
ratingNotSelectImg| 未选中的星星图片资源id | setImageRes(Int, Int)
ratingSelectedScore| 选中的星星个数评分(支持小数) | setScore(Float)和getScore()
ratingTotalScore| 总分数 | setTotalScore(Int)和getTotalScore()
ratingPadding| 星星之间的间距,单位px | setRatingPadding(Int)
ratingIsSupportDrag| 是否支持拖动 | setRatingIsSupportDrag(Boolean)和getRatingIsSupportDrag()
ratingStep| 星星步长(full:整颗星、half:半颗星、exactly:精确到具体刻度比例) | setStep(@RatingStep step: Int)








