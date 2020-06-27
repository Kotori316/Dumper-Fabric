package com.kotori316.dumper.dumps.items

import javax.annotation.Nonnull
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.{Item, ItemStack, Items}
import net.minecraft.tags.ItemTags
import net.minecraft.util.text.TextFormatting

import scala.jdk.javaapi.CollectionConverters

case class ItemData(index: Int, stack: ItemStack) {
  @Nonnull
  def item: Item = stack.getItem

  def id: Int = Item.getIdFromItem(item)

  def displayName: String = if (stack.isEmpty) "Unnamed" else TextFormatting.getTextWithoutFormattingCodes(item.getDisplayName(stack).getString)

  def tags: String = {
    if (stack.getItem == Items.ENCHANTED_BOOK) {
      CollectionConverters.asScala(EnchantmentHelper.getEnchantments(stack)).map { case (enchantment, level) =>
        TextFormatting.getTextWithoutFormattingCodes(enchantment.getDisplayName(level).getString)
      }.mkString(", ")
    } else {
      CollectionConverters.asScala(ItemTags.getCollection.getOwningTags(item)).mkString(", ")
    }
  }
}
