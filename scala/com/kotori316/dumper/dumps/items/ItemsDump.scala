package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.Dumper
import com.kotori316.dumper.dumps.Dumps
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.{NonNullList, ResourceLocation}
import net.minecraftforge.fml.common.registry.ForgeRegistries

import scala.collection.JavaConverters._
import scala.util.Try

object ItemsDump extends Dumps {
  override val configName: String = "OutputAllItems"
  override val fileName: String = "allitemslist"
  private val filters = Seq(PickaxeFilter, AxeFilter, ShovelFilter, SwordFilter)


  override def apply(): Unit = {
    super.apply()
    if (Dumper.enables.contains(configName)) {
      filters.foreach(_.writeToFile())
    }
  }

  override def content(): Seq[String] = {
    val first = Seq("Items", "Number : ID: meta : Name")
    val printStackTrace: PartialFunction[Throwable, Unit] = {
      case e: Throwable => Dumper.logger.error(e)
    }
    val seq = ForgeRegistries.ITEMS.asScala.map(item => {
      val nonNullList = NonNullList.create[ItemStack]()
      Try {
        if (item.getCreativeTab != null) {
          item.getSubItems(item.getCreativeTab, nonNullList)
        } else {
          CreativeTabs.CREATIVE_TAB_ARRAY.foreach(item.getSubItems(_, nonNullList))
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
    val hasSubType = item.getHasSubtypes
    val list = nn.asScala.toList
    val fStack :: rest = list
    val displayName = if (fStack.isEmpty) "Unnamed" else TextFormatting.getTextWithoutFormattingCodes(item.getItemStackDisplayName(fStack))

    def strings = {
      if (item == Items.ENCHANTED_BOOK) {
        list.map(stack => {
          format.format(id, "", displayName) + " : " +
            EnchantmentHelper.getEnchantments(stack).asScala.map { case (enchantment, level) =>
              TextFormatting.getTextWithoutFormattingCodes(enchantment.getTranslatedName(level))
            }.mkString(", ")
        })
      } else {
        val first = format.format(id, if (hasSubType) fStack.getItemDamage else "", displayName) + " : " + registryName + oreName(fStack)
        first :: rest.map(s => format.format(id, s.getItemDamage, TextFormatting.getTextWithoutFormattingCodes(s.getDisplayName)) + " : " + registryName + oreName(s))
      }
    }

    def shortName = "%4d : %5d : %s".format(id, fStack.getMaxDamage, displayName)
  }

}
