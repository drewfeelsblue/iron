package io.github.iltotore.iron.formJsoniter

import com.github.plokhotnyuk.jsoniter_scala.core.*
import com.github.plokhotnyuk.jsoniter_scala.macros.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.jsoniter.makeCodec
import io.github.iltotore.iron.{*, given}

import scala.util.Try

type Username = DescribedAs[
  Alphanumeric & MinLength[3] & MaxLength[10],
  "Username should be alphanumeric and have a length between 3 and 10"
]

type Password = DescribedAs[
  Match["[A-Za-z].*[0-9]|[0-9].*[A-Za-z]"] & MinLength[6] & MaxLength[20],
  "Password must contain atleast a letter, a digit and have a length between 6 and 20"
]

type Age = DescribedAs[Greater[0], "Age should be strictly positive"]

/**
 * A basic Account with a name, a password and an age.
 *
 * @param name an alphanumeric String with a length between 3 and 10.
 * @param password an String containing atleast a letter, a digit, with a length between 6 and 20.
 * @param age a strictly positive Int.
 */
case class Account(name: String :| Username, password: String :| Password, age: Int :| Age)

object Account:
  given JsonValueCodec[Account] = JsonCodecMaker.make

  private val readerConfig = ReaderConfig.withAppendHexDumpToParseException(false)

  def fromJsonString(jsonString: String): Either[String, Account] =
    Try(readFromString[Account](jsonString, readerConfig)).toEither.left.map(_.getMessage)

  extension (account: Account)
    def asJsonString: String = writeToString(account)
