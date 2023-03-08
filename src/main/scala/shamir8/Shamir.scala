package shamir8

extension (a: Int)
  /** @return The modulus (and not the remainder) of the division.
    * @see https://de.wikipedia.org/wiki/Division_mit_Rest#Modulo
    * @see https://docs.oracle.com/javase/specs/jls/se17/html/jls-15.html#jls-15.17.3 */
  def mod(n: Int): Int = ((a % n) + n) % n // Has lowest precedence when used as binary operator.

/** Find the x that multiplied with a is congruent to 1 modulo n: `a * x ≡ 1 (mod n)`.
  * @return The multiplicative inverse of a (mod n).
  * @see https://en.wikipedia.org/wiki/Modular_arithmetic */
def inverse(a: Int, n: Int): Int =
  // Easy to understand naive implementation.
  (1 until n).find(k => (k * a mod n) == 1)
    .getOrElse(throw IllegalArgumentException(s"$a not invertible in $n"))

def interpolate(data: Seq[(Int, Int)], x: Int, n: Int): Int =
  data.zipWithIndex.foldLeft(0) { case (r, (x1 -> y, i)) =>
    (data.zipWithIndex.foldLeft(y) { case (t, (x2 -> _, j)) =>
      if i == j then t else t * (x - x2) * inverse(x1 - x2, n) mod n
    } + r) mod n
  }

@main def tryout(): Unit =
  // f(x) = 209 + 49x
  // f(0) = 209
  // f(1) = 1
  // f(2) = 50
  println("expect f(0) = 209")
  println(interpolate(Seq((1, 258), (2, 50)), 0, 257))
  // f(x) = 117 + 37x + 194x^2 | mod 257
  // f(0) = 117
  // f(1) = 91
  // f(2) = 196
  // f(3) = 175
  println("expect f(0) = 117")
  println(interpolate(Seq((1, 91), (2, 196), (3, 175)), 0, 257))
  println(Integer.parseInt("G0", 17))
  println(Integer.toString(17, 17))
