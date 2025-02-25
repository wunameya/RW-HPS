package com.github.dr.rwserver.net.game

import com.github.dr.rwserver.net.core.server.AbstractNetConnect
import com.github.dr.rwserver.util.Time.concurrentMillis
import com.github.dr.rwserver.util.threads.ThreadFactoryName
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author Dr
 */
internal class TimeoutDetection(s: Int, startNet: StartNet) {
    private val namedFactory = ThreadFactoryName.nameThreadFactory("TimeoutDetection-")
    private val Service: ScheduledExecutorService = ScheduledThreadPoolExecutor(1, namedFactory)
    private val scheduledFuture: ScheduledFuture<*> = Service.scheduleAtFixedRate(CheckTime(startNet), 0, s.toLong(), TimeUnit.SECONDS)

    private class CheckTime(private val startNet: StartNet) : Runnable {
        override fun run() {
            startNet.OVER_MAP.each { k: String?, v: AbstractNetConnect ->
                if (checkTimeoutDetection(v)) {
                    v.disconnect()
                    startNet.OVER_MAP.remove(k)
                }
            }
        }
    }

    companion object {
        internal fun checkTimeoutDetection(abstractNetConnect: AbstractNetConnect?): Boolean {
            if (abstractNetConnect == null) {
                return true
            }

            return if (abstractNetConnect.inputPassword) {
                /* 60s无反应判定close */
                concurrentMillis() > abstractNetConnect.lastReceivedTime + 60 * 1000L
            } else {
                concurrentMillis() > abstractNetConnect.lastReceivedTime + 180 * 1000L
            }

        }
    }
}