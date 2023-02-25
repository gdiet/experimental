package tryout

import java.security.SecureRandom
import scala.jdk.CollectionConverters.*

@main def main(): Unit =
  val random = SecureRandom()
  val scheme = com.codahale.shamir.Scheme(random, 15, 5)
  for (n <- 0 to 100) {
    print(s"$n ")
    val secret = new Array[Byte](16)
    val parts = scheme.split(secret).asScala
    val recovered = scheme.join(parts.take(5).asJava)
    require(recovered.toSeq == secret.toSeq)
  }
