package com.manna

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityHomeBinding
import com.manna.databinding.DialogWelcomeBinding
import com.manna.presentation.make_meet.MeetRegisterActivity
import com.manna.util.ViewUtil
import dagger.hilt.android.AndroidEntryPoint

class WelcomeDialog : DialogFragment() {

    private lateinit var binding: DialogWelcomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_welcome, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tenDp = ViewUtil.convertDpToPixel(requireContext(), 10f)

        binding.panel1.background = GradientDrawable().apply {
            val array = floatArrayOf(tenDp, tenDp, tenDp, tenDp, 0f, 0f, 0f, 0f)
            cornerRadii = array
            setColor(Color.WHITE)
        }

        binding.panel2.background = GradientDrawable().apply {
            val array = floatArrayOf(0f, 0f, 0f, 0f, tenDp, tenDp, tenDp, tenDp)
            cornerRadii = array
            setColor(ContextCompat.getColor(requireContext(), R.color.keyColor))
        }
        binding.panel2.setOnClickListener {
            dismiss()
        }
    }
}


@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    private var selectedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ViewUtil.setStatusBarTransparent(this)

        if (intent?.getBooleanExtra(IS_WELCOME, false) == true) {
            WelcomeDialog().show(supportFragmentManager, "")
        }

        changeTab(0)
        binding.run {
            addButton.setOnClickListener {
                startActivity(MeetRegisterActivity.getIntent(this@HomeActivity))
            }

            meetListTab.setOnClickListener {
                changeTab(0)
            }

            profileTab.setOnClickListener {
                changeTab(1)
            }
        }
    }

    private fun changeTab(position: Int) {
        if (selectedPosition == position) return

        val prevFragment = supportFragmentManager.findFragmentByTag("$selectedPosition")
        if (prevFragment != null) {
            supportFragmentManager.commitNow {
                hide(prevFragment)
            }
        }

        val fragment = supportFragmentManager.findFragmentByTag("$position")
        if (fragment != null) {
            supportFragmentManager.commit {
                show(fragment)
            }
        } else {
            supportFragmentManager.commit {
                add(R.id.frag_container, getTabFragment(position), "$position")
            }
        }

        selectedPosition = position
    }


    private fun getTabFragment(position: Int): Fragment {
        return when (position) {
            0 -> MeetListFragment.newInstance()
            1 -> ProfileFragment.newInstance()
            else -> error("Invalid position")
        }
    }

    companion object {
        private const val IS_WELCOME = "is_welcome"

        fun getIntent(context: Context, isWelcome: Boolean = false) =
            Intent(context, HomeActivity::class.java).apply {
                putExtra(IS_WELCOME, isWelcome)
            }
    }
}