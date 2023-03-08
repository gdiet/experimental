package shamir7

import util.chaining.scalaUtilChainingOps

extension (a: Int)
  // Return the modulus, not the remainder.
  // see https://de.wikipedia.org/wiki/Division_mit_Rest#Modulo
  def mod(n: Int): Int = ((a % n) + n) % n

def inverse(a: Int, n: Int): Int =
  (1 until n).find(k => (k * a mod n) == 1).getOrElse(throw new IllegalArgumentException(s"$a not invertible in $n"))

/* Using f(x) = secret + a * x, shares can be created as (i, f(i)).
   The secret can be recovered from any two shares as follows:
                         f(i1) - f(i2)
   secret = f(i1) - i1 * ---------------
                           i1  -  i2
   To restrict secret and shares to manageable sizes, calculations are
   done mod(257). This also means that no more than 256 different shares
   can be created. */
def s2_257(i1: Int, f1: Int, i2: Int, f2: Int): Int =
  (f1 - i1 * (f1 - f2) * inverse(i1 - i2, 257)) mod 257

case class Data(x: Int, fx: Int)

/* data a Seq of (xi, f(xi)). find f(x). */
// Lagrange interpolation
// https://www.geeksforgeeks.org/lagranges-interpolation/
// https://www.geeksforgeeks.org/copyright-information/
def interpolate_257(data: Seq[Data], x: Int) =
  var result = 0
  data.zipWithIndex.foreach { case (Data(x1, fx1), i1) =>
    var term = fx1
    data.zipWithIndex.foreach { case (Data(x2, _), i2) =>
      if i1 != i2 then term = term * (x - x2) * inverse(x1 - x2, 257) mod 257
    }
    result = result + term mod 257
  }
  result

@main def tryout(): Unit =
  // f(x) = 209 + 49x
  // f(0) = 209
  // f(1) = 1
  // f(2) = 50
  println(s2_257(1, 258, 2, 50))
  println(interpolate_257(Seq(Data(1,258), Data(2,50)), 0))
  // f(x) = 117 + 37x + 194x^2 | mod 257
  // f(0) = 117
  // f(1) = 91
  // f(2) = 196
  // f(3) = 175
  println(interpolate_257(Seq(Data(1,91), Data(2,196), Data(3,175)), 0))
