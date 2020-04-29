package com.example.canexpandview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull

/**
//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \\|     |// '.
//                 / \\|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \\\  -  /// |   |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               佛祖保佑         永无BUG

 * @author 雄
 * Date: 2020/4/29
 * ClassName: 15:13
 */
class CanExpandTextView : LinearLayout {

    /**显示内容的控件*/
    private var visibleContextTv: TextView? = null
    /**点击控件*/
    private var clickExpandTv: TextView? = null
    /**默认显示的最大行数*/
    private var maxExpandLines = 0
    /**动画的时间*/
    private var animalDuration = 200
    /**文本内容真实高度*/
    private var realHeight = 0
    /**来个标记记录文字是否发生了变动*/
    private var flagInching = false
    /**来记录是否是收缩状态*/
    private var isClosed = true
    /**未展开时候的容器布局的高度*/
    private var shrinkageHeight = 0
    /**剩余控件高度*/
    private var lastHeight = 0
    /**判断是否在执行动画*/
    private var isAnimate = false
    /**定义一个接口来监听我们的容器布局展开收缩的状态*/
    private var expandStateListener: ExpandStateListener? = null

    constructor(context: Context) : super(context, null)
    constructor(context: Context, @NonNull attrs: AttributeSet) : super(context, attrs) {
        //初始化属性
        init(context, attrs)
    }

    /**
     * 初始化属性
     */
    @SuppressLint("CustomViewStyleable")
    private fun init(context: Context, attrs: AttributeSet) {
        //设置布局排序方向
        orientation = VERTICAL
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandParames)
        //默认显示的最大行数
        maxExpandLines = typedArray.getInteger(R.styleable.ExpandParames_max_expend_lines, 4)
        //动画执行时间
        animalDuration = typedArray.getInteger(R.styleable.ExpandParames_animal_duration, 200)
        //回收TypedArray
        typedArray.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        //初始化容器内部的两个控件
        visibleContextTv = findViewById(R.id.visible_context_tv)
        clickExpandTv = findViewById(R.id.click_expand_tv)
        //设置点击事件
        clickExpandTv?.setOnClickListener {
            val expandAnimal: ExpandAnimal
            isClosed = !isClosed
            if (isClosed) {
                //设置显示让用户去操作
                clickExpandTv?.text = "展开"

                expandStateListener?.expandStartChangerListener(true)

                //收缩所以我们开始高度getHeight(),结束变为0
                expandAnimal = ExpandAnimal(height, shrinkageHeight)
            } else {
                clickExpandTv?.text = "收起"

                expandStateListener?.expandStartChangerListener(false)

                //展开：结束执行过程时候高度为内容控件的高度+点击控件的高度
                expandAnimal = ExpandAnimal(height, lastHeight + realHeight)
            }
            //让执行之后的动画保存当前状态。
            expandAnimal.fillAfter = true
            expandAnimal.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    clearAnimation()
                    isAnimate = false
                }

                override fun onAnimationStart(p0: Animation?) {
                    isAnimate = true
                }

            })
            clearAnimation()
            startAnimation(expandAnimal)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        //如果正在执行动画，那么其他打断时间分发。
        return isAnimate
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //如果控件被设置Visible=gon或者内容控件TextView内容没有变化那么没必要测量
        if (visibility == View.GONE || !flagInching){
            return
        }
        flagInching = false
        //初始化默认状态，显示文本就可以
        clickExpandTv?.visibility = View.GONE
        visibleContextTv?.maxLines = Integer.MAX_VALUE
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //如果本身没有文字行数没有达到限制最少的行数那么就没必要展开或者
        if (visibleContextTv!!.lineCount <= maxExpandLines){
            return
        }
        //获取内容TexView的真实高度，后面我们需要用到
        realHeight = getRealHeightTextView(visibleContextTv!!)
        //如果处于收缩状态，则设置最多显示行数
        if (isClosed){
            visibleContextTv?.setLines(maxExpandLines)
        }
        clickExpandTv?.visibility = View.VISIBLE
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //如果是收缩的状态那么需要去
        if (isClosed){
            visibleContextTv?.post {
                //剩余高度=当前收缩高度-内容控件的高度
                lastHeight = height - visibleContextTv!!.height
                //收缩时候的容器的高度
                shrinkageHeight = measuredHeight
            }
        }
    }

    private fun getRealHeightTextView(visible_context_tv: TextView): Int {
        //getLineTop返回值是一个根据行数而形成等差序列，如果参数为行数，则值即为文本的高度
        val textHeight = visible_context_tv.layout.getLineTop(visible_context_tv.lineCount)
        return textHeight + visible_context_tv.compoundPaddingBottom + visible_context_tv.compoundPaddingTop
    }

    /**
     * 展开动画监听器
     */
    fun setListener(listener: ExpandStateListener) {
        this.expandStateListener = listener
    }

    inner class ExpandAnimal(var startHeight: Int, var endHeight: Int) : Animation() {

        init {
            duration = animalDuration.toLong()
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            super.applyTransformation(interpolatedTime, t)
            //这里我们需要计算内容TextView的变化的动态高度height
            //我们将开始的高度+内容变化高度
            //内容变化高度=(startHeight + (endHeight  - startHeight) * interpolatedTime)
            var height = (startHeight + (endHeight - startHeight) * interpolatedTime).toInt()
            //动态的设置内容TextView的高度
            visibleContextTv?.height = height - lastHeight
            //从新摆放容器布局的子view
            this@CanExpandTextView.layoutParams.height = height
            this@CanExpandTextView.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }


    /**
     * 动态设置字体
     */
    private fun setText(text: String) {
        flagInching = true
        visibleContextTv?.text = text
    }

    fun setText(text: String, isClosed: Boolean) {
        this.isClosed = isClosed
        clickExpandTv?.text = if (isClosed) "展开" else "\n收起"
        clearAnimation()
        setText(text)
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
    }


    interface ExpandStateListener {
        /**
         * 展开状态监听器
         */
        fun expandStartChangerListener(isExpand: Boolean)
    }
}