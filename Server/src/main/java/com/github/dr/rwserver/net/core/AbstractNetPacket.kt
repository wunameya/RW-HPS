package com.github.dr.rwserver.net.core

import kotlin.Throws
import java.io.IOException
import com.github.dr.rwserver.data.Player
import com.github.dr.rwserver.game.GameCommand
import com.github.dr.rwserver.io.Packet
import com.github.dr.rwserver.struct.Seq
import com.github.dr.rwserver.util.zip.gzip.GzipEncoder
import java.io.DataOutputStream

/**
 * 获取包 转换包 理论上全版本通用 但是部分版本需要特殊覆盖实现
 * @author Dr
 */
interface AbstractNetPacket {
    /**
     * 获取系统命名的消息包
     * SERVER: ...
     * @param msg The message
     * @return Packet
     * @throws IOException err
     */
    @Throws(IOException::class)
    fun getSystemMessagePacket(msg: String): Packet

    /**
     * 发送用户名命名的消息
     * @param      msg     The message
     * @param      sendBy  The sendBy
     * @param      team    The team
     * @return Packet
     * @throws IOException err
     */
    @Throws(IOException::class)
    fun getChatMessagePacket(msg: String, sendBy: String, team: Int): Packet

    /**
     * Ping
     * @param player Player
     * @return Packet
     * @throws IOException err
     */
    @Throws(IOException::class)
    fun getPingPacket(player: Player): Packet

    /**
     * 获取时刻包
     * @param tick Tick
     * @return Packet
     * @throws IOException err
     */
    @Throws(IOException::class)
    fun getTickPacket(tick: Int): Packet

    /**
     * 获取时刻包
     * @param tick Tick
     * @param cmd 位移
     * @return Packet
     * @throws IOException err
     */
    @Throws(IOException::class)
    fun getGameTickCommandPacket(tick: Int, cmd: GameCommand): Packet

    /**
     * 获取时刻包
     * @param tick Tick
     * @param cmd 多位移
     * @return Packet
     * @throws IOException err
     */
    @Throws(IOException::class)
    fun getGameTickCommandsPacket(tick: Int, cmd: Seq<GameCommand>): Packet

    /**
     * 获取队伍包
     * @return 队伍包
     * @throws IOException err
     */
    @Throws(IOException::class)
    fun getTeamDataPacket(): GzipEncoder

    /**
     * 转换GameSave包
     * @param packet packet
     * @return Packet
     * @throws IOException err
     */
    @Throws(IOException::class)
    fun convertGameSaveDataPacket(packet: Packet): Packet

    /**
     * 开始游戏
     * @return Packet
     * @throws IOException err
     */
    @Throws(IOException::class)
    fun getStartGamePacket(): Packet

    /**
     * 获取包中的地图名
     * @param bytes Packet.bytes
     * @return 地图名
     * @throws IOException err
     */
    @Throws(IOException::class)
    fun getPacketMapName(bytes: ByteArray): String

    /**
     * 退出
     * @return Packet
     * @throws IOException err
     */
    @Throws(IOException::class)
    fun getExitPacket(): Packet

    /**
     * 写入玩家的数据
     * @param player Player
     * @param stream Data流
     */
    @Throws(IOException::class)
    fun writePlayer(player: Player, stream: DataOutputStream)

    /**
     * 获取连接包
     * @return Packet
     */
    @Throws(IOException::class)
    fun getPlayerConnectPacket(): Packet

    /**
     * 获取注册包
     * @param name Player Name
     * @param uuid Player UUID
     * @param passwd Server Passwd
     * @param key Server Register Key
     * @return Packet
     */
    @Throws(IOException::class)
    fun getPlayerRegisterPacket(name: String, uuid: String, passwd: String?, key: Int): Packet
}