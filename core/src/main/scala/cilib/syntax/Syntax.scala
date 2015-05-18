package cilib
package syntax

trait Syntaxes {

  object step extends ToStepOps

  object zip extends ToExtraZipOps

  object all extends ToOps

}

trait ToOps
    extends ToStepOps
    with ToExtraZipOps
