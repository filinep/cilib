package cilib
package syntax

import scalaz.{State,Traverse}

final class ExtraZipOps[F[_], A](val self: F[A]) extends AnyVal {
  def zipWithIndex(implicit F: Traverse[F]) =
    F.traverseU(self)(x => State((s: Int) => (s+1, (x, s)))).eval(0)
}

trait ToExtraZipOps {
  implicit def ToExtraZipOps[F[_], A](a: F[A]) = new ExtraZipOps(a)
}
