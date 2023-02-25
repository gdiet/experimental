package tryout

object GF256:
  opaque type B = Byte
  object B { def apply(b: Byte): B = b }
  extension (b: B)
    def int: Int = java.lang.Byte.toUnsignedInt(b)
    def +(c: B): B = (b ^ c).toByte
    def -(c: B): B = (b ^ c).toByte
    def *(c: B): B = b.multiplyWith(c)
    def /(c: B): B = b.multiplyWith(EXP(255 - LOG(c.int).int))
    // Can't use the extension "*" operator internally because "*" is the Byte operator.
    private def multiplyWith(c: B): B = if b == 0 || c == 0 then 0 else EXP(LOG(b.int).int + LOG(c.int).int)
  extension (a: Array[Byte]) def b: Array[B] = a
  extension (a: Array[B]) def bytes: Array[Byte] = a

  def interpolate(points: Array[Array[B]]): B = {
    // calculate f(0) of the given points using Lagrangian interpolation
    val x: B = 0
    var y: B = 0
    for (i <- points.indices) {
      val aX = points(i)(0)
      val aY = points(i)(1)
      var li: B = 1
      for (j <- points.indices) {
        val bX = points(j)(0)
        if i != j then
          li = ??? // li * ((x - bX) / (aX - bX))
          System.out.printf("(x - bX): %d  (aX - bX): %d\n", (x - bX), (aX - bX))
          System.out.printf("li: %d  i: %d  j: %d  aX: %d  bX: %d\n", li, i, j, aX, bX)
      }
      y = ??? // y + (li * aY)
//      println(s"y: $y")
    }
    B(y.toByte)
  }

  val LOG: Array[B] = java.util.HexFormat.of.parseHex(SLOG)
  val EXP: Array[B] = java.util.HexFormat.of.parseHex(SEXP)

  def SLOG: String =
    """ff00190132021ac64bc71b6833eedf036404e00e348d81ef4c7108c8f8691cc1
      |7dc21db5f9b9276a4de4a6729ac90978652f8a05210fe12412f082453593da8e
      |968fdbbd36d0ce94135cd2f14046833866ddfd30bf068b62b325e29822889110
      |7e6e48c3a3b61e423a6b2854fa853dba2b790a159b9f5eca4ed4ace5f373a757
      |af58a850f4ead6744faee9d5e7e6ade82cd7757aeb160bf559cb5fb09ca951a0
      |7f0cf66f17c449ecd8431f2da4767bb7ccbb3e5afb60b1863b52a16caa55299d
      |97b2879061bedcfcbc95cfcd373f5bd15339843c41a26d47142a9e5d56f2d3ab
      |441192d923202e89b47cb8267799e3a5674aeddec531fe180d638c80c0f77007""".stripMargin.replaceAll("\n", "")

  private def SEXP =
    """0103050f113355ff1a2e7296a1f813355fe13848d87395a4f702060a1e2266aa
      |e5345ce43759eb266abed97090abe63153f5040c143c44cc4fd168b8d36eb2cd
      |4cd467a9e03b4dd762a6f10818287888839eb9d06bbddc7f8198b3ce49db769a
      |b5c457f9103050f00b1d2769bbd661a3fe192b7d8792adec2f7193aee92060a0
      |fb163a4ed26db7c25de73256fa153f41c35ee23d47c940c05bed2c749cbfda75
      |9fbad564acef2a7e829dbcdf7a8e89809bb6c158e82365afea256fb1c843c554
      |fc1f2163a5f407091b2d7799b0cb46ca45cf4ade798b8691a8e33e42c651f30e
      |12365aee297b8d8c8f8a8594a7f20d17394bdd7c8497a2fd1c246cb4c752f601
      |03050f113355ff1a2e7296a1f813355fe13848d87395a4f702060a1e2266aae5
      |345ce43759eb266abed97090abe63153f5040c143c44cc4fd168b8d36eb2cd4c
      |d467a9e03b4dd762a6f10818287888839eb9d06bbddc7f8198b3ce49db769ab5
      |c457f9103050f00b1d2769bbd661a3fe192b7d8792adec2f7193aee92060a0fb
      |163a4ed26db7c25de73256fa153f41c35ee23d47c940c05bed2c749cbfda759f
      |bad564acef2a7e829dbcdf7a8e89809bb6c158e82365afea256fb1c843c554fc
      |1f2163a5f407091b2d7799b0cb46ca45cf4ade798b8691a8e33e42c651f30e12
      |365aee297b8d8c8f8a8594a7f20d17394bdd7c8497a2fd1c246cb4c752f6""".stripMargin.replaceAll("\n", "")
