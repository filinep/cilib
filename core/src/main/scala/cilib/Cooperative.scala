package cilib

object Cooperative {

  import syntax.step._
  import syntax.zip._

  import scalaz._
  import scalaz.std.list._

  import scalaz.syntax.traverse._

  type Collection[S,F[_],A] = List[Entity[S,F,A]]
  type Alg[S,F[_],A] = Collection[S,F,A] => Entity[S,F,A] => Step[F,A,Entity[S,F,A]]
  type Algorithm[S,F[_],A] = List[Particle[S,F,A]] => StepS[F,A,Position[F,A],List[Particle[S,F,A]]]//Iteration[F,A,Collection[S,F,A]]
  type Subswarm[S,F[_],A] = (Algorithm[S,F,A], Collection[S,F,A], List[Int])

  def coopEncodePos[F[_]: Traverse,A](from: F[A], to: F[A], indices: List[Int])(implicit F: Functor[F]) =
    F.map(to.zipWithIndex) { x => from.indexOr(x._1, indices.indexOf(x._2)) }

  def evalParticle[S,F[_]:Traverse](entity: Particle[S,F,Double], indices: List[Int]): StepS[F,Double,Position[F,Double],Particle[S,F,Double]] =
    StateT[Step[F,Double,?],Position[F,Double],Particle[S,F,Double]]((context: Position[F,Double]) =>
      Entity.evalF((x: F[Double]) => coopEncodePos(x, context.pos, indices))(entity).map((context, _))
    )

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

}
