package com.manna.network.model.meet


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MeetResponseItem(
    @SerializedName("manna_name")
    val mannaName: String?,
    @SerializedName("create_timestamp")
    val createTimestamp: Long?,
    @SerializedName("uuid")
    val uuid: String?
) : Parcelable