package cilib

object Cooperative {

  import syntax.step._
  import syntax.zip._

  import scalaz.{Lens => _, _}
  import scalaz.std.list._

  import scalaz.syntax.traverse._

  import monocle._

  type Alg[S,F[_],A] = List[Entity[S,F,A]] => Entity[S,F,A] => Step[F,A,Entity[S,F,A]]
  type Algorithm[S,F[_],A] = List[Entity[S,F,A]] => StepS[F,A,Position[F,A],List[Entity[S,F,A]]]
  type Subswarm[S,F[_],A] = (Algorithm[S,F,A], List[Entity[S,F,A]], List[Int])

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

    def selective[S,F[_]:Traverse,A](
      contribution: List[Entity[S,F,A]] => Step[F,A,Position[F,A]]
    ): Subswarm[S,F,A] => StepS[F,A,Position[F,A],Position[F,A]] = {
      val S = StateT.stateTMonadState[Position[F,A], Step[F,A,?]]
      swarm => for {
        context     <- S.get
        select      = nBest(contribution)
        newContext  <- select(swarm)
        bestContext <- StepS.liftK(Fitness.compare(context, newContext))
      } yield bestContext
    }

    def nBest[S,F[_]:Traverse,A](
      contribution: List[Entity[S,F,A]] => Step[F,A,Position[F,A]]
    ): Subswarm[S,F,A] => StepS[F,A,Position[F,A],Position[F,A]] = {
      val S = StateT.stateTMonadState[Position[F,A], Step[F,A,?]]
      swarm => for {
        context     <- S.get
        contributor <- contribution(swarm._2).liftStepS
        newContext  <- Position.evalF[F,A](coopEncodePos(contributor.pos, _, swarm._3))(context).liftStepS
      } yield newContext
    }

  }

  def coopEncodePos[F[_]: Traverse,A](from: F[A], to: F[A], indices: List[Int])(implicit F: Functor[F]) =
    F.map(to.zipWithIndex) { x => from.indexOr(x._1, indices.indexOf(x._2)) }

  def evalParticle[S,F[_]:Traverse,A](entity: Particle[S,F,A], indices: List[Int]): StepS[F,A,Position[F,A],Particle[S,F,A]] = {
    val S = StateT.stateTMonadState[Position[F,A], Step[F,A,?]]
    for {
      context <- S.get
      evalled <- Entity.evalF(coopEncodePos(_: F[A], context.pos, indices))(entity).liftStepS
    } yield evalled
  }

  def cooperative[S,F[_]: Traverse,A: spire.math.Numeric](
    contextUpdate: Subswarm[S,F,A] => StepS[F,A,Position[F,A],Position[F,A]]
  )(implicit M: Memory[S,F,A]): List[Subswarm[S,F,A]] => Subswarm[S,F,A] => StepS[F,A,Position[F,A],Subswarm[S,F,A]] = {
    implicit val S = StateT.stateTMonadState[Position[F,A], Step[F,A,?]]
    swarms => swarm => {
      val (algorithm, collection, indices) = swarm
      for {
        newCollection <- algorithm(collection).map((algorithm, _, indices))
        newContext    <- contextUpdate(newCollection)
        _             <- S.put(newContext)
      } yield newCollection
    }
  }

}
