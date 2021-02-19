package com.manna

import android.Manifest
import android.app.Activity
import android.content.Intent
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
import com.gun0912.tedpermission.TedPermissionResult
import com.manna.common.BaseFragment
import com.manna.common.plusAssign
import com.manna.databinding.FragmentProfileBinding
import com.manna.databinding.ItemFriendsBinding
import com.manna.ext.setImage
import com.manna.ext.toast
import com.manna.presentation.settings.DeleteAccountActivity
import com.manna.presentation.settings.FeedbackActivity
import com.manna.presentation.settings.NoticeActivity
import com.manna.util.ViewUtil
import com.tedpark.tedpermission.rx2.TedRx2Permission
import com.wswon.picker.ImagePickerFragment


class ProfileFragment : BaseFragment<FragmentProfileBinding>(R.layout.fragment_profile) {

    private val adapter by lazy {
        FriendsAdapter {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupView()
        setupViewModel()
    }

    private fun setupView() {
        with(binding) {
            profileHeader.updatePadding(top = ViewUtil.getStatusBarHeight(requireContext()))

            profileImage.setImage("http://mimg.segye.com/content/image/2020/03/12/20200312506832.jpg")
            profileImage.setOnClickListener {
                showImagePicker()
            }

            notice.setOnClickListener {
                startActivity(Intent(requireContext(), NoticeActivity::class.java))
            }
            questions.setOnClickListener {
                startActivity(Intent(requireContext(), FeedbackActivity::class.java))
            }
            storeEvaluate.setOnClickListener {

            }
            setupAlarm.setOnClickListener {

            }
            terms.setOnClickListener {

            }
            mapInfoProvider.setOnClickListener {

            }
            appVersion.setOnClickListener {

            }
            logout.setOnClickListener {

            }
            leave.setOnClickListener {
                startActivity(Intent(requireContext(), DeleteAccountActivity::class.java))
            }
        }
    }

    private fun setupViewModel() {

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
        checkPermissions {
            val imagePickerFragment = ImagePickerFragment()
            imagePickerFragment.setTargetFragment(this, REQ_IMAGE_PICKER)
            imagePickerFragment.show(
                parentFragmentManager,
                imagePickerFragment::class.java.simpleName
            )
        }
    }

    private fun checkPermissions(success: () -> Unit) {
        compositeDisposable += TedRx2Permission.with(requireContext())
            .setRationaleTitle("저장소 접근 권한")
            .setRationaleMessage("사진을 가져오기 위해 권한에 동의해 주세요.")
            .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .request()
            .subscribe({ tedPermissionResult: TedPermissionResult ->
                if (tedPermissionResult.isGranted) {
                    success()
                } else {
                    toast("허용되지 않은 권한 : ${tedPermissionResult.deniedPermissions}")
                }
            }, { throwable: Throwable? ->

            })
    }

    companion object {
        private const val REQ_IMAGE_PICKER = 100

        fun newInstance() =
            ProfileFragment()
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