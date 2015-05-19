package cilib
package syntax

trait Syntaxes {

  object step extends ToStepOps

  object zip extends ToExtraZipOps

  object iteration extends ToIterationOps

  object all extends ToOps

}

trait ToOps
    extends ToStepOps
    with ToExtraZipOps
    with ToIterationOps
