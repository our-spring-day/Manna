package com.manna.presentation.meet_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.manna.data.source.repo.MeetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class MeetListViewModel @Inject constructor(private val repository: MeetRepository) :
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
            MeetListItem.Header(
                "오늘 1개의\n약속이 있어요!",
                isNewApply = true,
                isNewAlert = true
            ),
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
