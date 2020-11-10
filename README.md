# PercentRatingBar
百分比评分控件

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/079533118d624aa38e65ec73f3febf37~tplv-k3u1fbpfcp-watermark.image)

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d7496e3e0b6d4413b09657d03f85921a~tplv-k3u1fbpfcp-watermark.image)

# 依赖方式
```gradle
buildscript {
    repositories {
        google()
        jcenter()
    }
}
```
```gradle
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

# 前言
公司的产品需要一个评分控件,并且分数并不仅仅是1.5、2.5这样的，而是要支持1.1、1.9分，并且星星的评分样式也要与分值完全对应
也就是要实现这种效果

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/800d1d17602c468f9511cab4ef258428~tplv-k3u1fbpfcp-watermark.image)

我一听就懵逼了,这不是为难我么

![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3d114f8615f642f5adbc87bb26513e39~tplv-k3u1fbpfcp-watermark.image)

不过既然产品既然提了需求,咱也得尽量去实现,否则以后还怎么愉快玩耍

![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f57e94a5cf7e42478a131bfbc0811fe8~tplv-k3u1fbpfcp-watermark.image)

# 需求描述
1. 支持整颗星、半颗星和按百分比评分
2. 支持滑动和点击评分
3. 支持自定义星星图标和星星间距
4. 支持...好了闭嘴吧...咱都给你实现了

# 需求分析
1. 首先绘制星星很简单,调用canvas.drawBitmap就可以,多个星星for循环绘制即可
2. 复杂的地方有两处:a.如何绘制残缺星星,b:如果在滑动时,将滑动位置转化为分数

# 开始写demo

## 我们先画一颗星看看效果
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/d4013f687fbd4237851d37da82b91911~tplv-k3u1fbpfcp-watermark.image)

不过没关系,我们可以换个改为在onMeasure中获取bitmap,并将其作为全局变量存起来(因为要计算星星的宽高以及整体自定义view宽高,所以本身也是需要在onMeasure中写的)

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/74a0cf7f13f74434a6faab1d475ef6e8~tplv-k3u1fbpfcp-watermark.image)

## 再来绘制五颗星
好,一颗星我们画完了,那么5颗星就for循环呗
```kotlin
override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    //绘制底部灰色星(未选中的)
    for (i in 0 until 5) {
        canvas.drawBitmap(
            staredBitmap,
            i * mStarImgWidth.toFloat(),//这里要记得每颗星星要向右偏移,否则5颗星星就重合了
            0f,
            paint
        )
    }
}
```
效果图![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/3b4a093108694e8ba50aadd2cbcd1261~tplv-k3u1fbpfcp-watermark.image)

绘制背景的5颗灰色的星星也是一样的思路,只是需要先绘制5颗灰色星星,再绘制N颗黄色星星,代码就不贴了

## 问题来了

但产品要求评分要精确到小数,所以问题来了,当分数为2.7,那么那0.7分的残缺星星该怎么画呢

![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/1ce60ab05c4a49e0853cd87ff95a3951~tplv-k3u1fbpfcp-watermark.image)

此时需要用到一个方法:canvas.clipRect(int left, int top, int right, int bottom),该方法是用来裁剪绘制区域的,具体用法我就不赘述了,大家参考这篇博客吧,作者讲得还挺详细的[https://www.jianshu.com/p/550d85419121](url)

绘制残缺星星的代码
```kotlin
override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val fractional = 0.7f
    //裁剪半颗星
    canvas.clipRect(0, 0, (mStarImgWidth*fractional).toInt(), staredBitmap.height)
    canvas.drawBitmap(staredBitmap, left.toFloat(), 0f, paint)
}
```
效果图![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/6866e03a54364a3488ca098f13225a55~tplv-k3u1fbpfcp-watermark.image)

哈哈,到此你们肯定就能够实现如何绘制2.7分的评分了,无非就是以下三步
- 绘制5颗灰色星星
- 绘制2颗黄色星星
- 绘制1颗裁剪0.7倍的黄色星星

在源码中有一处小小的优化,就是灰色星星不用绘制5颗,只需要绘制黄色星星没覆盖的地方,避免浪费
具体代码如下:
![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/479872ba110a40e78946d084eefc6223~tplv-k3u1fbpfcp-watermark.image)

好了,现在贴一下目前的代码和效果图
```kotlin
val totalScore=5        //总分写死为5分
val score=2.7f          //评分写死为2.7分
override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        //绘制底部灰色星(未选中的)
        for (i in score.toInt() until totalScore) {
            canvas.drawBitmap(
                notStarBitmap, i * (mStarImgWidth.toFloat()), 0f, paint
            )
        }

        val fractional = score-score.toInt()//2.7分:代表残缺星星的评分

        //绘制黄色星(选中的整颗星)
        for (i in 0 until score.toInt()) {
            canvas.drawBitmap(
                staredBitmap, i * (mStarImgWidth.toFloat()), 0f, paint
            )
        }

        //计算绘制的左侧位置和右侧位置
        val left =
            paddingStart + score.toInt() * (mStarImgWidth.toFloat()).toInt()
        val right = left + (mStarImgWidth * fractional).toInt()

        //裁剪半颗星
        canvas.clipRect(left, 0, right, staredBitmap.height)
        canvas.drawBitmap(staredBitmap, left.toFloat(), 0f, paint)
    }
```
效果图![](https://p3-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/287e25ec22d54331a018ea60d9687125~tplv-k3u1fbpfcp-watermark.image)

# 实现滑动评分效果
## 在看代码之前先看一张说明图
![](https://p9-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/39117648e26c40498d4938bb01daed63~tplv-k3u1fbpfcp-watermark.image)
解释:
- paddingStart:就是官方的android:paddingStart属性,代表左边距
- mStarImgWidth:星星的宽度
- mRatingPadding:两颗星星的左右间距
- mStarImgWidth加mRatingPadding作为一个整体,我将其称为:控件块,代码中的变量名叫做:chunkWidth,(起名字真是个麻烦的事情)![](https://p1-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/f37d6e5dd3744e078b58190812647958~tplv-k3u1fbpfcp-watermark.image)

## 具体逻辑代码
```kotlin
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
    //加上滑出的百分比,得出新的分数
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
```
## 算法讲解
what?算法讲解是不可能讲解的,这辈子都不可能讲解(主要是我表达能力有限,容易让你们失去阅读兴趣,干扰你们思路~)

![](https://p6-juejin.byteimg.com/tos-cn-i-k3u1fbpfcp/8b9aafd49efc421ba09a9317cc293844~tplv-k3u1fbpfcp-watermark.image)

## 掘金地址:https://juejin.im/post/6893443087679684615/
