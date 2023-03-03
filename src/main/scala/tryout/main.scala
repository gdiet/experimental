package tryout

import shamir.*

import java.security.SecureRandom
import scala.jdk.CollectionConverters.*
import scala.util.chaining.scalaUtilChainingOps

@main def main(): Unit =

//  val b: Byte = 255.toByte
//  println(b)
//  println(2550 - b)
//
//  sys.exit(0)

  val random = SecureRandom()
  val scheme = com.codahale.shamir.Scheme(random, 15, 5)
  for (n <- 0 to 100) {
    print(s"$n ")
    val secret = new Array[Byte](16).tap(random.nextBytes)
    val parts = shamir.split(random, B(secret), 15, 5)
    val partsxy = parts.zipWithIndex.map((bytes, index) => Integer.valueOf(index + 1) -> bytes.bytes).toMap
    val recoveredx = scheme.join(partsxy.take(5).asJava)
    require(recoveredx.toSeq == secret.toSeq)
    sys.exit(0)
  }

def join(parts: Seq[Array[B]]): Array[B] =
  parts.head.indices.map { i =>
    B.zero
  }.toArray

def split(random: SecureRandom, secret: Array[B], shares: Int, threshold: Int): Seq[Array[B]] =
  require((shares < 256) && (shares > 0) && (threshold <= shares) && (threshold > 0)) // FIXME threshold 1 ???
  val polynomials = secret.map { b => generate(random, threshold - 1, b) }
  (1 to shares).map { share => polynomials.map { polynomial => eval(polynomial, B(share.toByte)) } }

def splitA(random: SecureRandom, secret: Array[B], shares: Int, threshold: Int): Seq[Array[Byte]] =
  val polynomials = secret.map { b => generate(random, threshold - 1, b).bytes }
  (1 to shares).map { share => polynomials.map { polynomial => evalx(polynomial, share.toByte) } }

def generate(random: SecureRandom, degree: Int, x: B): Array[B] =
  x +: new Array[B](degree - 1).tap { p => random.nextBytes(p.bytes) } :+ B(random.nextInt(1, 256).toByte)

//def generate(random: SecureRandom, degree: Int, x: B): Array[B] =
//  B(new Array[Byte](degree + 1).tap { p =>
//    random.nextBytes(p) // First & last random bytes are not used but it's easier to generate them anyway.
//    p(degree) = random.nextInt(1, 256).toByte
//    p(0) = x.byte
//  })
//
def splitx(random: SecureRandom, secret: Array[Byte], shares: Int, threshold: Int): Map[Integer, Array[Byte]] =
  splitIntoSharesx(random, secret, shares, threshold).zipWithIndex.map((bytes, index) => Integer.valueOf(index + 1) -> bytes).toMap

def splitIntoSharesx(random: SecureRandom, secret: Array[Byte], shares: Int, threshold: Int): Seq[Array[Byte]] =
  val polynomials = secret.map { byte => generatex(random, threshold - 1, byte) }
  (1 to shares).map { share => polynomials.map { polynomial => evalx(polynomial, share.toByte) } }

def generatex(random: SecureRandom, degree: Int, x: Byte): Array[Byte] =
  new Array[Byte](degree + 1).tap { p =>
    random.nextBytes(p) // First & last random bytes are not used but it's easier to generate them anyway.
    p(degree) = random.nextInt(1, 256).toByte
    p(0) = x
  }

def evalx(p: Array[Byte], x: Byte): Byte =
  eval(B(p), B(x)).byte

// Horner's method
def eval(bs: Array[B], x: B) = bs.foldRight(B.zero) { case (b, result) => result * x + b }

/*
public static byte eval(byte[] p, byte x) {
    byte result = 0;
    for (int i = p.length - 1; i >= 0; i--) {
        result = add(mul(result, x), p[i]);
    }
    return result;
}
*/
