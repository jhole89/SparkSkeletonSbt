package org.apache.spark.streaming

import org.apache.spark.util.ManualClock

/**
  * This class is defined in this package as the ManualClock is
  * private in the "spark" package
  *
  * Inspired by https://github.com/mkuthan/example-spark &
  * https://blog.ippon.tech/testing-strategy-for-spark-streaming/
  */
class ClockWrapper(ssc: StreamingContext) {

  def getTimeMillis(): Long = manualClock().getTimeMillis()

  def setTime(timeToSet: Long): Unit = manualClock().setTime(timeToSet)

  def advance(timeToAdd: Long): Unit = manualClock().advance(timeToAdd)

  def waitTillTime(targetTime: Long): Long = manualClock().waitTillTime(targetTime)

  private def manualClock(): ManualClock = {
    ssc.scheduler.clock.asInstanceOf[ManualClock]
  }
}
