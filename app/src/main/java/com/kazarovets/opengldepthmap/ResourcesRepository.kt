package com.kazarovets.opengldepthmap

object ResourcesRepository {

    fun getOriginalAndDepth(id: Int): Pair<Int, Int> {
        return when (id) {
            0 -> Pair(R.drawable.small_pic, R.drawable.small_map)
            1 -> Pair(R.drawable.ball, R.drawable.ball_map)
            2 -> Pair(R.drawable.small_pic_2, R.drawable.small_map_2)
            else -> Pair(R.drawable.lady, R.drawable.lady_map)
        }
    }
}