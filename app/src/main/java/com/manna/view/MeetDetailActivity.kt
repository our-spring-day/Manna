package com.manna.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.manna.R
import com.manna.ext.ViewUtil
import kotlinx.android.synthetic.main.activity_meet_detail.*


class MeetDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meet_detail)

        ViewUtil.setStatusBarTransparent(this)

        top_panel.fitsSystemWindows = true

        btn_back.setOnClickListener {
            onBackPressed()
        }

        btn_menu.setOnClickListener {
            drawer.openDrawer(side_panel)
        }

        BottomSheetBehavior.from(bottom_sheet)
            .addBottomSheetCallback(createBottomSheetCallback(bottom_sheet_state))
    }

    private fun createBottomSheetCallback(text: TextView): BottomSheetCallback =
        object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                text.text = when (newState) {
                    BottomSheetBehavior.STATE_DRAGGING -> "STATE DRAGGING"
                    BottomSheetBehavior.STATE_EXPANDED -> "STATE EXPANDED"
                    BottomSheetBehavior.STATE_COLLAPSED -> "STATE COLLAPSED"
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        String.format(
                            "STATE_HALF_EXPANDED\\nhalfExpandedRatio = %.2f",
                            BottomSheetBehavior.from(bottomSheet).halfExpandedRatio
                        )
                    }
                    else -> {
                        text.text.toString()
                    }
                }
            }

            override fun onSlide(
                bottomSheet: View,
                slideOffset: Float
            ) {
            }
        }
}
