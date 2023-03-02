package shamir5

extension (b: Byte)
  def #+(c: Byte): Byte = (b ^ c).toByte
  def #-(c: Byte): Byte = (b ^ c).toByte
  def times3: Byte = if b >= 0 then (b << 1 ^ b).toByte else (b << 1 ^ b ^ 0x1b).toByte

val EXP = { var a = 0xf6.toByte; Array.fill(255) { a = a.times3; a } }
