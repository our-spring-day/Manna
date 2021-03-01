package com.manna.presentation.settings

import android.os.Bundle
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityNoticeDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoticeDetailActivity :
    BaseActivity<ActivityNoticeDetailBinding>(R.layout.activity_notice_detail) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.run {
            layoutTitleBar.tvTitle.text = "공지사항"
            tvContent.text = "안녕하세요 외않와팀입니다.\n" +
                "모든 국민은 신체의 자유를 가진다. 누구든지 법률에 의하지 아니하고는 체포·구속·압수·수색 또는 심문을 받지 아니하며, 법률과 적법한 절차에 의하지 아니하고는 처벌·보안처분 또는 강제노역을 받지 아니한다.\n" +
                "\n" +
                "연소자의 근로는 특별한 보호를 받는다. 국토와 자원은 국가의 보호를 받으며, 국가는 그 균형있는 개발과 이용을 위하여 필요한 계획을 수립한다. 교육의 자주성·전문성·정치적 중립성 및 대학의 자율성은 법률이 정하는 바에 의하여 보장된다.\n" +
                "\n" +
                "감사합니다.\n" +
                "외않와팀 드림"
            tvDate.text = "2020월 3월 2일"
            layoutTitleBar.ivBack.setOnClickListener {
                finish()
            }
        }
    }
}