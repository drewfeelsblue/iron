package io.github.iltotore.iron

import _root_.scodec.Codec
import io.github.iltotore.iron.FirstName
import io.github.iltotore.iron.constraint.numeric.Positive
import io.github.iltotore.iron.scodec.given
import utest.*

object ScodecSuite extends TestSuite:
  val tests: Tests = Tests:
    test("Scodec's Codec is resolved for new types"):
      Codec[FirstName]

    test("Scodec's Codec is resolved for Double iron types"):
      Codec[Double :| Positive]
