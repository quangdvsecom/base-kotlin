
import com.el.mybasekotlin.data.state.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

interface SafeApiCall {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): DataState<T> {
        return withContext(Dispatchers.IO) {
            try {
                DataState.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {
                    is HttpException -> {
                        DataState.Error(
                            0.toString(),
                            throwable.response()?.errorBody().toString(),
                            isException = true
                        )
                    }

                    else -> {
                        DataState.Error(0.toString(), "SafeApiCall null", isException = true)
                    }
                }
            }
        }
    }
}