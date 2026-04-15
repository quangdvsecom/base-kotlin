package com.el.mybasekotlin.data.network.api

import com.el.mybasekotlin.data.model.ConfigResponse
import com.el.mybasekotlin.data.model.Setting
import com.el.mybasekotlin.data.response.BaseDataResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    private val apiConfigService: ApiConfigService
) : ApiHelper {
override fun getSetting(): Flow<BaseDataResponse<MutableList<Setting>>> {
    return flow { emit(apiService.getSetting()) }
}
    override fun getListNotices(page: Int) = flow { emit(apiService.getListNotice(page)) }
    override fun configApp(): Flow<ConfigResponse> = flow { emit(apiConfigService.configApp()) }
}