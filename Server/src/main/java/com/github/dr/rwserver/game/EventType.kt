package com.github.dr.rwserver.game

import com.github.dr.rwserver.data.Player
import com.github.dr.rwserver.net.core.server.AbstractNetConnect

/**
 * @author Dr
 */
class EventType {
    /** 服务器初始化  */
    class ServerLoadEvent

    /** 玩家加入  */
    class PlayerJoinEvent(val player: Player)

    /** 玩家重连  */
    class PlayerReJoinEvent(val player: Player)

    /** 玩家连接密码验证. */
    class PlayerConnectPasswdCheckEvent(
        /** 游戏实现协议  */
        val abstractNetConnect: AbstractNetConnect,
        /** 密码SHA256的16进  */
        val passwd: String
    ) {
        /** 密码SHA256的16进  */
        @JvmField
        var result = false

        /** 你可以给他设置一个名字  */
        @JvmField
        var name = ""
    }

    /** 玩家连接时. */
    class PlayerConnectEvent(val player: Player)

    /** 玩家离开时  */
    class PlayerLeaveEvent(val player: Player)

    /** 玩家发言时  */
    class PlayerChatEvent(val player: Player, val message: String)

    /** 开始游戏  */
    class GameStartEvent

    /** 结束游戏  */
    class GameOverEvent

    /** 玩家被ban  */
    class PlayerBanEvent(val player: Player)

    /** 玩家被解除ban  */
    class PlayerUnbanEvent(val player: Player)

    /** 玩家被banIp  */
    class PlayerIpBanEvent(val player: Player)

    /** 玩家被解banIp  */
    class PlayerIpUnbanEvent(val ip: String)

    /** 玩家加入时的名字过滤 */
    class PlayerJoinNameEvent(val name: String) {
        @JvmField
        var resultName = ""
    }

    /** 玩家操作单位事件 */
    class PlayerOperationUnitEvent(val player: Player, val playerUnit: EventLambdaType.PlayerUnit)

    /** 玩家进入的UUID/NAME */
    class PlayerJoinUuidandNameEvent(val uuid: String,val  name: String)
}