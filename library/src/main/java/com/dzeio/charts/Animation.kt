package com.dzeio.charts

import kotlin.math.abs

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
     * @param targetValue the value you want to obtain at the end of the animation
     * @param currentValue the current value
     * @param startValue the value at which the the base started on
     * @param step override the auto moveValue change
     *
     * @return the new updated value
     */
    fun updateValue(
        targetValue: Float,
        currentValue: Float,
        startValue: Float,
        step: Float?
    ): Float {
        if (!enabled) {
            return targetValue
        }

        val moveValue = step ?: (abs(targetValue - startValue) / duration * refreshRate)

        var result = targetValue
        if (currentValue < targetValue) {
            result = currentValue + moveValue
        } else if (currentValue > targetValue) {
            result = currentValue - moveValue
        }

        if (
            abs(targetValue - currentValue) <= moveValue
        ) {
            return targetValue
        }
        return result
    }

    /**
     * Update the value depending on the maximum obtainable value
     *
     * @param targetValue the value you want to obtain at the end of the animation
     * @param currentValue the current value
     * @param startValue the value at which the the base started on
     *
     * @return the new updated value
     */
    fun updateValue(
        targetValue: Float,
        currentValue: Float,
        startValue: Float
    ): Float {
        return updateValue(
            targetValue,
            currentValue,
            startValue,
            null
        )
    }

    fun getDelay() = this.duration / this.refreshRate
}
