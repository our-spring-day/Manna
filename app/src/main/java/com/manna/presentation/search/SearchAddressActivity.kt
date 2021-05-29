package com.manna.presentation.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.common.Logger
import com.manna.databinding.ActivitySearchAddressBinding
import com.manna.ext.openKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class SearchAddressActivity :
    BaseActivity<ActivitySearchAddressBinding>(R.layout.activity_search_address) {

    private val viewModel by viewModels<SearchViewModel>()

    private val addressAdapter by lazy {
        SearchAddressAdapter()
    }

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.run {
            addressPagingData.observe(this@SearchAddressActivity, { pagingData ->
                Logger.d("$pagingData")
                pagingData
                    .subscribe({
                        addressAdapter.submitData(lifecycle, it)
                    }, {
                        Logger.d("$it")
                    })
            })

            clickItem.observe(this@SearchAddressActivity, {
                val data = Intent().putExtra(ADDRESS_ITEM, SearchAddressResult.of(it))
                setResult(Activity.RESULT_OK, data)
                finish()

//                startActivity(
//                    RouteActivity.getIntent(
//                        this@SearchAddressActivity,
//                        LatLng(it.latitude.toDouble(), it.longitude.toDouble())
//                    )
//                )
            })
        }

        binding.run {
            btnBack.setOnClickListener {
                onBackPressed()
            }
            rvAddressList.run {
                adapter = addressAdapter
            }

            edtKeyword.openKeyboard()
            edtKeyword.doOnTextChanged { text, _, _, _ ->
                viewModel.search(text.toString())
            }
            edtKeyword.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {
                        val keyword = edtKeyword.text.toString()
                        viewModel.search(keyword)
                    }
                }
                true
            }
        }
    }

    companion object {
        const val ADDRESS_ITEM = "address_item"

        fun getIntent(context: Context) =
            Intent(context, SearchAddressActivity::class.java)
    }
}
