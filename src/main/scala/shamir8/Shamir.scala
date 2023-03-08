package shamir8

extension (a: Int) {
  /** @return The modulus (not the remainder) of a divided by n.
    * @see https://de.wikipedia.org/wiki/Division_mit_Rest#Modulo
    * @see https://docs.oracle.com/javase/specs/jls/se17/html/jls-15.html#jls-15.17.3 */
  def mod(n: Int): Int = ((a % n) + n) % n // Has lowest precedence when used as binary operator.
  /** @return The result of the exponentiation `a ^ exponent modulus n` */
  def exp(exponent: Int, n: Int): Int = if a == 0 then 0 else
    val factor = if exponent >= 0 then a else inverse(a, n)
    (1 to Math.abs(exponent)).foldLeft(1)((result, _) => result * factor mod n)
}

/** Find the x that multiplied with a is congruent to 1 modulo n: `a * x ≡ 1 (mod n)`.
  * @return The multiplicative inverse of a (mod n).
  * @see https://en.wikipedia.org/wiki/Modular_arithmetic */
def inverse(a: Int, n: Int): Int =
  // Easy to understand naive implementation.
  (1 until n).find(k => (k * a mod n) == 1)
    .getOrElse(throw IllegalArgumentException(s"$a not invertible in $n"))

/** @return The Lagrange interpolation at x modulus n of the polynomial defined by the given (x,y) data values. */
def interpolate(data: Seq[(Int, Int)], x: Int, n: Int): Int =
  data.zipWithIndex.foldLeft(0) { case (r, (x1 -> y, i)) =>
    (data.zipWithIndex.foldLeft(y) { case (t, (x2 -> _, j)) =>
      if i == j then t else t * (x - x2) * inverse(x1 - x2, n) mod n
    } + r) mod n
  }

/** @return The value at x modulus n of the polynomial defined by the given coefficients. */
def calculatePolynomial(x: Int, coefficients: Array[Int], modulus: Int): Int =
  coefficients.zipWithIndex.foldLeft(0){ case (result, (coeff, index)) =>
    (result + coeff * x.exp(index, modulus)) mod modulus
  }

@main def tryout(): Unit =

  println("Using the numbers ring modulus 257 to share the secret 87")
  println("with 7 players so any 3 players can reconstruct it.")
  println("The secret is the first coefficient and thus is P(0).")
  val secret = 87
  val threshold = 3
  val players = 7
  // The first coefficient must be the secret.
  // The middle coefficients should be random numbers.
  // The last coefficient modulus 257 must not be 0.
  val coefficients = Array(secret, 4, 6)
  require((coefficients.last mod 257) != 0)
  require(coefficients.length == threshold)
  (0 to players).foreach(player =>
    println(s"P($player) = ${calculatePolynomial(player, coefficients, 257)}")
  )
  println()
  println("Recreating the secret:")
  println(interpolate(Seq(2 -> 119, 1 ->  97, 5 ->   0), 0, 257))
  println(interpolate(Seq(6 ->  70, 3 -> 153, 7 -> 152), 0, 257))
  println(interpolate(Seq(2 -> 119, 4 -> 199, 6 ->  70), 0, 257))


//  println(calculatePolynomial(3, Array(), 257))
//  println(calculatePolynomial(3, Array(40), 257))
//  println(calculatePolynomial(3, Array(40, 4), 257))
//  println(calculatePolynomial(3, Array(40, 4, 3), 257))
//  println()

//  println(3.exp(-2, 257))
//  println(3.exp(-1, 257))
//  println(3.exp(0, 257))
//  println(3.exp(1, 257))
//  println(3.exp(2, 257))
//  println(3.exp(3, 257))
//  println(3.exp(9, 257))

//  // f(x) = 209 + 49x
//  // f(0) = 209
//  // f(1) = 1
//  // f(2) = 50
//  println("expect f(0) = 209")
//  println(interpolate(Seq((1, 258), (2, 50)), 0, 257))
//  // f(x) = 117 + 37x + 194x^2 | mod 257
//  // f(0) = 117
//  // f(1) = 91
//  // f(2) = 196
//  // f(3) = 175
//  println("expect f(0) = 117")
//  println(interpolate(Seq((1, 91), (2, 196), (3, 175)), 0, 257))
//  println(Integer.parseInt("G0", 17))
//  println(Integer.toString(17, 17))
