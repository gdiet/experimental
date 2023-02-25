package tryout

import com.codahale.shamir.*
import tryout.GF256.*

import java.util.Random
import scala.jdk.CollectionConverters.*

@main def main(): Unit =

  println(GF256.asByte(1))
  println(GF256.asByte(127))
  println(GF256.asByte(128))
  println(GF256.asByte(255))

//  println(GF256.generate(Random(1), 200, 42).filter(_ == 0).mkString(" "))
//  println(GF256.generate(Random(2), 200, 42).filter(_ == 0).mkString(" "))
//  println(GF256.generate(Random(3), 200, 42).filter(_ == 0).mkString(" "))
//  println(GF256.generate(Random(4), 200, 42).filter(_ == 0).mkString(" "))
//  println(GF256.generate(Random(5), 200, 42).filter(_ == 0).mkString(" "))
//  println(GF256.generate(Random(6), 200, 42).filter(_ == 0).mkString(" "))
//  println(GF256.generate(Random(7), 200, 42).filter(_ == 0).mkString(" "))

  sys.exit(0)

//  val b1 = tryout.GF256.B(10)
//  val b2 = tryout.GF256.B(-1)
//  println(b1.int)
//  println(b2.int)
//
//  println(b1*b2)
//  println(GF256.mul(10, -1))
//
//  println(b1/b2)
//  println(GF256.div(10, -1))
//
//  println(b1)
//  println(b1*b2/b2+b2-b2)

//  println(tryout.GF256.SLOG)
//  val b1 = tryout.GF256.B(10)
//  val b2 = tryout.GF256.B(3)
//  println(b1 + b2)
//  println(GF256.LOG.length)
//  println(GF256.LOG.map(b => f"$b%02x").mkString)
//  println(tryout.GF256.LOG.map(b => f"$b%02x").mkString)
//  println(GF256.EXP.length)
//  println(GF256.EXP.map(b => f"$b%02x").mkString)

//  sys.exit(0)

  val secret = "he".getBytes("UTF-8")
  val shares = 5
  val threshold = 2

  // TODO use SecureRandom again
  val random = Random(1)
  val scheme = Scheme(random, shares, threshold)
  println(s"size of secret: ${secret.size}")

  {
    val parts = scheme.split(secret).asScala
    println(s"size of parts: ${parts.values.map(_.length).mkString(", ")}")
    println(s"parts: ${parts.values.map(_.mkString(",")).mkString(" | ")}")
//    val recovered = scheme.join(parts.take(4).asJava)
    val recovered = join(parts.take(2).asJava)
    println(String(recovered, "UTF-8"))
  }

  {
    val random = Random(1)
    val parts = createShares(random, secret, shares, threshold)
    println(s"size of parts: ${parts.map(_.length).mkString(", ")}")
    println(s"parts: ${parts.map(_.mkString(",")).mkString(" | ")}")
    val recovered = joinShares2(parts.take(2))
    println(String(recovered, "UTF-8"))
  }

  {
    val random = Random(1)
    val parts = createShares(random, secret, shares, threshold)
    println(s"size of parts: ${parts.map(_.length).mkString(", ")}")
    println(s"parts: ${parts.map(_.mkString(",")).mkString(" | ")}")
    val recovered = joinShares(parts.take(2))
    println(String(recovered.bytes, "UTF-8"))
  }

/** @return The shares. Any `threshold` or more shares can be combined to recover the secret. */
def createShares(random: Random, secret: Array[Byte], shares: Int, threshold: Int): Seq[Array[Byte]] =
  val polynomials = secret.map { byte => GF256.generate(random, threshold - 1, byte) }
  (1 to shares).map { share => polynomials.map { polynomial => GF256.eval(polynomial, share.toByte) } }

def joinShares(shares: Seq[Array[Byte]]): Array[B] =
  require(shares.length > 1, "Not enough shares.")
  require(shares.map(_.length).distinct.size == 1, "Length of shares not uniform.")
  shares.head.indices.map { position => // The result is an array of the same size as the input.
    val points = shares.zipWithIndex.map { (share, index) =>
      Array((index + 1).toByte, share(position)).b
    }.toArray
    println(s"points: ${points.map(_.mkString(",")).mkString(" | ")}")
    tryout.GF256.interpolate(points)
  }.toArray

def joinShares2(shares: Seq[Array[Byte]]): Array[Byte] =
  require(shares.length > 1, "Not enough shares.")
  require(shares.map(_.length).distinct.size == 1, "Length of shares not uniform.")
  shares.head.indices.map { position => // The result is an array of the same size as the input.
    val points = shares.zipWithIndex.map { (share, index) =>
      Array((index + 1).toByte, share(position))
    }.toArray
    println(s"points: ${points.map(_.mkString(",")).mkString(" | ")}")
    GF256.interpolate(points)
  }.toArray

def join(parts: java.util.Map[Integer, Array[Byte]]) = {
  require(parts.size > 0, "No parts provided")
  val lengths = parts.values.stream.mapToInt((v: Array[Byte]) => v.length).distinct.toArray
  require(lengths.length == 1, "Varying lengths of part values")
  val secret = new Array[Byte](lengths(0))
  for (i <- secret.indices) {
    val points = Array.fill(parts.size)(new Array[Byte](2))
    var j = 0
    for ((key, value) <- parts.asScala) {
      points(j)(0) = key.toByte
      points(j)(1) = value(i)
      j += 1
    }
    println(s"points: ${points.map(_.mkString(",")).mkString(" | ")}")
    secret(i) = GF256.interpolate(points)
  }
  secret
}
