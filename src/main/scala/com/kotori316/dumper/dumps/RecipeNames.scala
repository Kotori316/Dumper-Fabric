package com.kotori316.dumper.dumps

import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraft.server.MinecraftServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.registry.Registry

import scala.jdk.javaapi.CollectionConverters

object RecipeNames extends Dumps[IRecipe[_]] {
  override val configName: String = "OutputRecipeNames"
  override val fileName: String = "recipes"
  private final val formatter = new Formatter[Recipe](
    Seq("-name", "-type", "-output", "group", "-isDynamic"),
    Seq(_.getId, _.recipeType, _.getRecipeOutput, _.getGroup, _.isDynamic)
  )

  override def content(filters: Seq[Filter[IRecipe[_]]], server: MinecraftServer): Seq[String] = {
    val comparator = Ordering.by((r: Recipe) => r.recipeType) orElseBy (_.getGroup) orElseBy (_.getId)
    val all = CollectionConverters.asScala(server.getRecipeManager.getRecipes)
      .toSeq
      .map(Recipe)
      .sorted(comparator)
    formatter.format(all)
  }

  private case class Recipe(private val recipe: IRecipe[_]) {
    val recipeType: String = Registry.RECIPE_TYPE.getKey(recipe.getType).toString

    def getId: ResourceLocation = recipe.getId

    def getRecipeOutput: ItemStack = recipe.getRecipeOutput

    def getGroup: String = recipe.getGroup

    def isDynamic: Boolean = recipe.isDynamic
  }

}
