package com.manna.presentation.sign_up

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentCreateNameBinding
import com.manna.ext.closeKeyboard
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class CreateNameFragment : BaseFragment<FragmentCreateNameBinding>(R.layout.fragment_create_name) {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                requireActivity().finish()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.run {
            layoutTitleBar.tvTitle.text = getString(R.string.sign_up_create_name_title)
            layoutTitleBar.ivBack.setOnClickListener {
                requireActivity().finish()
            }
            ivClear.setOnClickListener {
                clearEditText()
            }
            tvNext.setOnClickListener {
                binding.edtName.closeKeyboard()

                parentFragmentManager.beginTransaction().hide(this@CreateNameFragment).commit()
                val fragment = ProfileGuideFragment.newInstance()
                parentFragmentManager.beginTransaction()
                    .add(R.id.fl_sign_up, fragment, fragment::class.java.simpleName).commit()
            }
            edtName.doAfterTextChanged {
                if (binding.edtName.text.isNotEmpty()) {
                    binding.ivClear.visibility = View.VISIBLE
                    binding.tvError.visibility = View.VISIBLE
                    binding.ivError.visibility = View.VISIBLE
                    checkNickname()
                } else {
                    clearEditText()
                }
            }
        }
    }

    private fun clearEditText() {
        binding.run {
            ivClear.visibility = View.GONE
            tvError.text = ""
            ivError.visibility = View.GONE
            edtName.text.clear()
            tvNext.isEnabled = false
            tvNext.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_gray)
        }
    }

    private fun checkNickname() {
        val pattern = Pattern.compile("^[가-힣]{1,6}")
        val matcher = pattern.matcher(binding.edtName.text.toString())
        if (!matcher.matches()) {
            val pattern = Pattern.compile(".*[ㄱ-ㅎㅏ-ㅣ].*")
            val matcher = pattern.matcher(binding.edtName.text.toString())
            when {
                matcher.matches() -> {
                    binding.tvError.text =
                        getString(R.string.sign_up_create_name_error_message_consonant_vowel)
                }
                binding.edtName.text.length > 6 -> {
                    binding.tvError.text =
                        getString(R.string.sign_up_create_name_error_message_length)
                }
                else -> {
                    binding.tvError.text =
                        getString(R.string.sign_up_create_name_error_message_hangul)
                }
            }
            binding.run {
                tvError.setTextColor(ContextCompat.getColor(requireContext(), R.color.ff0000))
                ivError.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_error
                    )
                )
                tvNext.isEnabled = false
                tvNext.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_gray)
            }
        } else {
            binding.run {
                tvError.text = getString(R.string.sign_up_create_name_available_message)
                tvError.setTextColor(ContextCompat.getColor(requireContext(), R.color.keyColor))
                ivError.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_available
                    )
                )
                tvNext.isEnabled = true
                tvNext.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_blue)
            }
        }
    }

    companion object {
        fun newInstance() = CreateNameFragment()
    }
}