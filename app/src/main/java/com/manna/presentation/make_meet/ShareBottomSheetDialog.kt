package com.manna.presentation.make_meet

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import com.manna.R
import com.manna.common.Logger
import com.manna.databinding.DialogShareBottomSheetBinding
import com.manna.databinding.ItemShareBottomSheetBinding
import com.manna.ext.toast
import com.manna.util.ViewUtil
import java.util.*

data class Entry(
    @DrawableRes val iconResId: Int,
    val name: String
)

class ShareBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: DialogShareBottomSheetBinding? = null
    private val binding: DialogShareBottomSheetBinding
        get() = _binding!!


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).also { dialog ->
            dialog.setOnShowListener {
                val bottomSheet =
                    (dialog as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                if (bottomSheet != null) {
                    BottomSheetBehavior.from(bottomSheet).run {
                        state = BottomSheetBehavior.STATE_EXPANDED
                        skipCollapsed = true
                        isHideable = true
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogShareBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
    }

    private fun setupView() {
        val adapter = ShareBottomSheetAdapter(::onClickEntry)

        val entryList = getEntryList()
        adapter.replaceAll(entryList)

        with(binding) {
            list.adapter = adapter
            close.setOnClickListener {
                dismiss()
            }
            root.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun getEntryList(): List<Entry> =
        mutableListOf<Entry>().apply {
            if (LinkClient.instance.isKakaoLinkAvailable(requireContext())) {
                add(Entry(R.drawable.ic_share_kakao, "카카오톡"))
            }
            add(Entry(R.drawable.ic_share_message, "메시지"))
            add(Entry(R.drawable.ic_share_link, "링크복사"))
        }

    private fun onClickEntry(entry: Entry) {
        createLink("원우석") { title, description, link ->
            when (entry.iconResId) {
                R.drawable.ic_share_kakao -> {
                    shareKakaoLink(
                        title = title,
                        description = description,
                        url = link
                    )
                }

                R.drawable.ic_share_message -> {
                    shareSMS(link)
                }
                R.drawable.ic_share_link -> {
                    copyToClipBoard(link)
                }
            }
        }
    }


    private fun createLink(
        name: String,
        complete: (title: String, description: String, link: String) -> Unit
    ) {
        val title = "${name}님의 약속 초대장"
        val description = "외않와로 모이면 지각자가 없어진다!"

        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://whynotcoming.com/invite")
            domainUriPrefix = "https://whynotcoming.page.link"
            // Open links with this app on Android
            androidParameters {

            }

            socialMetaTagParameters {
                this.title = title
                this.description = description
                imageUrl =
                    Uri.parse("https://user-images.githubusercontent.com/48197016/109977857-9b312400-7d40-11eb-9c37-59594786089c.png")
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            Logger.d("shortLink $shortLink")
            complete(title, description, shortLink.toString())
        }.addOnFailureListener {
            Logger.d("$it")
        }
    }

    private fun shareKakaoLink(title: String, description: String, url: String) {
        val defaultFeed = FeedTemplate(
            content = Content(
                title = title,
                description = description,
                imageUrl = "https://user-images.githubusercontent.com/48197016/109977857-9b312400-7d40-11eb-9c37-59594786089c.png",
                link = Link(
                    webUrl = url,
                    mobileWebUrl = url
                ),
                imageWidth = 270,
                imageHeight = 135
            ),
            buttons = listOf(
                Button(
                    "약속 확인하기",
                    Link(
                        webUrl = url,
                        mobileWebUrl = url
                    )
                )
            )
        )

        LinkClient.instance.defaultTemplate(requireContext(), defaultFeed) { linkResult, error ->
            if (error != null) {
                Logger.d("카카오링크 보내기 실패 $error")
            } else if (linkResult != null) {
                Logger.d("카카오링크 보내기 성공 ${linkResult.intent}")
                startActivity(linkResult.intent)

                // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                Logger.d("Warning Msg: ${linkResult.warningMsg}")
                Logger.d("Argument Msg: ${linkResult.argumentMsg}")
            }
        }
    }

    private fun shareSMS(url: String) {
        try {
            context?.let {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    putExtra(
                        "sms_body",
                        url
                    )
                    data = Uri.parse("sms:")
                }

                it.startActivity(intent)
            }
        } catch (e: Exception) {
            Logger.e(e.message.orEmpty())
        } finally {

        }
    }

    private fun copyToClipBoard(url: String) {
        try {
            context?.let {
                val clipboardManager: ClipboardManager =
                    it.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                val clip: ClipData = ClipData.newPlainText("message", url)
                clipboardManager.setPrimaryClip(clip)

                toast("복사완료!\n이제 공유만 하시면 되겠네요.")
            }

        } catch (e: Exception) {
            Logger.d(e.message.orEmpty())
        } finally {

        }
    }

    companion object {
        fun newInstance(): ShareBottomSheetDialog =
            ShareBottomSheetDialog()
    }
}

class ShareBottomSheetAdapter(
    private val onItemClick: (Entry) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<Entry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ShareBottomSheetViewHolder(parent, onItemClick = onItemClick)

    override fun getItemCount(): Int =
        items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ShareBottomSheetViewHolder).bind(items[position])
    }

    fun replaceAll(items: List<Entry>) {
        this.items.clear()
        this.items.addAll(items)
        notifyDataSetChanged()
    }
}

class ShareBottomSheetViewHolder(
    parent: ViewGroup,
    private val binding: ItemShareBottomSheetBinding =
        ItemShareBottomSheetBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
    private val onItemClick: (Entry) -> Unit
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Entry) {
        val context = itemView.context

        with(binding) {
            binding.name.text = item.name
            val bitmapIcon = getBitmapFromVector(context, item.iconResId)
            val roundedIcon = getRoundedCornerBitmap(
                bitmapIcon,
                ViewUtil.convertDpToPixel(context, 4f)
            )
            icon.setImageBitmap(roundedIcon)
        }

        itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    private fun getBitmapFromVector(context: Context, drawableId: Int): Bitmap {
        val drawable = AppCompatResources.getDrawable(context, drawableId)

        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Float): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true
        canvas.drawRoundRect(rectF, pixels, pixels, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        bitmap.recycle()
        return output
    }
}
