package yancey.chelper.android.util;

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.Pair
import android.view.View
import android.view.ViewTreeObserver


/**
 *
 * @author Liang Jx
 *
 * @since 2019/8/5 11:36 AM
 *
 */
class KeyBoardWatcher private constructor() {
    private var globalLayoutListener: GlobalLayoutListener? = null
    private var decorView: View? = null

    interface OnKeyboardStateChangeListener {
        /**
         * 监听键盘状态变化监听
         * @param isShow 是否显示
         * @param heightDifference 界面变化的高度差
         */
        fun onKeyboardStateChange(isShow: Boolean, heightDifference: Int)
    }


    /**
     * 监听键盘的状态变化
     * @param decorView
     * @param listener
     * @return
     */
    fun init(
        decorView: View,
        listener: OnKeyboardStateChangeListener?
    ): KeyBoardWatcher {
        this.decorView = decorView
        this.globalLayoutListener = GlobalLayoutListener(listener)
        addSoftKeyboardChangedListener()
        return this
    }

    /**
     * 释放资源
     */
    fun release() {
        removeSoftKeyboardChangedListener()
        globalLayoutListener = null
    }

    /**
     * 取消软键盘状态变化监听
     */
    private fun removeSoftKeyboardChangedListener() {
        if (globalLayoutListener != null && null != decorView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                decorView!!.viewTreeObserver.removeOnGlobalLayoutListener(globalLayoutListener)
            } else {
                decorView!!.viewTreeObserver.removeGlobalOnLayoutListener(globalLayoutListener)
            }
        }
    }

    /**
     * 注册软键盘状态变化监听
     */
    private fun addSoftKeyboardChangedListener() {
        if (globalLayoutListener != null && null != decorView) {
            removeSoftKeyboardChangedListener()
            decorView!!.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
        }
    }

    /**
     * 判断键盘是否显示
     * @param context
     * @param decorView
     * @return
     */
    fun isKeyboardShowing(context: Context?, decorView: View): Pair<Boolean, Int> {
        val outRect = Rect()
        //指当前Window实际的可视区域大小，通过decorView获取到程序显示的区域，包括标题栏，但不包括状态栏。
        decorView.getWindowVisibleDisplayFrame(outRect)
        val displayScreenHeight = context!!.resources.displayMetrics.heightPixels

        //如果屏幕高度和Window可见区域高度差值大于0，则表示软键盘显示中，否则软键盘为隐藏状态。
        val heightDifference = displayScreenHeight - outRect.bottom
        return Pair(heightDifference > 0, heightDifference)
    }

    fun isKeyboardShow(decorView: View): Boolean {
        return isKeyboardShowing(decorView.context, decorView).second > 0
    }

    inner class GlobalLayoutListener(private val onKeyboardStateChangeListener: OnKeyboardStateChangeListener?) :
        ViewTreeObserver.OnGlobalLayoutListener {

        private var isKeyboardShow = false

        init {
            this.isKeyboardShow = false
        }

        override fun onGlobalLayout() {
            if (onKeyboardStateChangeListener != null && decorView != null) {
                val pair = isKeyboardShowing(decorView!!.context, decorView!!)
                if (pair.first) {
                    onKeyboardStateChangeListener.onKeyboardStateChange(isKeyboardShow, pair.second)
                    isKeyboardShow = true
                } else if (isKeyboardShow) {
                    onKeyboardStateChangeListener.onKeyboardStateChange(isKeyboardShow, pair.second)
                    isKeyboardShow = false
                }
            }
        }
    }

    companion object {

        fun get(): KeyBoardWatcher {
            return KeyBoardWatcher()
        }
    }
}