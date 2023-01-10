package com.dzeio.charts

import kotlin.math.abs
import kotlin.math.max

data class Animation(
    /**
     * Enable / Disable the Chart Animations
     */
    var enabled: Boolean = true,

    /**
     * Number of milliseconds the animation is running before it ends
     */
    var duration: Int = 1000,

    /**
     * Number of updates per seconds
     */
    var refreshRate: Int = 50
) {
    /**
     * Update the value depending on the maximum obtainable value
     *
     * @param maxValue the maximum value the item can obtain
     * @param targetValue the value you want to obtain at the end of the animation
     * @param currentValue the current value
     *
     * @return the new updated value
     */
    fun updateValue(
        maxValue: Float,
        targetValue: Float,
        currentValue: Float,
        minValue: Float,
        minStep: Float
    ): Float {
        if (!enabled) {
            return targetValue
        }

        if (currentValue < minValue) {
            return minValue
        }

        val moveValue = max(minStep, (maxValue - targetValue) / refreshRate)

        var result = targetValue
        if (currentValue < targetValue) {
            result = currentValue + moveValue
        } else if (currentValue > targetValue) {
            result = currentValue - moveValue
        }

        if (
            abs(targetValue - currentValue) <= moveValue ||
            result < minValue ||
            result > maxValue
        ) {
            return targetValue
        }
        return result
    }

    fun getDelay() = this.duration / this.refreshRate
}
