package com.manna.presentation.sign_up

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.manna.R
import com.manna.common.BaseFragment
import com.manna.databinding.FragmentCreateNameBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.regex.Pattern

@AndroidEntryPoint
class CreateNameFragment : BaseFragment<FragmentCreateNameBinding>(R.layout.fragment_create_name) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initButton()

        binding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                binding.ivClear.visibility = View.VISIBLE
                binding.ivError.visibility = View.VISIBLE
                checkNickname()
            }
        })
    }

    private fun initButton() {
        binding.ivBack.setOnClickListener {

        }

        binding.ivClear.setOnClickListener {
            binding.ivClear.visibility = View.GONE
            binding.tvError.text = ""
            binding.ivError.visibility = View.GONE
            binding.edtName.text.clear()
        }

        binding.btnNext.setOnClickListener {
            (activity as SignUpActivity).replace(ProfileGuideFragment())
        }
    }

    fun checkNickname() {
        val pattern = Pattern.compile("^[가-힣]{1,6}")
        val matcher = pattern.matcher(binding.edtName.text.toString())
        if (!matcher.matches()) {
            val pattern = Pattern.compile("^[ㄱ-ㅎㅏ-ㅣ]")
            val matcher = pattern.matcher(binding.edtName.text.toString())
            when {
                matcher.matches() -> {
                    binding.tvError.text = "자음, 모음은 불가능합니다."
                }
                binding.edtName.text.length > 6 -> {
                    binding.tvError.text = "6자 이내로 입력해주세요."
                }
                else -> {
                    binding.tvError.text = "한글만 입력 가능합니다."
                }
            }
            binding.tvError.setTextColor(ContextCompat.getColor(requireContext(), R.color.ff0000))
            binding.ivError.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_error
                )
            )
        } else {
            binding.tvError.text = "사용 가능합니다."
            binding.tvError.setTextColor(ContextCompat.getColor(requireContext(), R.color.keyColor))
            binding.ivError.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_available
                )
            )
        }
    }

    companion object {
        fun newInstance() = CreateNameFragment()
    }
}