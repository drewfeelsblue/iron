package io.github.iltotore.iron

import _root_.scodec.Codec
import _root_.scodec.Attempt
import _root_.scodec.Err

/**
 * Implicit [[Codec]]s for refined types.
 */
object scodec extends ScodecLowPriority:
  given [T](using mirror: RefinedType.Mirror[T], ev: Codec[mirror.IronType]): Codec[T] = ev.asInstanceOf[Codec[T]]

private trait ScodecLowPriority:
  /**
   * A [[Codec]] for refined types.
   *
   * @param codec      the [[Codec]] of the underlying type.
   * @param constraint the [[RuntimeConstraint]] implementation to test the decoded value.
   */
  given [A, B](using codec: Codec[A], constraint: RuntimeConstraint[A, B]): Codec[A :| B] =
    Codec[A].exmap(
      a => Attempt.fromEither(a.refineEither.left.map(Err(_))),
      ab => Attempt.successful(ab.asInstanceOf[A])
    )
