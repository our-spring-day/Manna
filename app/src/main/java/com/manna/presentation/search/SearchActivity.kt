package com.manna.presentation.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.databinding.library.baseAdapters.BR
import com.manna.R
import com.manna.databinding.ActivitySearchBinding
import com.manna.databinding.ItemSearchAddressBinding
import com.naver.maps.geometry.LatLng
import com.wswon.picker.common.BaseActivity
import com.wswon.picker.common.BaseRecyclerViewAdapter
import com.wswon.picker.common.BaseRecyclerViewHolder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>(R.layout.activity_search) {

    private val viewModel by viewModels<SearchViewModel>()

    private val addressAdapter by lazy {
        object :
            BaseRecyclerViewAdapter<SearchAddressItem, ItemSearchAddressBinding, BaseRecyclerViewHolder<ItemSearchAddressBinding>>(
                R.layout.item_search_address,
                variableId = BR.item
            ) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE  or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        viewModel.run {
            addressItems.observe(this@SearchActivity, {
                addressAdapter.replaceAll(it)
            })

            clickItem.observe(this@SearchActivity, {
                startActivity(
                    RouteActivity.getIntent(
                        this@SearchActivity,
                        LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                    )
                )
            })
        }

        binding.run {
            rvAddressList.run {
                adapter = addressAdapter
            }
            edtKeyword.setOnEditorActionListener { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        viewModel.search(edtKeyword.text.toString())
                    }
                }
                true
            }
        }
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, SearchActivity::class.java)
    }
}
