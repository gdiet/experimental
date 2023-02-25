package tryout

import java.security.SecureRandom
import scala.jdk.CollectionConverters.*
import scala.util.chaining.scalaUtilChainingOps

@main def main(): Unit =
  val random = SecureRandom()
  val scheme = com.codahale.shamir.Scheme(random, 15, 5)
  for (n <- 0 to 100) {
    print(s"$n ")
    val secret = new Array[Byte](16)
    val parts = split(random, secret, 15, 5)
    val recovered = scheme.join(parts.take(5).asJava)
    require(recovered.toSeq == secret.toSeq)
  }

def split(random: SecureRandom, secret: Array[Byte], shares: Int, threshold: Int): Map[Integer, Array[Byte]] =
  splitIntoShares(random, secret, shares, threshold).zipWithIndex.map((bytes, index) => Integer.valueOf(index + 1) -> bytes).toMap

def splitIntoShares(random: SecureRandom, secret: Array[Byte], shares: Int, threshold: Int): Seq[Array[Byte]] =
  val polynomials = secret.map { byte => generate(random, threshold - 1, byte) }
  (1 to shares).map { share => polynomials.map { polynomial => com.codahale.shamir.GF256.eval(polynomial, share.toByte) } }

def generate(random: SecureRandom, degree: Int, x: Byte) =
  new Array[Byte](degree + 1).tap { p =>
    random.nextBytes(p) // First & last random bytes are not used but it's easier to generate them anyway.
    p(degree) = random.nextInt(1, 256).toByte
    p(0) = x
  }
