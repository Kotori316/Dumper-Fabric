package com.kotori316.dumper.dumps.items

import com.kotori316.dumper.Dumper
import com.kotori316.dumper.dumps.Dumps
import net.minecraft.block.Block
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemBlock, ItemStack}
import net.minecraft.util.NonNullList
import net.minecraftforge.fml.common.registry.ForgeRegistries

import scala.collection.JavaConverters._

object BlocksDump extends Dumps {
    override val configName: String = "outputBlocks"
    override val fileName: String = "blocksOutput"
    private[this] final val filters = Seq(OreFilter, WoodFilter, LeaveFilter)

    override def apply(): Unit = {
        super.apply()
        if (Dumper.enables.contains(configName)) {
            filters.foreach(_.writeToFile())
        }
    }

    override def content(): Seq[String] = {
        ForgeRegistries.BLOCKS.asScala.map(BD.apply).flatMap(_.stacks).map { e: BlockStack =>
            filters.find(_.addtoList(e.stack, e.o, e.stack.getDisplayName, e.bd.name.toString))
            e.o
        }.zipWithIndex.map { case (s, i) => "%4d : %s".format(i, s) }.toSeq
    }

    case class BD(block: Block) {
        val item = Item.getItemFromBlock(block)
        val id = Block.getIdFromBlock(block)
        val name = block.getRegistryName
        val blocks = NonNullList.create[ItemStack]()
        block.getSubBlocks(block.getCreativeTabToDisplayOn, blocks)

        def stacks: Seq[BlockStack] = {
            if (blocks.isEmpty) {
                return Nil
            }
            val f :: rest = blocks.asScala.toList
            FBS(this, f) :: rest.map(s => BlockStack(this, s))
        }
    }

    val f = "%4d : %3s : %s"

    sealed trait BlockStack {
        val o: String
        val bd: BD
        val stack: ItemStack
    }

    object BlockStack {
        def apply(p_bd: BD, p_stack: ItemStack): BlockStack = {
            if (!p_stack.isEmpty)
                NNStack(p_bd, p_stack)
            else {
                new BlockStack {
                    override val o: String = f.format(p_bd.id, p_stack.getItemDamage, "")
                    override val bd: BD = p_bd
                    override val stack: ItemStack = ItemStack.EMPTY
                }
            }
        }
    }

    case class NNStack(bd: BD, stack: ItemStack) extends BlockStack {
        val o = f.format(bd.id, stack.getMetadata, stack.getDisplayName) + oreName(stack)
    }

    case class FBS(bd: BD, stack: ItemStack) extends BlockStack {

        def classString = {
            val clazz = bd.item.getClass
            if (clazz != classOf[ItemBlock])
                " : " + clazz.getName
            else ""
        }

        val o: String =
            if (stack.isEmpty) {
                if (bd.item == Items.AIR)
                    f.format(bd.id, if (bd.item.getHasSubtypes) stack.getMetadata else "", bd.block.getLocalizedName) + " : " + bd.name
                else
                    f.format(bd.id, if (bd.item.getHasSubtypes) stack.getMetadata else "", bd.item.getItemStackDisplayName(stack)) + " : " + bd.name
            } else {
                f.format(bd.id, if (bd.item.getHasSubtypes) stack.getMetadata else "", stack.getDisplayName) +
                  classString + " : " + bd.name + oreName(stack)
            }
    }

}
