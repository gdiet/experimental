package ants

def info(message: String): Unit = println(s"INFO: $message")

/** @return The positive modulo */
def mod(dividend: Int, divisor: Int): Int = (dividend % divisor + divisor) % divisor
