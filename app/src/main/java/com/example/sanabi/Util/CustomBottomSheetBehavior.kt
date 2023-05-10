package com.example.sanabi.Util

import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

class CustomBottomSheetBehavior : BottomSheetBehavior<View>() {

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && state == STATE_EXPANDED) {
            // Eğer bottom sheet dialog açıksa, yukarı sürüklemeyi engelle
            return true
        }
        return super.onInterceptTouchEvent(parent, child, event)
    }
}