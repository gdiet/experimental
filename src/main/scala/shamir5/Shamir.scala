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
  val secret = "hallo".getBytes("UTF-8").map(_.toInt)
  println(secret.map(hex).mkString(" "))
  val parts = split(new java.security.SecureRandom().nextInt(_, _), secret, 5, 3)
  println("parts:")
  parts.foreach(part => println(part.map(hex).mkString(" ")))
