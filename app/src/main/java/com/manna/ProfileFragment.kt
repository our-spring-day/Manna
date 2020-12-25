package com.manna

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.manna.databinding.FragmentProfileBinding
import com.manna.databinding.ItemFriendsBinding
import com.manna.ext.setImage
import com.manna.util.ViewUtil
import com.wswon.picker.ImagePickerFragment
import com.wswon.picker.common.BaseFragment


class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val adapter by lazy {
        FriendsAdapter {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileImage.post {
            binding.profileImage.setImage("http://mimg.segye.com/content/image/2020/03/12/20200312506832.jpg")
        }

        with(binding) {
            profileHeader.updatePadding(top = ViewUtil.getStatusBarHeight(requireContext()))
            profileButton.setOnClickListener {
                showImagePicker()
            }

            friendsList.adapter = adapter
            friendsList.addItemDecoration(getDecoration())

            adapter.submitList((0..10).map {
                FriendsItem(
                    "펭수",
                    "http://mimg.segye.com/content/image/2020/03/12/20200312506832.jpg",
                    "$it"
                )
            })

            friendsCount.text = "${adapter.currentList.size}"
        }
    }

    private fun getDecoration() : RecyclerView.ItemDecoration =
        object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)

                outRect.bottom = ViewUtil.convertDpToPixel(requireContext(), 14f).toInt()
            }
        }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_IMAGE_PICKER -> {
                if (resultCode == Activity.RESULT_OK) {
                    val imageUri =
                        data?.getParcelableExtra<Uri>(ImagePickerFragment.ARG_IMAGE_URI)
                    if (imageUri != null) {
                        binding.profileImage.setImage(imageUri)
                    }
                }
            }
        }
    }

    private fun showImagePicker() {
        val imagePickerFragment = ImagePickerFragment()
        imagePickerFragment.setTargetFragment(this, REQ_IMAGE_PICKER)
        imagePickerFragment.show(parentFragmentManager, DIALOG_TAG)
    }

    companion object {
        fun newInstance() =
            ProfileFragment()

        private const val REQ_IMAGE_PICKER = 100
        private const val DIALOG_TAG = "IMAGE_PICKER"
    }
}

data class FriendsItem(
    val name: String,
    val image: String,
    val code: String
)

class FriendsAdapter(private val onClick: (FriendsItem) -> Unit) :
    ListAdapter<FriendsItem, FriendsViewHolder>(
        object : DiffUtil.ItemCallback<FriendsItem>() {
            override fun areItemsTheSame(oldItem: FriendsItem, newItem: FriendsItem): Boolean =
                oldItem.code == newItem.code

            override fun areContentsTheSame(oldItem: FriendsItem, newItem: FriendsItem): Boolean =
                oldItem == newItem

        }
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder =
        FriendsViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_friends, parent, false),
            onClick
        )

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}

class FriendsViewHolder(view: View, private val onClick: (FriendsItem) -> Unit) :
    RecyclerView.ViewHolder(view) {
    private val binding = DataBindingUtil.bind<ItemFriendsBinding>(itemView)!!

    fun bind(item: FriendsItem) {
        with(binding) {
            root.setOnClickListener {
                onClick(item)
            }

            name.text = item.name
            profileImage.setImage(item.image)
        }
    }
}