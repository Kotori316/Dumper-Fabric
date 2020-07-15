package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.Dumper
import com.kotori316.dumper.dumps.{Dumps, Filter, Formatter}
import net.minecraft.item.{ItemGroup, ItemStack}
import net.minecraft.server.MinecraftServer
import net.minecraft.util.NonNullList
import net.minecraftforge.registries.ForgeRegistries

import scala.jdk.javaapi.CollectionConverters
import scala.util.Try

object ItemsDump extends Dumps[ItemData] {
  override val configName = "OutputItems"
  override val fileName = "items"
  final val formatter = new Formatter[ItemData](
    Seq("Index", "ID", "-Name", "-RegistryName", "-Tags"),
    Seq(_.index, _.id, _.displayName, _.item.getRegistryName, _.tags)
  )

  override def getFilters: Seq[Filter[ItemData]] = Seq(new PickaxeFilter, new AxeFilter, new ShovelFilter, new SwordFilter)

  override def content(filters: Seq[Filter[ItemData]], server: MinecraftServer): Seq[String] = {
    val items = ForgeRegistries.ITEMS
    val stacks = CollectionConverters.asScala(items)
      .flatMap { item =>
        val nonNullList = NonNullList.create[ItemStack]()
        Try {
          if (item.getGroup != null) {
            item.fillItemGroup(item.getGroup, nonNullList)
          } else {
            ItemGroup.GROUPS.foreach(item.fillItemGroup(_, nonNullList))
          }
          if (nonNullList.isEmpty) nonNullList.add(new ItemStack(item))
        }.recover {
          case e: Throwable => Dumper.LOGGER.error(e)
        }
        CollectionConverters.asScala(nonNullList)
      }
      .zipWithIndex
      .map { case (stack, i) =>
        val data = ItemData(i + 1, stack)
        filters.find(_.addToList(data))
        data
      }.toSeq
    formatter.format(stacks)
  }

}
