package io.github.iltotore.iron

import _root_.pureconfig.ConfigSource
import _root_.pureconfig.generic.derivation.default.*
import io.github.iltotore.iron.pureconfig.given
import utest.*

object PureConfigSuite extends TestSuite:

  val tests: Tests = Tests:

    test("reader"):
      test("ironType"):
        test("success") - assert(ConfigSource.string("{ username: admin }").load[IronTypeConfig] == Right(IronTypeConfig("admin")))
        test("failure") - assert(ConfigSource.string("{ username: a }").load[IronTypeConfig].isLeft)

      test("newType"):
        test("success") - assert(ConfigSource.string("{ username: admin, password: password }").load[NewTypeConfig] == Right(NewTypeConfig(
          Username("admin"),
          Password("password")
        )))
        test("failure") - assert(ConfigSource.string("{ username: a }").load[NewTypeConfig].isLeft)
        test("failure") - assert(ConfigSource.string("{ username: admin, password: p }").load[NewTypeConfig].isLeft)

      test("map"):
        type PureString = PureString.T
        object PureString extends RefinedType[String, Pure]

        test("success") - assert(ConfigSource.string("{ key: 1 }").load[Map[PureString, Int]] == Right(Map(PureString("key") -> 1)))
        test("success") - assert(ConfigSource.string("{ key: 1 }").load[Map[String :| Pure, Int]] == Right(Map("key" -> 1)))
