package cilib

import _root_.scala.Predef.{any2stringadd => _, _}

import scalaz._

import monocle.syntax._
import Position._

import spire.algebra._
import spire.implicits._
import spire.syntax.module._

object PSO {
  import Lenses._

  // Constrain this better - Not numeric. Operation for vector addition
  def stdPosition[S,F[_],A](
    c: Particle[S,F,A],
    v: Position[F,A]
  )(implicit A: Module[F[A],A]): Step[F,A,Particle[S,F,A]] =
    Step.point(_position.modify((_: Position[F,A]) + v)(c))

  // Dist \/ Double (scalar value)
  // This needs to be fleshed out to cater for the parameter constants // remember to extract Dists
  def stdVelocity[S,F[_]:Traverse](
    entity: Particle[S,F,Double],
    social: Position[F,Double],
    cognitive: Position[F,Double],
    w: Double,
    c1: Double,
    c2: Double
  )(implicit V: Velocity[S,F,Double], M: Module[F[Double],Double], F:Field[Double]): Step[F,Double,Position[F,Double]] =
    Step.pointR(for {
      cog <- (cognitive - entity.pos) traverse (x => Dist.stdUniform.map(_ * x))
      soc <- (social - entity.pos)    traverse (x => Dist.stdUniform.map(_ * x))
    } yield (w *: V._velocity.get(entity.state)) + (c1 *: cog) + (c2 *: soc))

  // Step to evaluate the particle, without any modifications
  def evalParticle[S,F[_]:Foldable](entity: Particle[S,F,Double]) =
    Entity.evalF[S,F,Double](x => x)(entity)

  def updatePBest[S,F[_]](p: Particle[S,F,Double])(implicit M: Memory[S,F,Double]): Step[F,Double,Particle[S,F,Double]] = {
    val pbestL = M._memory
    Step.liftK(Fitness.compare(p.pos, (p.state applyLens pbestL).get).map(x => Entity(p.state applyLens pbestL set x, p.pos)))
  }

  def updateVelocity[S,F[_]](p: Particle[S,F,Double], v: Position[F,Double])(implicit V: Velocity[S,F,Double]): Step[F,Double,Particle[S,F,Double]] =
    Step.pointR(RVar.point(Entity(p.state applyLens V._velocity set v, p.pos)))

  def singleComponentVelocity[S,F[_]:Traverse](
    entity: Particle[S,F,Double],
    component: Position[F,Double],
    w: Double,
    c: Double
  )(implicit V: Velocity[S,F,Double], M: Memory[S,F,Double], MO: Module[F[Double],Double]): Step[F,Double,Position[F,Double]] = {
    //val (state,pos) = entity
    Step.pointR(for {
      comp <- (component - entity.pos) traverse (x => Dist.stdUniform.map(_ * x))
    } yield (w *: V._velocity.get(entity.state)) + (c *: comp))
  }

  case class GCParams(p: Double = 1.0, successes: Int = 0, failures: Int = 0, e_s: Double = 15, e_f: Double = 5)
  def gcVelocity[S,F[_]:Traverse](
    entity: Particle[S,F,Double],
    nbest: Position[F,Double],
    w: Double,
    s: GCParams
  )(implicit V: Velocity[S,F,Double], M: Module[F[Double],Double]): Step[F,Double,Position[F,Double]] =
    Step.pointR(
      nbest traverse (_ => Dist.stdUniform.map(x => s.p * (1 - 2*x))) map (a =>
        -1.0 *: entity.pos + nbest + w *: V._velocity.get(entity.state) + a
      ))

  def barebones[S,F[_]:Monad:Traverse:Zip](p: Particle[S,F,Double], global: Position[F,Double])(implicit M: Memory[S,F,Double]) =
    Step.pointR {
      val pbest = M._memory.get(p.state)
      val zipped = pbest.zip(global)
      val sigmas = zipped map { case (x,y) => math.abs(x - y) }
      val means  = zipped map { case (x,y) => (x + y) / 2.0 }

      (means zip sigmas) traverse (x => Dist.gaussian(x._1, x._2))
    }

  def quantum[S,F[_]:Traverse](
    collection: List[Particle[S,F,Double]],
    x: Particle[S,F,Double],
    center: Position[F,Double],
    r: Double
  )(implicit M: Module[F[Double],Double]): Step[F,Double,Position[F,Double]] =
    Step.pointR(
      for {
        u <- Dist.uniform(0,1)
        rand_x <- x.pos.traverse(_ => Dist.stdNormal)
      } yield {
        import scalaz.syntax.foldable._
        val sum_sq = rand_x.pos.foldLeft(0.0)((a,c) => a + c*c)
        val scale: Double = r * math.pow(u, 1.0 / x.pos.pos.length) / math.sqrt(sum_sq)
        (scale *: rand_x) + center
      }
    )

  def acceleration[S,F[_]:Monad]( // Why must this be a Monad?? Surely Functor is enough?
    collection: List[Particle[S,F,Double]],
    x: Particle[S,F,Double],
    distance: (Position[F,Double], Position[F,Double]) => Double,
    rp: Double,
    rc: Double
  )(implicit C: Charge[S], MO: Module[F[Double],Double]): Step[F,Double,Position[F,Double]] = {
    def charge(x: Particle[S,F,Double]) =
      C._charge.get(x.state)

    Step.point(
      collection
        .filter(z => charge(z) > 0.0)
        .foldLeft(x.pos.map(_ => 0.0)) { (p1, p2) => {
          val d = distance(x.pos, p2.pos)
          if (d > rp || (x eq p2)) p1
          else (charge(x) * charge(p2) / (d * (if (d < rc) (rc * rc) else (d * d)))) *: (x.pos - p2.pos) + p1
      }})
  }

  // Naming?
  def replace[S,F[_]](entity: Particle[S,F,Double], p: Position[F,Double]): Step[F,Double,Particle[S,F,Double]] =
    Step.point(entity applyLens _position set p)

  def createParticle[S,F[_]](f: Position[F,Double] => Particle[S,F,Double])(pos: Position[F,Double]): Particle[S,F,Double] =
    f(pos)
}

object Guide {

  def identity[S,F[_],A]: Guide[S,F,A] =
    (_, x) => Step.point(x.pos)

  def pbest[S,F[_],A](implicit M: Memory[S,F,A]): Guide[S,F,A] =
    (_, x) => Step.point(M._memory.get(x.state))

  def nbest[S,F[_]](selection: Selection[Particle[S,F,Double]])(implicit M: Memory[S,F,Double]): Guide[S,F,Double] = {
    (collection, x) => new Step(Kleisli[RVar, (Opt,Eval[F,Double]), Position[F,Double]]((o: (Opt,Eval[F,Double])) => RVar.point {
      selection(collection, x).map(e => M._memory.get(e.state)).reduceLeft((a, c) => Fitness.compare(a, c) run o._1)
    }))
  }

  def gbest[S,F[_]](implicit M: Memory[S,F,Double]): Guide[S,F,Double] = nbest((c, _) => c)
  def lbest[S,F[_]](n: Int)(implicit M: Memory[S,F,Double]) = nbest(Selection.indexNeighbours[Particle[S,F,Double]](n))

}

object Multiswarm {

  import Iteration._
  import PSO._

  import syntax.step._

  import scalaz._
  import scalaz.std.list._
  import scalaz.syntax.applicative._
  import scalaz.syntax.apply._
  import scalaz.syntax.monad._
  import scalaz.syntax.functor._
  import scalaz.syntax.traverse._
  import scalaz.syntax.zip._

  def spherical[F[_]:Foldable:SolutionRep,A](implicit N: Numeric[A]) =
    new Unconstrained[F,A]((a: F[A]) => Valid(a.foldMap(x => N.toDouble(N.times(x, x)))))

  type Collection[S,F[_],A] = List[Entity[S,F,A]]
  type Alg[S,F[_],A] = Collection[S,F,A] => Entity[S,F,A] => Step[F,A,Entity[S,F,A]]
  type Algorithm[S,F[_],A] = List[Particle[S,F,A]] => StepS[F,A,Position[F,A],List[Particle[S,F,A]]]//Iteration[F,A,Collection[S,F,A]]
  type Subswarm[S,F[_],A] = (Algorithm[S,F,A], Collection[S,F,A], List[Int])

  import Lenses._

  implicit class ExtraZip[F[_], A](f: F[A]) {
    def zipWithIndex(implicit F: Traverse[F]) =
      F.traverseU(f)(x => State((s: Int) => (s+1, (x, s)))).eval(0)
  }

  def coopEncodePos[F[_]: Traverse,A](from: F[A], to: F[A], indices: List[Int])(implicit F: Functor[F]) =
    F.map(to.zipWithIndex) { x => from.indexOr(x._1, indices.indexOf(x._2)) }

  def evalParticle[S,F[_]:Traverse](entity: Particle[S,F,Double], indices: List[Int]): StateT[Step[F,Double,?], Position[F,Double], Particle[S,F,Double]] =
    StateT[Step[F,Double,?],Position[F,Double],Particle[S,F,Double]]((context: Position[F,Double]) => //Step((e: (Opt,Eval[F,Double])) => {
      Entity.evalF((x: F[Double]) => coopEncodePos(x, context.pos, indices))(entity).map((context, _)))//     entity.pos.mapEval(e._2)(x => coopEncodePos(x, context.pos, indices)).map(x => _position.set(x)(entity))
    //}).map((context, _)))

  def gbest[S,F[_]:Traverse](
    w: Double,
    c1: Double,
    c2: Double,
    cognitive: Guide[S,F,Double],
    social: Guide[S,F,Double],
    indices: List[Int]
  )(implicit M: Memory[S,F,Double], V: Velocity[S,F,Double], MO: Module[F[Double],Double]): List[Particle[S,F,Double]] => Particle[S,F,Double] => StepS[F,Double, Position[F,Double], Particle[S,F,Double]] = {
    val S = StateT.stateTMonadState[Position[F,Double], Step[F,Double,?]]
    val hoist = StateT.StateMonadTrans[Position[F,Double]]
    collection => x => for {
      pos     <- S.get
      cog     <- hoist.liftMU(cognitive(collection, x))
      soc     <- hoist.liftMU(social(collection, x))
      v       <- hoist.liftMU(stdVelocity(x, soc, cog, w, c1, c2))
      p       <- hoist.liftMU(stdPosition(x, v))
      p2      <- evalParticle(p, indices)
      p3      <- hoist.liftMU(updateVelocity(p2, v))
      updated <- hoist.liftMU(updatePBest(p3))
    } yield updated
  }

  def cooperative[S,F[_]: Traverse,A: spire.math.Numeric](implicit M: Memory[S,F,A]): List[Subswarm[S,F,A]] => Subswarm[S,F,A] => StepS[F,A,Position[F,A],Subswarm[S,F,A]] = {
    implicit val S = StateT.stateTMonadState[Position[F,A], Step[F,A,?]]
    swarms => swarm => {
      val (algorithm, collection, indices) = swarm
      for {
        context       <- S.get
        newCollection <- algorithm(collection).map((algorithm, _, indices))
        bestEntity    <- newCollection._2.foldLeftM[StepS[F,A,Position[F,A],?], Position[F,A]](newCollection._2.head.pos) {
          (a, c) => StepS.liftK(Fitness.compare(a, M._memory.get(c.state)))
        }
        newContext    <- Position.evalF[F,A](coopEncodePos(bestEntity.pos, _, indices))(context).liftStepS
        bestContext   <- StepS.liftK(Fitness.compare(context, newContext))
        _             <- S.put(bestContext)
      } yield newCollection
    }
  }

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
      ((z: Collection[Mem[List, Double],List,Double]) => z.traverse[StepS[List,Double,Position[List,Double],?], Entity[Mem[List, Double], List, Double]] { a =>
        y._1._1(y._2)(z)(a)
      }, y._1._2, y._2)
    }
  }

  val a = StepS.pointR[List,Double,Position[List,Double],List[Subswarm[Mem[List,Double],List,Double]]](swarms)

  def coopAlgK(implicit A: Applicative[Kleisli[StepS[List,Double,Position[List, Double], ?], List[Subswarm[Mem[List,Double], List,Double]], ?]]) =
    Iteration.syncS(cooperative[Mem[List,Double], List, Double])

  def coop = a.flatMap(x => coopAlgK.run(x))

  val run = coop.run(Position(List.fill(10)(100.0))).run((Min, spherical)).run(RNG.fromTime)

}


/*
next pso work:
==============
- vepso / dvepso (robert afer moo & dmoo)
- cooperative & variations
- heterogenous filipe

- niching (less important for now)

commonalities:
- subswarms

functions:
- moo & dmoo functions (benchmarks) robert
*/

/*
 Stopping conditions:
 ====================
 iteration based stopping conditions
 fitness evaluations
 dimension based updates
 # of position updates (only defined if change is some epislon based on the position vector)
 # of dimensional updates > epsilon
 */
