package shamir

opaque type B = Byte

object B:
  def zero: B = 0
  def apply(a: Array[Byte]): Array[B] = a

extension (b: B)
  def +(c: B): B = (b ^ c).toByte
  def -(c: B): B = (b ^ c).toByte
  def int: Int = java.lang.Byte.toUnsignedInt(b)
  def is0: Boolean = b == 0
