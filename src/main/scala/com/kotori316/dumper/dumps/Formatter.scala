package com.kotori316.dumper.dumps

class Formatter[A](rows: Seq[String], converters: Seq[A => Any]) {

  require(rows.size == converters.size, s"rows $rows doesn't match converters $converters.")

  def format(as: scala.collection.Seq[A]): Seq[String] = {
    val converted: Seq[Seq[String]] = as.map(a => converters.map(_.apply(a).toString)).toSeq
    val lengthMaxes: Seq[Int] = (rows +: converted).map(_.map(_.length)).foldLeft(Seq.fill(rows.size)(0)) {
      case (s1, s2) => (s1 zip s2).map { case (i, i1) => i max i1 }
    }
    val formatString = (lengthMaxes zip rows).map { case (i, str) => s"%${getMinus(str)}${i}s" }.mkString(" : ")
    (removeMinus(rows) +: converted).map(ss => formatString.format(ss: _*))
      .map(s => ("0" + s).trim.substring(1))
  }

  def getMinus(s: String): String = if (s.startsWith("-")) "-" else ""

  def removeMinus(ss: Seq[String]): Seq[String] = ss.map(s => if (s.startsWith("-")) s.tail else s)
}
