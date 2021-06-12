package com.manna.presentation.meet_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.manna.data.source.repo.MeetRepository
import io.reactivex.disposables.CompositeDisposable

class MeetListViewModel @ViewModelInject constructor(private val repository: MeetRepository) :
    ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val _meetList = MutableLiveData<List<MeetListItem>>()
    val meetList: LiveData<List<MeetListItem>>
        get() = _meetList


    fun getMeetList(deviceId: String) {
//        compositeDisposable += repository.getMeetList(deviceId)
//            .subscribeOn(Schedulers.io())
//            .map {
//                it.map {
//                    it.toMeetListItem()
//                }
//            }
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({
//                _meetList.value = it
//            }, {
//                Logger.d("$it")
//            })


        val meetList = listOf(
            MeetListItem.MeetItem(
                uuid = "roomId",
                meetName = "테스트용 아이템",
                createTimestamp = System.currentTimeMillis(),
                locationJoinUserList = "",
                chatJoinUserList = ""
            ),
            MeetListItem.DateTitleItem(
                dateTitle = "이번 주"
            ),
            MeetListItem.MeetItem(
                uuid = "roomId",
                meetName = "테스트용 아이템",
                createTimestamp = System.currentTimeMillis(),
                locationJoinUserList = "",
                chatJoinUserList = ""
            ),
            MeetListItem.DateTitleItem(
                dateTitle = "다음 주 부터"
            ),
            MeetListItem.MeetItem(
                uuid = "roomId",
                meetName = "테스트용 아이템",
                createTimestamp = System.currentTimeMillis(),
                locationJoinUserList = "",
                chatJoinUserList = ""
            )
        )

        _meetList.value = meetList
    }
}
