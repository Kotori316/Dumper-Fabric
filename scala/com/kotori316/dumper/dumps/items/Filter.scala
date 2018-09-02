package com.kotori316.dumper.dumps.items

import net.minecraft.item.ItemStack

trait Filter {
    def addtoList(stack: ItemStack, shortName: String, displayName: String, uniqueName: String): Boolean

    def writeToFile(): Unit
}
