package com.manna.network.model.meet


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.manna.presentation.meet_list.MeetListItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class MeetResponseItem(
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("mannaName")
    val mannaName: String?,
    @SerializedName("createTimestamp")
    val createTimestamp: Long?,
    @SerializedName("locationJoinUserList")
    val locationJoinUserList: String?,
    @SerializedName("chatJoinUserList")
    val chatJoinUserList: String?,
) : Parcelable {

    fun toMeetListItem(): MeetListItem.MeetItem =
        MeetListItem.MeetItem(
            uuid = uuid.orEmpty(),
            meetName = mannaName.orEmpty(),
            createTimestamp = createTimestamp ?: -1L,
            locationJoinUserList = uuid.orEmpty(),
            chatJoinUserList = chatJoinUserList.orEmpty()
        )
}