package shamir5

import scala.util.chaining.scalaUtilChainingOps

type RandomInt = (Int, Int) => Int

def split(random: RandomInt, secret: Array[Int], shares: Int, threshold: Int): Seq[Array[Int]] =
  require((shares < 256) && (shares > 0) && (threshold <= shares) && (threshold > 0)) // FIXME threshold 1 ???
  val polynomials = secret.map { b => generate(random, threshold - 1, b) }
  (1 to shares).map { share => polynomials.map { polynomial => eval(polynomial, share) } }

def generate(random: RandomInt, degree: Int, x: Int): Array[Int] =
  x +: Array.fill(degree - 1)(random(0, 256)) :+ random(1, 256)

def eval(a: Array[Int], x: Int) = a.foldRight(0) { case (b, result) => result #* x #+ b }

@main def tryout2(): Unit =
  println(eval(Array(20, 20), 2))

  import scala.jdk.CollectionConverters.*
  val secret = "hallo".getBytes("UTF-8")
  println(secret.map(b => f"$b%02x").mkString(" "))
  val secret2 = secret.map(_.toInt)
  println(secret2.map(b => f"$b%02x").mkString(" "))
  {
    val scheme = new com.shamir2.Scheme(5, 3);
    val random = java.util.Random(1)
    val parts = scheme.split(secret, random.nextInt(_, _)).asScala
    parts.foreach((a, b) => println(s"$a-" + b.map(b => f"$b%02x").mkString("")))
    val joined = scheme.join(parts.take(3).asJava)
    println(joined.map(b => f"$b%02x").mkString(" "))
    println(new String(joined, "UTF-8"))
  }
  println()
  {
    val scheme = new com.shamir2.Scheme(5, 3);
    val random = java.util.Random(1)
    val parts = split(random.nextInt, secret2, 5, 3)
    parts.zipWithIndex.foreach((part, a) => println(s"${a + 1}-" + part.map(hex).mkString("")))
    val partsJ = parts.zipWithIndex.map((p, a) => (Integer.valueOf(a + 1), p.map(_.toByte))).toMap
    val joined = scheme.join(partsJ.take(3).asJava)
    println(joined.map(b => f"$b%02x").mkString(" "))
    println(new String(joined, "UTF-8"))
  }
