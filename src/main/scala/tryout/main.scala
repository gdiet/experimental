package tryout

import java.security.SecureRandom
import scala.jdk.CollectionConverters.*

@main def main(): Unit =
  val random = SecureRandom()
  val scheme = com.codahale.shamir.Scheme(random, 5, 3)
  val secret = "hello there".getBytes("UTF-8")
  println(s"size of secret: ${secret.size}")
  val parts = scheme.split(secret).asScala
  println(s"size of parts: ${parts.values.map(_.length).mkString(", ")}")
  val recovered = scheme.join(parts.take(4).asJava)
  println(String(recovered, "UTF-8"))
