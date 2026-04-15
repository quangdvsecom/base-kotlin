package com.el.mybasekotlin.compose

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bumptech.glide.load.engine.Resource
import com.el.mybasekotlin.base.BaseViewModel
import com.el.mybasekotlin.data.model.Notice
import com.el.mybasekotlin.data.network.api.ApiHelper
import com.el.mybasekotlin.data.state.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Created by ElChuanmen on 3/19/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@HiltViewModel
class NoticeViewmodel@Inject constructor(
    private val application: Application,
    private val apiHelper: ApiHelper,
) : BaseViewModel(application, apiHelper) {
//    val getListNoticeState: StateFlow<DataState<SnapshotStateList<Notice>>> = _noticeList.asStateFlow()
    private val _listNoticeState =
        MutableStateFlow<DataState<MutableList<Notice>>>(DataState.Empty)// listener state
    val stateNotice: StateFlow<DataState<MutableList<Notice>>> = _listNoticeState.asStateFlow()
    private val _dataNotice = mutableStateListOf<Notice>()
    val dataNotice: SnapshotStateList<Notice> get() = _dataNotice

    fun  updateNoticeType(){
//        _dataNotice.

    }
}