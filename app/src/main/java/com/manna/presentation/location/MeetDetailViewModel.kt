package com.manna.presentation.location

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.manna.Logger
import com.manna.UserHolder
import com.manna.common.BaseViewModel
import com.manna.ext.Event
import com.manna.ext.plusAssign
import com.manna.network.api.BingApi
import com.manna.network.api.MeetApi
import com.manna.presentation.User
import com.naver.maps.geometry.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MeetDetailViewModel @ViewModelInject constructor(
    private val repository: BingApi,
    private val meetApi: MeetApi
) :
    BaseViewModel() {

    private val _drawWayPoints = MutableLiveData<List<WayPoint>>()
    val drawWayPoints: LiveData<List<WayPoint>> get() = _drawWayPoints

    val remainValue = MutableLiveData<Pair<User, Pair<Double?, Int?>>>()

    val userList = MutableLiveData<List<User>>()

    val bottomUserItemClickEvent = MutableLiveData<Event<User>>()

    fun submitUserList(userList: List<User>) {
        this.userList.value = userList
    }

    fun addUser(user: User) {
        this.userList.value = userList.value.orEmpty() + user
    }

    fun findRoute(user: User, startPoint: WayPoint, endPoint: WayPoint) {
        compositeDisposable += repository
            .getRoute(
                startLatLng = startPoint.getPoint(),
                endLatLng = endPoint.getPoint()
            )
            .subscribeOn(Schedulers.io())
            .map { root ->
                val items =
                    root.resourceSets?.first()?.resources?.first()?.routeLegs?.first()?.itineraryItems

                val paths =
                    root.resourceSets?.first()?.resources?.first()?.routePath?.line?.coordinates

                val points = mutableListOf<WayPoint>()

                val remainDistance =
                    root.resourceSets?.first()?.resources?.first()?.routeLegs?.first()?.travelDistance

                val remainTime =
                    root.resourceSets?.first()?.resources?.first()?.routeLegs?.first()?.travelDuration

                val remainValue = user to (remainDistance to remainTime)
                this.remainValue.postValue(remainValue)

                paths?.forEach { path ->
                    if (path.size > 1) {
                        points.add(WayPoint(LatLng(path[0], path[1]), ""))
                    }
                }

                val titles = mutableListOf<String>()

                items?.forEach {
                    val mode = it.details?.first()?.mode
                    val title = it.instruction?.text

                    if (!title.isNullOrEmpty()) {
                        titles.add(title)
                    }
                    val childTitles = it.childItineraryItems?.mapNotNull {
                        it.instruction?.text
                    }
                    if (childTitles != null) {
                        titles.addAll(childTitles)
                    }

                    if (mode == "Transit") {
                        Logger.d("$it")
                    }
                }

                points to titles
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ (list, titles) ->
                _drawWayPoints.value = list
            }, {
                Logger.d("$it")
            })
    }

    fun urgingUser(roomId: String, receiverToken: String) {
        compositeDisposable += meetApi.sendPushMessage(
            roomId,
            receiverToken,
            UserHolder.deviceId
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Logger.d("$it")
            }, {
                Logger.d("$it")
            })
    }
}