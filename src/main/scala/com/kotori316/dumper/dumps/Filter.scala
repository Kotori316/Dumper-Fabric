package com.kotori316.dumper.dumps

trait Filter[T] {
  def addToList(v: T): Boolean

  def writeToFile(): Unit
}
