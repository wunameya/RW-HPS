package com.github.dr.rwserver.util.log

import com.github.dr.rwserver.data.global.Data
import com.github.dr.rwserver.util.Time.getUtcMilliFormat
import java.io.PrintWriter
import java.io.StringWriter
import java.text.MessageFormat

/**
 * Log Util
 * @author Dr
 * @version 1.1
 * @date 2020年3月8日星期日 3:54
 * 练手轮子? :P
 */
object Log {
    /** 默认 WARN  */
    private var LOG_GRADE = 5
    private lateinit var logPrint: LogPrint<Any>
    private val LOG_CACHE = StringBuilder()

    @JvmStatic
	fun set(log: String) {
        LOG_GRADE = Logg.valueOf(log).getLogg()
    }

    @JvmStatic
	fun setPrint(system: Boolean) {
        logPrint =
            if (system) object : LogPrint<Any> {
                override fun println(t: Any) {
                    System.out.println(t)
                }
            } else object : LogPrint<Any> {
                override fun println(t: Any) {
                    LOG_CACHE.append(t)
                }
            }
    }

    @JvmStatic
	val logCache: String
        get() {
            val result = LOG_CACHE.toString()
            LOG_CACHE.delete(0, LOG_CACHE.length)
            return result
        }

    /**
     * Log：
     * tag 标题 默认警告级
     */
    @JvmStatic
    fun skipping(e: Any) {
        logs(9, "SKIPPING", e)
    }
    @JvmStatic
    fun skipping(tag: Any, e: Any) {
        logs(9, tag, e)
    }

    @JvmStatic
    fun fatal(e: Exception) {
        log(7, "FATAL", e)
    }
    @JvmStatic
    fun fatal(tag: Any, e: Exception) {
        log(7, tag, e)
    }
    @JvmStatic
    fun fatal(e: Any) {
        logs(7, "FATAL", e)
    }
    @JvmStatic
    fun fatal(tag: Any, e: Any) {
        logs(7, tag, e)
    }

    @JvmStatic
    fun error(e: Exception) {
        log(6, "ERROR", e)
    }
    @JvmStatic
    fun error(tag: Any, e: Exception) {
        log(6, tag, e)
    }
    @JvmStatic
    fun error(e: Any) {
        logs(6, "ERROR", e)
    }

    @JvmStatic
	fun error(tag: Any, e: Any) {
        logs(6, tag, e)
    }

    @JvmStatic
    fun warn(e: Exception) {
        log(5, "WARN", e)
    }
    @JvmStatic
    fun warn(tag: Any, e: Exception) {
        log(5, tag, e)
    }
    @JvmStatic
    fun warn(e: Any) {
        logs(5, "WARN", e)
    }
    @JvmStatic
	fun warn(tag: Any, e: Any) {
        logs(5, tag, e)
    }

    @JvmStatic
    fun info(e: Exception) {
        log(4, "INFO", e)
    }
    @JvmStatic
    fun info(tag: Any, e: Exception) {
        log(4, tag, e)
    }
    @JvmStatic
    fun info(e: Any) {
        logs(4, "INFO", e)
    }
    @JvmStatic
	fun info(tag: Any, e: Any) {
        logs(4, tag, e)
    }

    @JvmStatic
    fun debug(e: Exception) {
        log(3, "DEBUG", e)
    }
    @JvmStatic
    fun debug(tag: Any, e: Exception) {
        log(3, tag, e)
    }
    @JvmStatic
    fun debug(e: Any) {
        logs(3, "DEBUG", e)
    }
    @JvmStatic
	fun debug(tag: Any, e: Any) {
        logs(3, tag, e)
    }

    @JvmStatic
    fun track(e: Exception) {
        log(2, "TRACK", e)
    }
    @JvmStatic
    fun track(tag: Any, e: Exception) {
        log(2, tag, e)
    }

    private fun logs(e: Exception): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        e.printStackTrace(printWriter)
        return stringWriter.buffer.toString()
    }

    /**
     * WLog：
     * @param i 警告级
     * @param tag 标题 默认警告级
     * @param e Exception
     * i>=设置级 即写入文件
     */
    private fun log(i: Int, tag: Any, e: Exception) {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        e.printStackTrace(printWriter)
        logs(i, tag, stringWriter.buffer)
    }

    private fun logs(i: Int, tag: Any, e: Any) {
        if (this.LOG_GRADE > i) {
            return
        }
        val sb = StringBuilder()
        val lines = e.toString().split(Data.LINE_SEPARATOR).toTypedArray()
        val stack = Throwable().stackTrace
        var i1: Int = 0
        while (i1 < stack.size) {
            val ste = stack[i1]
            val className = ste.className + "." + ste.methodName
            if (!className.contains("com.github.dr.rwserver.util.log.Log")) {
                sb.append("[").append(ste.fileName).append("] : ")
                    .append(ste.methodName).append(" : ").append(ste.lineNumber).append(Data.LINE_SEPARATOR)
                break
            }
            i1++
        }
        sb.append("UTC [")
            .append(getUtcMilliFormat(1)).append("] ") //.append(LINE_SEPARATOR)
            .append(tag)
            .append(": ")
            .append(Data.LINE_SEPARATOR)
        for (line in lines) {
            sb.append(line)
                .append(Data.LINE_SEPARATOR)
        }
        //this.logPrint.println(sb)
        println(sb)
    }

    @JvmStatic
    fun clog(text: String) {
        var text = text
        text = "[" +
                getUtcMilliFormat(1) +
                " UTC] " +
                text
        println(formatColors("$text&fr"))
    }

    @JvmStatic
	fun clog(text: String?, vararg obj: Any?) {
        clog(MessageFormat(text).format(obj))
    }

    private fun formatColors(text: String): String {
        var text = text
        for (i in ColorCodes.CODES.indices) {
            text = text.replace("&" + ColorCodes.CODES[i], ColorCodes.VALUES[i])
        }
        return text
    }

    fun testPrint(`object`: Any) {
        info(`object`)
    }

    private enum class Logg(private val logg: Int) {
        /* Log等级 默认为WARN */
        /* 开发时为ALL */
        OFF(8), FATAL(7), ERROR(6), WARN(5), INFO(4), DEBUG(3), TRACE(2), ALL(1);

        open fun getLogg(): Int {
            return logg
        }
    }

    private interface LogPrint<T> {
        /**
         * 接管Log逻辑
         * @param t TEXT
         */
        fun println(t: T)
    }
}