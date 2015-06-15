package cilib
package example

object CooperativeExample {

  import Cooperative._
  import Lenses._
  import PSO._

  import syntax.step._

  import scalaz._
  import scalaz.std.list._

  import scalaz.syntax.traverse._
  import scalaz.syntax.applicative._

  import spire.algebra._
  import spire.implicits._

  def gbest1[S,F[_]:Traverse](
    w: Double,
    c1: Double,
    c2: Double,
    cognitive: Guide[S,F,Double],
    social: Guide[S,F,Double]
  )(
    evalParticle: Particle[S,F,Double] => Step[F,Double,Particle[S,F,Double]]
  )(
    implicit M: Memory[S,F,Double], V: Velocity[S,F,Double], MO: Module[F[Double],Double]
  ): List[Particle[S,F,Double]] => Particle[S,F,Double] => Step[F,Double,Particle[S,F,Double]] =
    collection => x => for {
      cog     <- cognitive(collection, x)
      soc     <- social(collection, x)
      v       <- stdVelocity(x, soc, cog, w, c1, c2)
      p       <- stdPosition(x, v)
      p2      <- evalParticle(p)
      p3      <- updateVelocity(p2, v)
      updated <- updatePBest(p3)
    } yield updated

  def gbest[S,F[_]:Traverse](
    w: Double,
    c1: Double,
    c2: Double,
    cognitive: Guide[S,F,Double],
    social: Guide[S,F,Double],
    indices: List[Int]
  )(implicit M: Memory[S,F,Double], V: Velocity[S,F,Double], MO: Module[F[Double],Double]): List[Particle[S,F,Double]] => Particle[S,F,Double] => StepS[F,Double, Position[F,Double], Particle[S,F,Double]] =
    collection => x => for {
      cog     <- cognitive(collection, x).liftStepS
      soc     <- social(collection, x).liftStepS
      v       <- stdVelocity(x, soc, cog, w, c1, c2).liftStepS
      p       <- stdPosition(x, v).liftStepS
      p2      <- Cooperative.evalParticle(p, indices)
      p3      <- updateVelocity(p2, v).liftStepS
      updated <- updatePBest(p3).liftStepS
    } yield updated

  type StepS_[A] = StepS[List,Double,Position[List, Double],A]
  type Subswarm_ = Subswarm[Mem[List,Double],List,Double]
  type Entity_ = Entity[Mem[List, Double], List, Double]

  val cognitive = Guide.pbest[Mem[List,Double],List,Double]
  val social = Guide.gbest[Mem[List,Double],List,Double]

  val algs = (0 until 10).toList.map(i => (x: List[Int]) => gbest(
    0.729, 1.496, 1.496, cognitive, social, x)
  )

  val collections = (0 until 5).toList.traverse(_ =>
    Position.createCollection(PSO.createParticle(x => Entity(Mem(x,x.map(_ => 0.0)), x)))(Interval(closed(-100.0),closed(100.0))^2, 5)
  )

  val swarms = collections.map { x =>
    algs.zip(x).zip((0 until 5).toList.map(i => List(2*i, 2*i+1))).map { y =>
      ((z: List[Entity[Mem[List, Double],List,Double]]) => z.traverse[StepS_, Entity_] { a =>
        y._1._1(y._2)(z)(a)
      }, y._1._2, y._2)
    }
  }.liftStepS[List,Double,Position[List,Double]]

  def coopAlg = cooperative[Mem[List,Double], List, Double](ContextUpdate.selective(Contribution.memoryBest))

  def coopAlgK = Iteration.repeat[StepS_,Subswarm_](1000,
    Iteration.asyncS(coopAlg)
  )

  def coop = swarms.flatMap(x => coopAlgK(x))

  val run = coop.run(Position(List.fill(10)(100.0))).run(Min)(Problems.spherical).run(RNG.fromTime)

}
