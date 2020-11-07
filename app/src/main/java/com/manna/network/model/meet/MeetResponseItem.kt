package com.manna.network.model.meet


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MeetResponseItem(
    @SerializedName("mannaName")
    val mannaName: String?,
    @SerializedName("createTimestamp")
    val createTimestamp: Long?,
    @SerializedName("uuid")
    val uuid: String?,
    @SerializedName("locationJoinUserList")
    val locationJoinUserList: String?,
    @SerializedName("chatJoinUserList")
    val chatJoinUserList: String?,
) : Parcelable