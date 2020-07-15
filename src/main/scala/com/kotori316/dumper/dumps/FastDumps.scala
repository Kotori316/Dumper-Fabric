package com.kotori316.dumper.dumps

import net.minecraft.server.MinecraftServer

trait FastDumps[T] extends Dumps[T] {
  override def content(filters: Seq[Filter[T]], server: MinecraftServer): Seq[String] = content(filters)

  def content(filters: Seq[Filter[T]]): Seq[String]
}
