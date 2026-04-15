package com.el.mybasekotlin.ui.fragment.home

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draganddrop.DragAndDropModifierNode
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.load.engine.Resource
import com.el.mybasekotlin.base.BaseViewModel
import com.el.mybasekotlin.data.model.ConfigResponse
import com.el.mybasekotlin.data.model.Notice
import com.el.mybasekotlin.data.model.Setting
import com.el.mybasekotlin.data.network.api.ApiHelper
import com.el.mybasekotlin.data.state.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val application: Application,
    private val apiHelper: ApiHelper
) : BaseViewModel(application, apiHelper) {


}

