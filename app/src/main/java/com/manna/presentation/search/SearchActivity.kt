package com.manna.presentation.search

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.databinding.library.baseAdapters.BR
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.common.BaseRecyclerViewAdapter
import com.manna.common.BaseRecyclerViewHolder
import com.manna.databinding.ActivitySearchBinding
import com.manna.databinding.ItemSearchAddressBinding
import com.naver.maps.geometry.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_search.*

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


            iv_search.setOnClickListener {
                viewModel.search(edt_keyword.text.toString())
            }
        }

    }
}
