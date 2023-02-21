package tryout

import com.codahale.shamir.*

import java.util.Random
import scala.jdk.CollectionConverters.*

@main def main(): Unit =
  val secret = "hello".getBytes("UTF-8")
  val shares = 5
  val threshold = 3

  // TODO use SecureRandom again
  val random = Random(1)
  val scheme = Scheme(random, shares, threshold)
  println(s"size of secret: ${secret.size}")

  {
    val parts = scheme.split(secret).asScala
    println(s"size of parts: ${parts.values.map(_.length).mkString(", ")}")
    println(s"parts: ${parts.values.map(_.mkString(",")).mkString(" | ")}")
    val recovered = scheme.join(parts.take(4).asJava)
    println(String(recovered, "UTF-8"))
  }

  {
    val random = Random(1)
    val parts = splitIntoShares(random, secret, shares, threshold)
    println(s"size of parts: ${parts.map(_.length).mkString(", ")}")
    println(s"parts: ${parts.map(_.mkString(",")).mkString(" | ")}")
  }

def splitIntoShares(random: Random, secret: Array[Byte], shares: Int, threshold: Int): Seq[Array[Byte]] =
  val polynomials = secret.map { byte => GF256.generate(random, threshold - 1, byte) }
  (1 to shares).map { share => polynomials.map { polynomial => GF256.eval(polynomial, share.toByte) } }
