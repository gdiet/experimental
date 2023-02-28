package shamir

opaque type B = Byte

object B:
  def zero: B = 0
  def apply(b: Byte): B = b // FIXME review whether needed or not
  def apply(a: Array[Byte]): Array[B] = a // FIXME review whether needed or not

extension (b: B)
  def +(c: B): B = (b ^ c).toByte
  def -(c: B): B = (b ^ c).toByte
  def is0: Boolean = b == 0
  def int: Int = java.lang.Byte.toUnsignedInt(b)
  def byte: Byte = b // FIXME review whether needed or not

extension (a: Array[B]) def bytes: Array[Byte] = a
