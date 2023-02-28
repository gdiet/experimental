package shamir3

opaque type B = Byte

object B:
  def zero: B = 0
  def apply(b: Byte): B = b
  def apply(a: Array[Byte]): Array[B] = a

extension (b: B)
  def unary_~ : Int = java.lang.Byte.toUnsignedInt(b)
  def +(c: B): B = (b ^ c).toByte
  def -(c: B): B = (b ^ c).toByte
  def is0: Boolean = b == 0

extension (a: Array[B]) def bytes: Array[Byte] = a
