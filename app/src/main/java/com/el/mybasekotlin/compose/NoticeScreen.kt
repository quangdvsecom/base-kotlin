package com.el.mybasekotlin.compose

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.load.engine.Resource
import com.el.mybasekotlin.base.LocalGlobalViewModel
import com.el.mybasekotlin.data.state.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import timber.log.Timber

/**
 * Created by ElChuanmen on 3/19/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
@Composable
fun NoticeScreen() {
    val TAG = "Notification"
    val globalViewModel = LocalGlobalViewModel.current
    val noticeViewModel = hiltViewModel<NoticeViewmodel>()
    val noticeState by noticeViewModel.stateNotice.collectAsStateWithLifecycle()
//    val noticeData by rememberSaveable { derivedStateOf { noticeViewModel.dataNotice } }
    val noticeData = noticeViewModel.dataNotice
    //listener state response
    val scope = rememberCoroutineScope()
    when (noticeState) {
        is DataState.Loading -> {
//                isLoading = true
//                if (!loadMore)
//                    refreshing = true

        }

        is DataState.Success -> {
        }

        is DataState.Empty -> {
            Timber.d("LoadNotice Empty")
//                isLoading = false
//                refreshing = false
//                loadMore = false
        }

        is DataState.Error -> {
//                isLoading = false
//                refreshing = false
//                loadMore = false
        }


    }

    Button(onClick = {

        scope.launch(Dispatchers.IO) {
            try {
                delay(1000)
                println("Button clicked after 1s")
            } catch (e: Exception) {

            }

        }
    }) {
        Text("Click me")
    }

}