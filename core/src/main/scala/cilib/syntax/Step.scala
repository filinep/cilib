package cilib
package syntax

import scalaz.{IndexedStateT,LensFamily}

object step {
  final implicit class StepId[A](val self: A) extends AnyVal {
    def liftStep[F[_], B]: Step[F,B,A] = Step.point(self)
    //def liftStepS[F[_], B, S]: StepS[F,B,S,A] = StepS.point(self)
  }

  final implicit class StepRVar[A](val self: RVar[A]) extends AnyVal {
    def liftStep[F[_], B]: Step[F,B,A] = Step.pointR(self)
    def liftStepS[F[_], B, S]: StepS[F,B,S,A] = StepS.pointR(self)
  }

/*  final implicit class StepOps[F[_],A,B](val self: Step[F,A,B]) extends AnyVal {
    def liftStepS[S]: StateT[Step[F,A,?], S, B] = StepS.pointK(self)
 }*/

  // final implicit class StepSOps[F[_],A,S,B](val self: StepS[F,A,S,B]) extends AnyVal {
  //   def zoom[S0, S3](l: LensFamily[S0, S3, S, S]): StepS[F,A,S,B] =
  //     self.run.zoom(l: LensFamily[S0, S3, S, S])
  // }
}

