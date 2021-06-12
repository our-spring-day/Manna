package com.manna.presentation.alert

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.data.model.AlertItem
import com.manna.databinding.ActivityAlertListBinding

class AlertListActivity : BaseActivity<ActivityAlertListBinding>(R.layout.activity_alert_list) {
    private val alertListAdapter = AlertListAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initAdapter()
    }

    private fun initView() {
        binding.run {
            layoutTitleBar.tvTitle.text = getString(R.string.alert)
            layoutTitleBar.ivBack.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun initAdapter() {
        binding.rvAlertList.adapter = alertListAdapter

        val alertList = arrayListOf<AlertItem>()
        alertList.add(AlertItem("21.05.05", "정재인님 도착", "지도 열기"))
        alertList.add(AlertItem("21.05.10", "원우석님 도착", "자세히"))

        alertListAdapter.addItem(alertList)
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, AlertListActivity::class.java)
    }
}