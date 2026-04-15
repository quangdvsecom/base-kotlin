import android.content.Context
import com.bumptech.glide.load.engine.Resource
import com.el.mybasekotlin.data.model.TestUser
import com.el.mybasekotlin.data.network.api.ApiHelperImpl
import com.el.mybasekotlin.data.network.api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

/**
 * Created by ElChuanmen on 11/30/2022.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
//class TokenAuthenticator @Inject constructor(
//    context: Context, private val apiService: ApiService, apiConfigService: ApiConfigService
//) : Authenticator, ApiHelperImpl(apiService, apiConfigService) {
//    private val _dataTest = MutableStateFlow<Resource<List<TestUser>>>(Resource.Empty)
//    val dataResult: StateFlow<Resource<List<TestUser>>> = _dataTest
//
//    override fun authenticate(route: Route?, response: Response): Request? {
//        return runBlocking {
////            callRefreshToken()
//            dataResult.collect {
//                when (it) {
//                    is Resource.Success -> {
//                        /**
//                         *   Save token to local
//                         */
////                        userPreferences.saveAccessTokens(
////                            tokenResponse.value.access_token!!,
////                            tokenResponse.value.refresh_token!!
////                        )
//                        /**
//                         *   make the current request when the new token is obtained
//                         */
//
////                        response.request.newBuilder()
////                            .header("Authorization", "Bearer ${tokenResponse.value.access_token}")
////                            .build()
//                    }
//
//                    else -> null
//                }
//            }
//
//        }
//    }
//
////    suspend fun callRefreshToken() {
////        refreshToken().flowOn(Dispatchers.IO).catch { e ->
////        }.onCompletion {}.collect {
////            _dataTest.value = Resource.success(it)
////            }
////        }
//}