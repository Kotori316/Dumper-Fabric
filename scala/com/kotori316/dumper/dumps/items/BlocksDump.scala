package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.dumps.Dumps
import net.minecraft.block.Block
import net.minecraft.item.{Item, ItemBlock, ItemStack}
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.common.registry.ForgeRegistries

import scala.collection.JavaConverters._

object BlocksDump extends Dumps {
    override val configName: String = "outputBlocks"
    override val fileName: String = "blocksOutput"
    private[this] val filters = Seq(OreFilter, WoodFilter, LeaveFilter)

    override def content(): Seq[String] = {
        ForgeRegistries.BLOCKS.asScala.map(BD.apply)
        ???
    }

    case class BD(block: Block) {
        val item = Item.getItemFromBlock(block)
        val id = Block.getIdFromBlock(block)
        val name = block.getRegistryName
        val blocks = NonNullList.create[ItemStack]()
        block.getSubBlocks(block.getCreativeTabToDisplayOn, blocks)

        def classString = {
            val clazz = item.getClass
            if (clazz != classOf[ItemBlock])
                " : " + clazz.getName
            else ""
        }
    }

}
