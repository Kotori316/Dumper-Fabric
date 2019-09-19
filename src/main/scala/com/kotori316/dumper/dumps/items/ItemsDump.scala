package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.Dumper
import com.kotori316.dumper.dumps.{Dumps, Filter}
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.item.{Item, ItemGroup, ItemStack, Items}
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.{NonNullList, ResourceLocation}
import net.minecraftforge.registries.ForgeRegistries

import scala.jdk.CollectionConverters._
import scala.util.Try

object ItemsDump extends Dumps[ItemStack] {
  override val configName: String = "OutputAllItems"
  override val fileName: String = "AllItemsList"

  override def getFilters: Seq[Filter[ItemStack]] = Seq(new PickaxeFilter, new AxeFilter, new ShovelFilter, new SwordFilter)

  override def output(filters: Seq[Filter[ItemStack]]): Unit = {
    super.output(filters)
    if (isEnabled) {
      filters.foreach(_.writeToFile())
    }
  }

  override def content(filters: Seq[Filter[ItemStack]]): Seq[String] = {
    val first = Seq("Items", "Number : ID: meta : Name")
    val printStackTrace: PartialFunction[Throwable, Unit] = {
      case e: Throwable => Dumper.LOGGER.error(e)
    }
    val seq = ForgeRegistries.ITEMS.asScala.map(item => {
      val nonNullList = NonNullList.create[ItemStack]()
      Try {
        if (item.getGroup != null) {
          item.fillItemGroup(item.getGroup, nonNullList)
        } else {
          ItemGroup.GROUPS.foreach(item.fillItemGroup(_, nonNullList))
        }
        if (nonNullList.isEmpty) nonNullList.add(new ItemStack(item))
      }.recover(printStackTrace)
      ID(item, nonNullList)
    }).flatMap(id => {
      filters.find(_.addToList(id.fStack, id.shortName, id.displayName, id.registryName.toString))
      id.strings
    }).zipWithIndex.map { case (s, i) => "%5d : %s".format(i, s) }

    first ++ seq
  }

  private[this] val format = "%4d : %5s : %s"

  case class ID(item: Item, nn: NonNullList[ItemStack]) {
    val registryName: ResourceLocation = item.getRegistryName
    val id = Item.getIdFromItem(item)
    val hasSubType = nn.size() > 1
    //item.getHasSubtypes
    val list = nn.asScala.toList
    val fStack :: rest = list
    val displayName = if (fStack.isEmpty) "Unnamed" else TextFormatting.getTextWithoutFormattingCodes(item.getDisplayName(fStack).getFormattedText)

    def strings = {
      if (item == Items.ENCHANTED_BOOK) {
        list.map(stack => {
          format.format(id, "", displayName) + " : " +
            EnchantmentHelper.getEnchantments(stack).asScala.map { case (enchantment, level) =>
              TextFormatting.getTextWithoutFormattingCodes(enchantment.getDisplayName(level).getFormattedText)
            }.mkString(", ")
        })
      } else {
        val first = format.format(id, if (hasSubType) fStack.getDamage else "", displayName) + " : " + registryName + oreName(fStack)
        first :: rest.map(s => format.format(id, s.getDamage, s.getDisplayName.getUnformattedComponentText) + " : " + registryName + oreName(s))
      }
    }

    def shortName = "%4d : %5d : %s".format(id, fStack.getMaxDamage, displayName)
  }

}
