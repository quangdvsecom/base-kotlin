package com.el.mybasekotlin.ui.fragment.game

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.el.mybasekotlin.data.model.game.GameData
import com.el.mybasekotlin.data.model.game.GameType
import com.el.mybasekotlin.ui.fragment.gamedetails.EmptyGameCustom
import com.el.mybasekotlin.ui.fragment.gamedetails.MyFragmentGameExample
import com.el.mybasekotlin.ui.fragment.gamedetails.MyGameCustomViewExample
/**
 * Xác định là customview hay fragment
 */
sealed class DetailsContent {
    data class FragmentContent(
        val fragment: Fragment
    ) : DetailsContent()

    data class ViewContent(
        val viewFactory: (Context) -> View
    ) : DetailsContent()
}

object GameFactory {

    fun create(gameData: GameData): DetailsContent {
        return when (gameData.gameType) {
            GameType.RANKING_FILTER_GAME.value -> {
                DetailsContent.ViewContent { context ->
                    MyGameCustomViewExample(context, gameData).apply {}
                }
            }
            GameType.MATH_RUN_GAME.value -> {
                val fragment = MyFragmentGameExample().apply {
                    arguments = Bundle().apply {
                        putParcelable("KEY_GAME_DATA", gameData)
                    }
                }
                DetailsContent.FragmentContent(fragment)
            }

            else -> {
//                DetailsContent.ViewContent { context ->
//                    EmptyGameCustom(context).apply {}
//                }
                val fragment = MyFragmentGameExample().apply {
                    arguments = Bundle().apply {
                        putParcelable("KEY_GAME_DATA", gameData)
                    }
                }
                DetailsContent.FragmentContent(fragment)

            }
        }


    }
}