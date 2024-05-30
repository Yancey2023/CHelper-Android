package com.lzf.easyfloat.utils

import android.view.*
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnFloatAnimator
import com.lzf.easyfloat.interfaces.OnTouchRangeListener
import com.lzf.easyfloat.widget.BaseSwitchView

/**
 * @author: liuzhenfeng
 * @date: 2020/10/24  21:29
 * @Package: com.lzf.easyfloat.utils
 * @Description: 拖拽打开、关闭浮窗
 */
object DragUtils {

    private const val ADD_TAG = "ADD_TAG"
    private const val CLOSE_TAG = "CLOSE_TAG"
    private var addView: BaseSwitchView? = null
    private var closeView: BaseSwitchView? = null
    private var downX = 0f
    private var screenWidth = 0
    private var offset = 0f

    private fun setAddView(
        event: MotionEvent,
        progress: Float,
        listener: OnTouchRangeListener? = null,
        layoutId: Int
    ) {
        // 设置触摸状态监听
        addView?.let {
            it.setTouchRangeListener(event, listener)
            it.translationX = it.width * (1 - progress)
            it.translationY = it.width * (1 - progress)
        }
        // 手指抬起或者事件取消，关闭添加浮窗
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) dismissAdd()
        else showAdd(layoutId)
    }

    private fun showAdd(layoutId: Int) {
        if (EasyFloat.isShow(ADD_TAG)) return
        EasyFloat.with(LifecycleUtils.application)
            .setLayout(layoutId)
            .setShowPattern(ShowPattern.CURRENT_ACTIVITY)
            .setTag(ADD_TAG)
            .setDragEnable(false)
            .setSidePattern(SidePattern.BOTTOM)
            .setGravity(Gravity.BOTTOM or Gravity.END)
            .setAnimator(null)
            .registerCallback {
                createResult { isCreated, _, view ->
                    if (!isCreated || view == null) return@createResult
                    if ((view as ViewGroup).childCount > 0) {
                        // 获取区间判断布局
                        view.getChildAt(0).apply {
                            if (this is BaseSwitchView) {
                                addView = this
                                translationX = width.toFloat()
                                translationY = width.toFloat()
                            }
                        }
                    }
                }
                dismiss { addView = null }
            }
            .show()
    }

    private fun showClose(
        layoutId: Int,
        showPattern: ShowPattern,
        appFloatAnimator: OnFloatAnimator?
    ) {
        if (EasyFloat.isShow(CLOSE_TAG)) return
        EasyFloat.with(LifecycleUtils.application)
            .setLayout(layoutId)
            .setShowPattern(showPattern)
            .setMatchParent(widthMatch = true)
            .setTag(CLOSE_TAG)
            .setSidePattern(SidePattern.BOTTOM)
            .setGravity(Gravity.BOTTOM)
            .setAnimator(appFloatAnimator)
            .registerCallback {
                createResult { isCreated, _, view ->
                    if (!isCreated || view == null) return@createResult
                    if ((view as ViewGroup).childCount > 0) {
                        // 获取区间判断布局
                        view.getChildAt(0).apply { if (this is BaseSwitchView) closeView = this }
                    }
                }
                dismiss { closeView = null }
            }
            .show()
    }

    private fun dismissAdd() = EasyFloat.dismiss(ADD_TAG)

    private fun dismissClose() = EasyFloat.dismiss(CLOSE_TAG)

}