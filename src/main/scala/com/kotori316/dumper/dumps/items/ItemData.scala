package com.kotori316.dumper.dumps.items

import javax.annotation.Nonnull
import net.minecraft.ChatFormatting
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.{Item, ItemStack, Items}

import scala.jdk.javaapi.CollectionConverters

case class ItemData(index: Int, stack: ItemStack) {
  @Nonnull
  def item: Item = stack.getItem

  def id: Int = Item.getId(item)

  def displayName: String = if (stack.isEmpty) "Unnamed" else ChatFormatting.stripFormatting(item.getName(stack).getString)

  def tags: String = {
    if (stack.getItem == Items.ENCHANTED_BOOK) {
      CollectionConverters.asScala(EnchantmentHelper.getEnchantments(stack)).map { case (enchantment, level) =>
        ChatFormatting.stripFormatting(enchantment.getFullname(level).getString)
      }.mkString(", ")
    } else {
      CollectionConverters.asScala(ItemTags.getAllTags.getMatchingTags(item)).mkString(", ")
    }
  }
}
