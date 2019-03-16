package com.kotori316.dumper.dumps.items

import net.minecraft.item.ItemStack

trait Filter {
  def addToList(stack: ItemStack, shortName: String, displayName: String, uniqueName: String): Boolean

  def writeToFile(): Unit
}
