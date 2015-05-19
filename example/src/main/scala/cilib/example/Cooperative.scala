package cilib
package example

object CooperativeExample {

  import Cooperative._
  import Lenses._
  import PSO._

  import syntax.step._
  import syntax.iteration._

  import scalaz._
  import scalaz.std.list._

  import scalaz.syntax.traverse._
  import scalaz.syntax.applicative._

  import spire.algebra._
  import spire.implicits._

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

  implicit val S = StateT.stateTMonadState[Position[List,Double], Step[List,Double,?]]
  implicit val R = Kleisli.kleisliApplicative[Lambda[Z => StateT[Lambda[X => Step[List,Double,X]],Position[List, Double], Z]], List[Subswarm[Mem[List,Double], List,Double]]]
  val hoist = StateT.StateMonadTrans[Position[List,Double]]

  val cognitive = Guide.pbest[Mem[List,Double],List,Double]
  val social = Guide.gbest[Mem[List,Double],List]

  val algs = (0 until 5).toList.map(i => (x: List[Int]) => gbest(
    0.729, 1.496, 1.496, cognitive, social, x)
  )

  val collections = (0 until 5).toList.map(_ =>
    Position.createCollection(PSO.createParticle(x => Entity(Mem(x,x.map(_ => 0.0)), x)))(Interval(closed(-100.0),closed(100.0))^2, 5)
  ).sequence

  val swarms = collections.map { x =>
    algs.zip(x).zip((0 until 5).toList.map(i => List(2*i, 2*i+1))).map { y =>
      ((z: List[Entity[Mem[List, Double],List,Double]]) => z.traverse[StepS[List,Double,Position[List,Double],?], Entity[Mem[List, Double], List, Double]] { a =>
        y._1._1(y._2)(z)(a)
      }, y._1._2, y._2)
    }
  }

  val a = StepS.pointR[List,Double,Position[List,Double],List[Subswarm[Mem[List,Double],List,Double]]](swarms)

  def coopAlgK =//(implicit A: Applicative[Kleisli[StepS[List,Double,Position[List, Double], ?], List[Subswarm[Mem[List,Double], List,Double]], ?]]) =
    //A.replicateM(1000,
      Iteration.syncS(cooperative[Mem[List,Double], List, Double](ContextUpdate.selective(Contribution.memoryBest)))
  //)

  val aaa = ToApplicativeOps/*[Kleisli[StepS[List,Double,Position[List,Double],?], List[Subswarm[Mem[List,Double], List,Double]],?],  List[Subswarm[Mem[List,Double], List,Double]]]*/(coopAlgK)

  //def coop = a.flatMap(x => coopAlgK.run(x))

  //val run = coop.run(Position(List.fill(10)(100.0))).run((Min, Problems.spherical)).run(RNG.fromTime)

}
