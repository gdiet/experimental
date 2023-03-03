package shamir5

extension (b: Byte)
  def int: Int = java.lang.Byte.toUnsignedInt(b)
  def #+(c: Byte): Byte = (b ^ c).toByte
  def #-(c: Byte): Byte = (b ^ c).toByte
  def times3: Byte = if b >= 0 then (b << 1 ^ b).toByte else (b << 1 ^ b ^ 0x1b).toByte

val EXP = { var a = 0xf6.toByte; Array.fill(510) { a = a.times3; a } }
// TODO is LOG(0) used at all??? If not, it might be cleanest to set it to NAN.
val LOG = (0 to 255).map(i => EXP.indexOf(i.toByte).toByte).toArray

def hex(b: Byte): String = f"$b%02x"

@main def tryout(): Unit =
  println(EXP.map(hex).mkString(" "))
  println()
  println(LOG.map(hex).mkString(" "))
