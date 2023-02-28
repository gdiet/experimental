package shamir

import java.security.SecureRandom
import scala.util.chaining.scalaUtilChainingOps

def split(random: SecureRandom, secret: Array[B], shares: Int, threshold: Int): Seq[Array[B]] =
  require((shares < 256) && (shares > 0) && (threshold <= shares) && (threshold > 0)) // FIXME threshold 1 ???
  val polynomials = secret.map { b => generate(random, threshold - 1, b) }
  (1 to shares).map { share => polynomials.map { polynomial => eval(polynomial, B(share.toByte)) } }

def generate(random: SecureRandom, degree: Int, x: B): Array[B] =
  x +: new Array[B](degree - 1).tap { p => random.nextBytes(p.bytes) } :+ B(random.nextInt(1, 256).toByte)

// Horner's method
def eval(bs: Array[B], x: B) = bs.foldRight(B.zero) { case (b, result) => result * x + b }
