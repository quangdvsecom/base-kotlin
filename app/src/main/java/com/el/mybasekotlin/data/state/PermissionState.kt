package com.el.mybasekotlin.data.state

/**
 * Created by ElChuanmen on 1/17/2025.
 * Telegram : elchuanmen
 * Phone :0949514503-0773209008
 * Mail :doanvanquang146@gmail.com
 */
data class PermissionState(
    val name: String="",
    val state: State
)
enum class State {
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}
