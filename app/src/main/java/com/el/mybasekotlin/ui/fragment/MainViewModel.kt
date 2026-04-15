package com.el.mybasekotlin.ui.fragment

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import com.el.mybasekotlin.base.BaseViewModel
import com.el.mybasekotlin.data.model.ConfigResponse
import com.el.mybasekotlin.data.model.Setting
import com.el.mybasekotlin.data.model.game.GameData
import com.el.mybasekotlin.data.network.api.ApiHelper
import com.el.mybasekotlin.data.state.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val apiHelper: ApiHelper
) : BaseViewModel(application, apiHelper) {
    private val _listSetting = MutableStateFlow<DataState<MutableList<Setting>>>(DataState.Empty)
    val listSetting: StateFlow<DataState<MutableList<Setting>>> = _listSetting.asStateFlow()

    private val _configApp = MutableStateFlow<DataState<ConfigResponse>>(DataState.Empty)
    val configApp: StateFlow<DataState<ConfigResponse>> = _configApp.asStateFlow()

    val combinedFlow = _listSetting.combine(_configApp) { value1, value2 ->
//        "Combined: $value1 and $value2"
        value1 to value2
    }
    val mergeData = _listSetting.combine(_configApp) { value1, value2 ->
//        "Combined: $value1 and $value2"
        value1 to value2
    }
    val zipFlow = _listSetting.zip(_configApp) { value1, value2 ->
//        "Combined: $value1 and $value2"
        value1 to value2
    }


    fun demo2Api() {
        launchJobCustom(coroutineException(_listSetting)) {
            _listSetting.value = DataState.Loading
            apiHelper.getSetting().flowOn(Dispatchers.IO)
                .catch { e -> flowCatch(e, _listSetting) }
                .collect {
                    if (it.isSuccess) {
                        Timber.d("Init home screen data success")
                        it.data?.let { data ->
                            _listSetting.value = DataState.Success(data)
                            _listSetting.value = DataState.Empty

                        }
                    } else {
                        Timber.d("Init home screen data fail")
                        errorCatch(it, _listSetting)
                    }
                }
        }
    }


    fun getSettingApp() {
        Timber.i("Loading event list data")
        launchJobCustom(coroutineException(_listSetting)) {
            _listSetting.value = DataState.Loading
            apiHelper.getSetting().flowOn(Dispatchers.IO)
                .catch { e -> flowCatch(e, _listSetting) }
                .collect {
                    if (it.isSuccess) {
                        Timber.d("Init home screen data success")
                        it.data?.let { data ->
                            _listSetting.value = DataState.Success(data)
                            _listSetting.value = DataState.Empty

                        }
                    } else {
                        Timber.d("Init home screen data fail")
                        errorCatch(it, _listSetting)
                    }
                }
        }
    }

    fun getConfigApp() {
        Timber.i("Loading config")
        launchJobCustom(coroutineException(_configApp)) {
            _configApp.value = DataState.Loading
            apiHelper.configApp().flowOn(Dispatchers.IO)
                .catch { e -> flowCatch(e, _configApp) }
                .collect {

                    _configApp.value = DataState.Success(it)
                    _configApp.value = DataState.Empty
                }
        }

    }

    private val _getListGameFilterState =
        MutableStateFlow<DataState<MutableList<GameData>>>(DataState.Empty)// listener state
    private val _dataAllGameFilter = MutableStateFlow(mutableStateListOf<GameData>())
    val dataAllGame = _dataAllGameFilter.asStateFlow()
    fun getAllDataGame() {
        Toast.makeText(application, "QuangDV getAllDataGame", Toast.LENGTH_SHORT).show()
        Timber.d("QuangDV getAllDataGame")
        launchJob {

            apiHelper.getAllGame().flowOn(Dispatchers.IO).catch { e -> }.collect {
                _getListGameFilterState.value = DataState.Success(it.data ?: arrayListOf())
                _dataAllGameFilter.value.clear()
//                    val shuffledList = (it.data ?: arrayListOf()).shuffled()

                // Add list đã xáo trộn
//                    _dataAllGameFilter.value.addAll(shuffledList)
                val sortedGames =
                    it.data?.filter { data -> data.enable }?.sortedBy { data -> data.pos }

                _dataAllGameFilter.value.addAll(sortedGames ?: arrayListOf())
                Timber.d("QuangDV getALlGame: success ${it.data}!")
            }
        }

    }


//    fun getALlGame() {
//        launchJobCustom(coroutineException(_getListGameFilterState)) {
//            _getListGameFilterState.value = DataState.Loading
//            apiHelper.getAllGame().flowOn(Dispatchers.IO).catch { e ->
//                flowCatch(e, _getListGameFilterState)
//            }.collect {
//                if (it.isSuccess) {
//                    _getListGameFilterState.value = DataState.Success(it.data ?: arrayListOf())
//                    _dataAllGameFilter.value.clear()
////                    val shuffledList = (it.data ?: arrayListOf()).shuffled()
//
//                    // Add list đã xáo trộn
////                    _dataAllGameFilter.value.addAll(shuffledList)
//                    val sortedGames =
//                        it.data?.filter { data -> data.enable }?.sortedBy { data -> data.pos }
//
//                    _dataAllGameFilter.value.addAll(sortedGames ?: arrayListOf())
//                    Timber.d("QuangDV getALlGame: success ${it.data}!")
//                    Timber.d("QuangDV getALlGame: success ${it.data?.size}!")
//                } else {
//                    _getListGameFilterState.value =
//                        DataState.Error(it.errorCode.toString(), it.message)
//                    Timber.d("QuangDV getALlGame: error! ${it.message}")
//                }
//                delay(100)
//                _getListGameFilterState.value = DataState.Empty
//            }
//        }
//    }
}


enum class TypeUpdate {
    GO_MAIN, AUTO_UPDATE, MAYBE_UPDATE
}
