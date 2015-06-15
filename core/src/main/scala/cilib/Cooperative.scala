package cilib

object Cooperative {

  import syntax.step._
  import syntax.zip._

  import scalaz.{Lens => _, _}
  import scalaz.std.list._

  import scalaz.syntax.traverse._

  import monocle._

  type Algorithm[S,F[_],A] = List[Entity[S,F,A]] => Entity[S,F,A] => Step[F,A,Entity[S,F,A]]
  type AlgorithmS[S,F[_],A] = List[Entity[S,F,A]] => StepS[F,A,Position[F,A],List[Entity[S,F,A]]]
  type Subswarm[S,F[_],A] = (AlgorithmS[S,F,A], List[Entity[S,F,A]], List[Int])

  object Split {

  }

  object Contribution {

    def _entitySelector[S,F[_],A](selector: Lens[Entity[S,F,A],Position[F,A]]): List[Entity[S,F,A]] => Step[F,A,Position[F,A]] =
      collection => collection.foldLeftM[Step[F,A,?], Position[F,A]](collection.head.pos) {
          (a, c) => Step.liftK(Fitness.compare(a, selector.get(c)))
      }

    def currentBest[S,F[_],A] = _entitySelector(Lenses._position)

    def memoryBest[S,F[_],A](implicit M: Memory[S,F,A]) =
      _entitySelector(Lenses._state[S,F,A] ^|-> M._memory)

  }

  object ContextUpdate {

    def selective[S,F[_]:Traverse:SolutionRep,A](
      contribution: List[Entity[S,F,A]] => Step[F,A,Position[F,A]]
    ): Subswarm[S,F,A] => StepS[F,A,Position[F,A],Position[F,A]] = {
      val select = nBest(contribution)
      swarm => for {
        context     <- StepS.get//[F,A,Position[F,A]]
        newContext  <- select(swarm)
        bestContext <- StepS.liftK(Fitness.compare(context, newContext))
      } yield bestContext
    }

    def nBest[S,F[_]:Traverse:SolutionRep,A](
      contribution: List[Entity[S,F,A]] => Step[F,A,Position[F,A]]
    ): Subswarm[S,F,A] => StepS[F,A,Position[F,A],Position[F,A]] = {
      swarm => for {
        context     <- StepS.get
        contributor <- contribution(swarm._2).liftStepS
        newContext  <- Position.evalF[F,A](x => x)(Position(coopEncodePos(contributor.pos, context.pos, swarm._3))).liftStepS
      } yield newContext
    }

  }

  def coopEncodePos[F[_]: Traverse,A](from: F[A], to: F[A], indices: List[Int])(implicit F: Functor[F]) = {
    F.map(to.zipWithIndex) { x => if (indices.exists(x._2 == _)) from.indexOr(x._1, indices.indexOf(x._2)) else x._1}
  }

  def evalParticle[S,F[_]:Traverse,A](entity: Particle[S,F,A], indices: List[Int]): StepS[F,A,Position[F,A],Particle[S,F,A]] =
    for {
      context <- StepS.get
      evalled <- Entity.eval(coopEncodePos(_: F[A], context.pos, indices))(entity).liftStepS
    } yield evalled

  def algToCoopAlg[S,F[_]: Traverse,A](
    algorithm: (Entity[S,F,A] => Step[F,A,Entity[S,F,A]]) => Algorithm[S,F,A]
  ): List[Int] => List[Entity[S,F,A]] => Entity[S,F,A] => StepS[F,A,Position[F,A],Entity[S,F,A]] =
    indices => xs => x => StepS(
      s => {
        val eval = Cooperative.evalParticle(_: Entity[S,F,A], indices).run.eval(s)
        val coopAlg = algorithm((p: Particle[S,F,A]) => eval(p))
        coopAlg(xs)(x).map((s,_)) // TODO: Dodgy!! only works cos cooperative's eval doesn't change state
      }
    )

  def cooperative[S,F[_]: Traverse,A: spire.math.Numeric](
    contextUpdate: Subswarm[S,F,A] => StepS[F,A,Position[F,A],Position[F,A]]
  )(implicit M: Memory[S,F,A]): List[Subswarm[S,F,A]] => Subswarm[S,F,A] => StepS[F,A,Position[F,A],Subswarm[S,F,A]] =
    swarms => swarm => {
      val (algorithm, collection, indices) = swarm
      for {
        newCollection <- algorithm(collection).map((algorithm, _, indices))
        newContext    <- contextUpdate(newCollection)
        _             <- StepS.put(newContext)
      } yield newCollection
    }

}


object VEPSO {

  import Iteration._

  import syntax.step._
  import syntax.zip._

  import scalaz.{Lens => _, _}
  import scalaz.std.list._

  import scalaz.syntax.traverse._
  import scalaz.syntax.apply._

  import monocle._

  import spire.algebra._
  import spire.implicits._
  import spire.syntax.module._

  type Algorithm[S,F[_],A] = List[Entity[S,F,A]] => Entity[S,F,A] => Step[F,A,Entity[S,F,A]]
  type Subswarm[S,F[_],A] = (Guide[S,F,A] => Iteration[Step[F,A,?], List[Entity[S,F,A]]], List[Entity[S,F,A]], (Opt, Eval[F,A]))

  import PSO._
  def gbest[S,F[_]:Traverse](
    w: Double,
    c1: Double,
    c2: Double,
    cognitive: Guide[S,F,Double]
  )(
    social: Guide[S,F,Double]
  )(implicit M: Memory[S,F,Double], V: Velocity[S,F,Double], MO: Module[F[Double],Double]): List[Particle[S,F,Double]] => Particle[S,F,Double] => Step[F,Double,Particle[S,F,Double]] =
    collection => x => for {
      cog     <- cognitive(collection, x)
      soc     <- social(collection, x)
      v       <- stdVelocity(x, soc, cog, w, c1, c2)
      p       <- stdPosition(x, v)
      p2      <- evalParticle(p)
      p3      <- updateVelocity(p2, v)
      updated <- updatePBest(p3)
    } yield updated

  def otherGBest[S,F[_]](M: Memory[S,F,Double]): List[Subswarm[S,F,Double]] => Subswarm[S,F,Double] => Guide[S,F,Double] =
    xs => x => {
      val index = xs.indexOf(x) + xs.length + 1
      val subswarm = Stream.continually(xs.toStream)
        .flatten
        .drop(index)
        .head

      (_: List[Entity[S,F,Double]], x: Entity[S,F,Double]) => Guide.gbest(M)(subswarm._2, x)
    }

  def vepso[S,F[_]](implicit M: Memory[S,F,Double]): List[Subswarm[S,F,Double]] => Subswarm[S,F,Double] => Step[F,Double,Subswarm[S,F,Double]] =
    xs => x => {
      val social = otherGBest(M)(xs)(x)
      for {
        newCollection <- x._1(social).run(x._2).run(x._3._1)(x._3._2).liftStep
      } yield (x._1, newCollection, x._3)
    }

  def syncSubswarm[S,F[_],A](alg: Guide[S,F,A] => Algorithm[S,F,A]): Guide[S,F,A] => Iteration[Step[F,A,?],List[Entity[S,F,A]]] =
    guide => sync(alg(guide))

  def objective1[F[_]:Foldable:SolutionRep,A](implicit N: Numeric[A]) =
    new Unconstrained[F,A]((a: F[A]) => Valid(a.foldMap(x => N.toDouble(N.times(x, x)) - 1.0)))

  def objective2[F[_]:Foldable:SolutionRep,A](implicit N: Numeric[A]) =
    new Unconstrained[F,A]((a: F[A]) => Valid(a.foldMap(x => -N.toDouble(N.times(x, x)) + 1.0)))

  val cognitive = Guide.pbest[Mem[List,Double],List,Double]

  val gbestPSO = gbest(0.729844, 1.496180, 1.496180, cognitive) _

  def swarm = Position.createCollection(
    PSO.createParticle(x => Entity(Mem(x, x.zeroed), x))
  )(Interval(closed(-5.12),closed(5.12))^1, 20)

  val s1 = swarm.map{
    (syncSubswarm(gbestPSO), _, (Max, objective1[List,Double]))
  }.liftStep[List,Double]

  val s2 = swarm.map{
    (syncSubswarm(gbestPSO), _, (Min, objective2[List,Double]))
  }.liftStep[List,Double]

  val swarms = (s1 |@| s2) { List(_, _) }

  val b2 = Iteration.sync(vepso[Mem[List,Double],List])
  val b3 = Iteration.repeat[Step[List,Double,?], Subswarm[Mem[List,Double],List,Double]](1000, b2)
  val w = swarms flatMap (b3)
  val m = w.run(Min)(objective1)

}
