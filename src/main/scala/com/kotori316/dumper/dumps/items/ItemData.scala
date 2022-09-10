package com.kotori316.dumper.dumps.items

import net.minecraft.ChatFormatting
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.{Item, ItemStack, Items}
import org.jetbrains.annotations.NotNull

import scala.jdk.StreamConverters._
import scala.jdk.javaapi.CollectionConverters

case class ItemData(index: Int, stack: ItemStack) {
  @NotNull
  def item: Item = stack.getItem

  def id: Int = Item.getId(item)

  def displayName: String = if (stack.isEmpty) "Unnamed" else ChatFormatting.stripFormatting(item.getName(stack).getString)

  def tags: String = {
    if (stack.getItem == Items.ENCHANTED_BOOK) {
      CollectionConverters.asScala(EnchantmentHelper.getEnchantments(stack)).map { case (enchantment, level) =>
        ChatFormatting.stripFormatting(enchantment.getFullname(level).getString)
      }.mkString(", ")
    } else {
      stack.getTags.toScala(Seq).map(_.location).mkString(", ")
    }
  }

  def getRegistryName: ResourceLocation = Registry.ITEM.getKey(item)
}
