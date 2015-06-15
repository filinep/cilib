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
