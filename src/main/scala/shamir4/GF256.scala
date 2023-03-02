package shamir4

import util.chaining.scalaUtilChainingOps

val EXP = { var a: B = B(0xf6); Array.fill(255) { a = a.times3; a } }

@main def tryout(): Unit =
  EXP.take(5).pipe(t => t ++ t).foreach(b => println(f"$b%02x"))


/*
var a: Byte = 1
for (_ <- 0 to 255)
  println(f"$a%02x")
  if a >= 0 then
    a = (a << 1 ^ a).toByte
  else
    a = (a << 1 ^ a ^ 0x1b).toByte
*/

/*
var a: Int = 1
val atable = new Array[Int](256)
for (c <- 0 to 255)
  atable(c) = a
  println(f"$a%02x")
  if a < 0x80 then
    a = a * 2 ^ a
  else
    a = a * 2 ^ a ^ 0x1b
  a &= 0xff
*/

/*
void generate_tables() {
        unsigned char c;
        unsigned char a = 1;
        unsigned char d;
        for(c=0;c<255;c++) {
                atable[c] = a;
                /* Multiply by three */
                d = a & 0x80;
                a <<= 1;
                if(d == 0x80) {
                        a ^= 0x1b;
                }
                a ^= atable[c];
    /* Set the log table value */
                ltable[atable[c]] = c;
        }
        atable[255] = atable[0];
        ltable[0] = 0;
}
*/
