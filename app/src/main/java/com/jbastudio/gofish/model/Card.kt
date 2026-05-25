package com.jbastudio.gofish.model

import org.json.JSONArray
import org.json.JSONObject

data class Card(val rank: String, val suit: String) {
    override fun toString() = "$rank$suit"

    fun toJson(): JSONObject = JSONObject().put("rank", rank).put("suit", suit)

    companion object {
        val RANKS = listOf("2","3","4","5","6","7","8","9","10","J","Q","K","A")
        val SUITS = listOf("♠","♥","♦","♣")

        fun fromJson(o: JSONObject) = Card(o.getString("rank"), o.getString("suit"))
        fun listFromJson(arr: JSONArray) =
            (0 until arr.length()).map { fromJson(arr.getJSONObject(it)) }
    }
}