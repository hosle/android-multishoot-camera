package com.hosle.multishootcamera.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

/**
 * Created by tanjiahao on 2018/1/18
 * Original Project HoMultiShootCamera
 */

class UploadGridLayout : ViewGroup {

    private val cellPadding: Int = 0
    private val column = 3

    internal var RATIO = 25f / 25

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val lineCount = if (childCount % column == 0) childCount / column else childCount / column + 1

        val childWidthMeasure = View.MeasureSpec.getSize(widthMeasureSpec) / column

        val childHeightMeasure = (childWidthMeasure / RATIO).toInt()


        val childMeasureWidthSpec = View.MeasureSpec.makeMeasureSpec(childWidthMeasure, View.MeasureSpec.getMode(widthMeasureSpec))

        val childMeasureHeightSpec = View.MeasureSpec.makeMeasureSpec(childHeightMeasure, View.MeasureSpec.getMode(heightMeasureSpec))

        measureChildWithMargin(childMeasureWidthSpec, childMeasureHeightSpec)

        val measuredHeightSpec = View.MeasureSpec.makeMeasureSpec(childHeightMeasure * lineCount,
                View.MeasureSpec.getMode(heightMeasureSpec))

        setMeasuredDimension(widthMeasureSpec, measuredHeightSpec)

        super.onMeasure(widthMeasureSpec, measuredHeightSpec)
    }

    private fun measureChildWithMargin(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            val layoutParams = child.layoutParams as RelativeLayout.LayoutParams

            val widthMeasure = View.MeasureSpec.getSize(widthMeasureSpec) - layoutParams.leftMargin - layoutParams.rightMargin
            val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
            val paddingWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(widthMeasure, widthMode)

            val heightMeasure = View.MeasureSpec.getSize(heightMeasureSpec) - layoutParams.topMargin - layoutParams.bottomMargin
            val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
            val paddingHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(heightMeasure, heightMode)

            measureChild(child, paddingWidthMeasureSpec, paddingHeightMeasureSpec)
        }


    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        var sumHeight = paddingTop
        var sumChildMarginRightSum = 0
        var lastLineMaxMarginBottom = 0
        var lastLineMaxMarginTop = 0


        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val columnCount = i % column

            val layoutParams = child.layoutParams as RelativeLayout.LayoutParams

            val cellWidth = child.measuredWidth
            val cellHeight = child.measuredHeight

            if (i % column == 0 && i > 0) {
                sumHeight += cellHeight + lastLineMaxMarginBottom + lastLineMaxMarginTop
                lastLineMaxMarginBottom = 0
                sumChildMarginRightSum = 0
            }

            val layoutLeft = cellWidth * columnCount + layoutParams.leftMargin + sumChildMarginRightSum
            val layoutRight = layoutLeft + cellWidth
            val layoutTop = sumHeight + layoutParams.topMargin
            val layoutBottom = layoutTop + cellHeight

            child.layout(layoutLeft, layoutTop, layoutRight, layoutBottom)

            sumChildMarginRightSum += layoutParams.rightMargin + layoutParams.leftMargin
            lastLineMaxMarginBottom = maxOf(lastLineMaxMarginBottom, layoutParams.bottomMargin)
            lastLineMaxMarginTop = maxOf(lastLineMaxMarginTop,layoutParams.topMargin)
        }
    }
}