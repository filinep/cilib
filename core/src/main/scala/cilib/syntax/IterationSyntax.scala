package cilib
package syntax

import cilib.Iteration._

import scalaz.{Applicative,Kleisli}

final class IterationOps[M[_], A](val self: Iteration[M,A]) extends AnyVal {
  def repeat(n: Int)(implicit A: Applicative[Kleisli[M,A,?]]): Kleisli[M,A,List[A]] =
    A.replicateM(n, self)
}

trait ToIterationOps {
  implicit def ToIterationOps[M[_], A](a: Iteration[M,A]) = new StepIdOps(a)
}
