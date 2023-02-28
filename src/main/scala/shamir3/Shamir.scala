package shamir3

import java.util.Random
import scala.util.chaining.scalaUtilChainingOps

@main def main(): Unit =
  val secret: Array[B] = B("hallo".getBytes("UTF-8"))
  println(split(Random(1), secret, 5, 3).map(_.mkString("/")))

def split(random: Random, secret: Array[B], shares: Int, threshold: Int): Seq[Array[B]] =
  require((shares < 256) && (shares > 0) && (threshold <= shares) && (threshold > 0)) // FIXME threshold 1 ???
  val polynomials = secret.map { b => generate(random, threshold - 1, b) }
  (1 to shares).map { share => polynomials.map { polynomial => eval(polynomial, B(share.toByte)) } }

def generate(random: Random, degree: Int, x: B): Array[B] =
  x +: new Array[B](degree - 1).tap { p => random.nextBytes(p.bytes) } :+ B(random.nextInt(1, 256).toByte)

def eval(bs: Array[B], x: B): B =
  bs.foldRight(B.zero) { case (b, result) => result * x + b }
