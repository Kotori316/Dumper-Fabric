package com.kotori316.dumper.dumps.items

trait Filter[T] {
  def addToList(v: T, shortName: String, displayName: String, uniqueName: String): Boolean

  def writeToFile(): Unit
}
