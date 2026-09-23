package com.haohao.camposer.state

import android.util.Size
import androidx.camera.core.AspectRatio
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy

/**
 * Image analysis / capture target size, accepting [AspectRatio] or [Size].
 * Mapped onto CameraX [ResolutionSelector].
 */
data class ImageTargetSize(
    private val aspectRatio: Int? = null,
    private val size: Size? = null,
) {

    /**
     * Target size using [AspectRatio].
     */
    constructor(@AspectRatio.Ratio aspectRatio: Int?) : this(
        aspectRatio = aspectRatio,
        size = null,
    )

    /**
     * Target size using [Size].
     */
    constructor(size: Size?) : this(
        aspectRatio = null,
        size = size,
    )

    internal fun toResolutionSelector(): ResolutionSelector? {
        val ratio = aspectRatio
        val target = size
        if (ratio == null && target == null) return null
        return ResolutionSelector.Builder().apply {
            if (ratio != null) {
                setAspectRatioStrategy(
                    AspectRatioStrategy(ratio, AspectRatioStrategy.FALLBACK_RULE_AUTO),
                )
            }
            if (target != null) {
                setResolutionStrategy(
                    ResolutionStrategy(
                        target,
                        ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER,
                    ),
                )
            }
        }.build()
    }
}
