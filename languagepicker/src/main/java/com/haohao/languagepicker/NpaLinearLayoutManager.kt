package com.haohao.languagepicker

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * No Predictive Animations GridLayoutManager
 */
class NpaLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    override fun supportsPredictiveItemAnimations(): Boolean {
        return false
    }
}
