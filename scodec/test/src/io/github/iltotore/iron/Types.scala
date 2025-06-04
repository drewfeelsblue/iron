package io.github.iltotore.iron

import io.github.iltotore.iron.constraint.numeric.Positive

type FirstName = FirstName.T
object FirstName extends RefinedType[String, Pure]

type PosInt = PosInt.T
object PosInt extends RefinedType[Int, Positive]
