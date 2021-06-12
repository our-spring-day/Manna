package com.manna.presentation.apply

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.data.model.Apply
import com.manna.data.model.ApplyItem
import com.manna.data.model.UserItem
import com.manna.databinding.ActivityApplyListBinding

class ApplyListActivity : BaseActivity<ActivityApplyListBinding>(R.layout.activity_apply_list) {
    private val applyListAdapter = ApplyListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initAdapter()
    }

    private fun initView() {
        binding.run {
            layoutTitleBar.tvTitle.text = getString(R.string.apply_request)
            layoutTitleBar.ivBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initAdapter() {
        binding.rvApplyList.adapter = applyListAdapter
        val userList = arrayListOf<UserItem>()
        userList.add(UserItem("우석", ""))
        userList.add(UserItem("재인", ""))

        val applyList = arrayListOf<ApplyItem>()
        applyList.add(ApplyItem("1시", "서울시 강남구", "D-3", userList))
        applyList.add(ApplyItem("1시", "서울시 강서구", "D-1", userList))

        val list = mutableListOf<Apply>()
        applyList.forEach { applyItem ->
            applyItem.userList.forEachIndexed { index, userItem ->
                if (index == 0) {
                    list.add(applyItem)
                }
                list.add(userItem)
            }
        }
        applyListAdapter.addItem(list)
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, ApplyListActivity::class.java)
    }
}