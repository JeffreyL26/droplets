package com.jbastudio.gofish

import com.jbastudio.gofish.network.GameClient
import com.jbastudio.gofish.network.GameServer
import com.jbastudio.gofish.ui.components.AvatarChoice
import org.json.JSONObject

/** Hält Server, Client, GAME_START-Nachricht und den lokalen Avatar über Activities hinweg. */
object GameHolder {
    var server:       GameServer? = null
    var client:       GameClient? = null
    var gameStartMsg: JSONObject? = null
    /** Der vom Spieler gewählte Avatar — wird beim Verbinden an den Server geschickt. */
    var myAvatar:     AvatarChoice = AvatarChoice()
}
