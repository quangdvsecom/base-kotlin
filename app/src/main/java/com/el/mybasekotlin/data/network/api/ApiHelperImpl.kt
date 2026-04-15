package com.el.mybasekotlin.data.network.api

import com.el.mybasekotlin.data.model.ConfigResponse
import com.el.mybasekotlin.data.model.Setting
import com.el.mybasekotlin.data.model.game.GameData
import com.el.mybasekotlin.data.response.BaseDataResponse
import com.el.mybasekotlin.helpers.JsonAssetController
import com.el.mybasekotlin.utils.extension.parseJsonList
import com.funface.battle.challenge.data.local.assetConfig.AssetFileName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ElChuanmen on 1/16/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@Singleton
open class ApiHelperImpl @Inject constructor(
    private val apiService: ApiService,
    private val apiConfigService: ApiConfigService,

    private val jsonAssetController: JsonAssetController,
) : ApiHelper {
override fun getSetting(): Flow<BaseDataResponse<MutableList<Setting>>> {
    return flow { emit(apiService.getSetting()) }
}
    override fun getListNotices(page: Int) = flow { emit(apiService.getListNotice(page)) }
    override fun configApp(): Flow<ConfigResponse> = flow { emit(apiConfigService.configApp()) }
    override fun getAllGame(): Flow<BaseDataResponse<MutableList<GameData>>> = flow {
        val jsonObject = jsonAssetController.readJsonFile(AssetFileName.ALL_GAME)
        if (jsonObject != null) {

            val dataArray = jsonObject.getJSONArray("data")

            val games: List<GameData> = parseJsonList<GameData>(dataArray.toString())
            emit(
                BaseDataResponse(
                    status = 1, data = games.toMutableList()
                )
            )
        } else {
            Timber.d("QuangDV getListGame lỗi m rồi! Json null")
            emit(
                BaseDataResponse(
                    status = 0,
                    message = "File not found or invalid JSON",
                    data = mutableListOf()
                )
            )
        }
    }
}