package cilib
package syntax

import scalaz.{State,Traverse}

object zip {
  final implicit class ExtraZipOps[F[_], A](val self: F[A]) extends AnyVal {
    def zipWithIndex(implicit F: Traverse[F]) =
      F.traverseU(self)(x => State((s: Int) => (s+1, (x, s)))).eval(0)
  }
}
