package com.dzeio.charts.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import kotlin.math.sqrt

/**
 * draw a dotted line
 */
fun Canvas.drawDottedLine(
    startX: Float,
    startY: Float,
    endX: Float,
    endY: Float,
    spacing: Float,
    paint: Paint
) {
    //calculate line length
    val length = if (endX - startX == 0f) {
        // just length of Y
        endY - startY
    } else if (endY - startY == 0f) {
        // just length of X
        endX - startX
    } else {
        // calculate using the Pythagorean theorem
        sqrt((startX + endX) * (startX + endX) + (startY + endY) * (startY + endY))
    }

    val lineCount = (length / spacing).toInt()

    val lenX = endX - startX
    val lenY = endY - startY

//    Log.d("DrawDottedLine", "----------- Start -----------")
//    Log.d("DrawDottedLine", "lenX: $lenX, lenY: $lenY")
    for (line in 0 until lineCount) {
        if (line % 2 == 0) {
            continue
        }

        val sx = lenX / lineCount * line + startX
        val sy = lenY / lineCount * line + startY
        val ex = lenX / lineCount * (line + 1)  + startX
        val ey = lenY / lineCount * (line + 1)  + startY
//        Log.d("DrawDottedLine", "$sx, $sy, $ex, $ey")
        this.drawLine(sx, sy, ex, ey, paint)
        // line
        // total line startX, endX, startY, endY
        // total line length
    }
}

/**
 * A more customizable drawRoundRect function
 */
fun Canvas.drawRoundRect(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    topLeft: Float,
    topRight: Float,
    bottomLeft: Float,
    bottomRight: Float,
    paint: Paint
) {
    val maxRound = arrayOf(topLeft, topRight, bottomLeft, bottomRight).maxOf { it }
    val width = right - left
    val height = bottom - top

    // draw first/global rect
    drawRoundRect(left, top, right, bottom, maxRound, maxRound, paint)

    // top left border
    if (topLeft == 0f) {
        drawRect(left, top, left + width / 2, top + height / 2, paint)
    } else {
        drawRoundRect(left, top, left + width / 2, top + height / 2, topLeft, topLeft, paint)
    }

    // top right border
    if (topRight == 0f) {
        drawRect(right - width / 2, top, right, top + height / 2, paint)
    } else {
        drawRoundRect(right - width / 2, top, right, top + height / 2, topRight, topRight, paint)
    }

    // bottom left border
    if (bottomLeft == 0f) {
        drawRect(left, bottom - height / 2, left + width / 2, bottom, paint)
    } else {
        drawRoundRect(
            left,
            bottom - height / 2,
            left + width / 2,
            bottom,
            bottomLeft,
            bottomLeft,
            paint
        )
    }

    // bottom right border
    if (bottomRight == 0f) {
        drawRect(right - width / 2, bottom - height / 2, right, bottom, paint)
    } else {
        drawRoundRect(
            right - width / 2,
            bottom - height / 2,
            right,
            bottom,
            bottomRight,
            bottomRight,
            paint
        )
    }

}

/**
 * A more customizable drawRoundRect function
 */
fun Canvas.drawRoundRect(
    rect: RectF,
    topLeft: Float,
    topRight: Float,
    bottomLeft: Float,
    bottomRight: Float,
    paint: Paint
) {
    drawRoundRect(
        rect.left,
        rect.top,
        rect.right,
        rect.bottom,
        topLeft,
        topRight,
        bottomLeft,
        bottomRight,
        paint
    )
}
