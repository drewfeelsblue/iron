package io.github.iltotore.iron

import _root_.pureconfig.CollectionReaders
import _root_.pureconfig.ConfigReader
import _root_.pureconfig.error.FailureReason

object pureconfig:
  final case class RefinedConfigError(description: String) extends FailureReason

  /**
   * A [[ConfigReader]] for refined types. Decodes to the underlying type then checks the constraint.
   *
   * @param reader     the [[ConfigReader]] of the underlying type
   * @param constraint the [[RuntimeConstraint]] implementation to test the decoded value
   */
  given [A, C](using reader: ConfigReader[A], constraint: RuntimeConstraint[A, C]): ConfigReader[A :| C] =
    reader
      .emap: value =>
        value
          .refineEither[C]
          .left
          .map(RefinedConfigError(_))

  /**
   * A [[ConfigReader]] for new types. Decodes to the underlying type then checks the constraint
   *
   * @param mirror the mirror of the [[RefinedTypeOps.Mirror]]
   * @param reader the [[ConfigReader]] of the underlying type
   */
  given [A](using mirror: RefinedType.Mirror[A], reader: ConfigReader[mirror.IronType]): ConfigReader[A] =
    reader.asInstanceOf[ConfigReader[A]]

  given [V, C](using readerV: ConfigReader[V], constraint: RuntimeConstraint[String, C]): ConfigReader[Map[String :| C, V]] =
    ConfigReader.fromCursor: cur =>
      CollectionReaders.mapReader(readerV).from(cur).flatMap: map =>
        ConfigReader.Result.sequence(map.map:
          case (key, value) => cur.scopeFailure(key.refineEither[C].map(_ -> value).left.map(RefinedConfigError(_)))
        ).map(_.toMap)

  given [A, V](using mirror: RefinedType.Mirror[A], reader: ConfigReader[Map[mirror.IronType, V]]): ConfigReader[Map[A, V]] =
    reader.asInstanceOf[ConfigReader[Map[A, V]]]
