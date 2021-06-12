package com.manna.data.model

data class ApplyItem(
    val date: String,
    val location: String,
    val dDay: String,
    val userList: ArrayList<UserItem>
): Apply()

data class UserItem(
    val nickname: String,
    val image: String
): Apply()

sealed class Apply