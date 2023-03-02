package shamir4

opaque type B = Byte

object B:
  def zero: B = 0
  def apply(b: Int): B = b.toByte
  def apply(b: Byte): B = b
  def apply(a: Array[Byte]): Array[B] = a

extension (b: B)
  def unary_~ : Int = java.lang.Byte.toUnsignedInt(b)
  def +(c: B): B = (b ^ c).toByte
  def -(c: B): B = (b ^ c).toByte
  def is0: Boolean = b == 0
  def times3: B = if b >= 0 then (b << 1 ^ b).toByte else (b << 1 ^ b ^ 0x1b).toByte


extension (a: Array[B]) def bytes: Array[Byte] = a
