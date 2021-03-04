package com.manna.presentation.make_meet

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.manna.R
import com.manna.common.BaseBottomSheetFragment
import com.manna.databinding.FragmentMemoBinding
import com.manna.ext.openKeyboard
import com.manna.ext.toast

class MemoBottomSheetFragment :
    BaseBottomSheetFragment<FragmentMemoBinding>(R.layout.fragment_memo) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.DialogStyle)
    }

    override fun onStart() {
        super.onStart()
        view?.run {
            post {
                val parent = parent as View
                val params = parent.layoutParams as CoordinatorLayout.LayoutParams
                val behavior = params.behavior
                val bottomSheetBehavior = behavior as BottomSheetBehavior

                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val prevMemo = arguments?.getString(MEMO).orEmpty()
            inputMemo.setText(prevMemo)

            inputMemo.openKeyboard()

            close.setOnClickListener {
                dismiss()
            }

            sendButton.setOnClickListener {
                val memo = inputMemo.text.toString()

                if (memo.isEmpty()) {
                    toast(getString(R.string.empty_memo))
                    return@setOnClickListener
                }

                val data = Bundle().apply { putString(MEMO, memo) }
                setFragmentResult(this@MemoBottomSheetFragment::class.java.simpleName, data)
                dismiss()
            }
        }
    }

    companion object {
        const val MEMO = "memo"

        fun newInstance(memo: String) =
            MemoBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString(MEMO, memo)
                }
            }
    }
}