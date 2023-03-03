package shamir5

extension (b: Int)
  def #+(c: Int): Int = b ^ c
  def #-(c: Int): Int = b ^ c
  def times3: Int = if b < 128 then b << 1 ^ b else b << 1 ^ b ^ 0x11b
  def #*(c: Int): Int = if b == 0 || c == 0 then 0 else EXP(LOG(b) + LOG(c))
  def #/(c: Int): Int = b #* EXP(255 - LOG(c))

val EXP = { var a = 0xf6; Array.fill(510) { a = a.times3; a } }
// TODO is LOG(0) used at all??? If not, it might be cleanest to set it to NAN.
val LOG = 255 +: (1 to 255).map(i => EXP.indexOf(i)).toArray

def hex(b: Int): String = f"$b%02x"

@main def tryout(): Unit =
  println(EXP.map(hex).mkString(" "))
  println()
  println(LOG.map(hex).mkString(" "))
