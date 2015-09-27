package cilib

object Cooperative {

  import Iteration._

  import syntax.step._
  import syntax.zip._

  import scalaz.{NonEmptyList,Kleisli,Traverse,Functor,Lens => _}
  import scalaz.std.list._

  import scalaz.syntax.traverse._

  import monocle._

  type Algorithm[S,F[_],A] = List[Entity[S,F,A]] => Entity[S,F,A] => Step[F,A,Entity[S,F,A]]
  // type AlgorithmS[S,F[_],A] = List[Entity[S,F,A]] => StepS[F,A,Position[F,A],List[Entity[S,F,A]]]
  type AlgorithmS[S,F[_],A] = Iteration[StepS[F,A,Position[F,A],?], List[Entity[S,F,A]]]
  type Subswarm[S,F[_],A] = (List[Int] => AlgorithmS[S,F,A], List[Entity[S,F,A]], List[Int])

  object DimensionSplit {

    def _split[A](n: Int, d: NonEmptyList[Interval[A]], dims: RVar[List[Int]]): RVar[List[(List[Int], NonEmptyList[Interval[A]])]] = {
      val size = d.length / n
      val extras = d.length - n * size

      dims.map { ds =>
        val (l, r) = ds.splitAt((n - extras) * size)
        (l.sliding(size, size) ++ r.sliding(size + 1, size + 1)).toList.map {
          x =>
          val dList = d.toList
          val n = x.map(dList(_))
          (x, n match {
            case h :: t => NonEmptyList.nel(h, t)
            case _      => sys.error("Empty stuff")
          })
        }
      }
    }

    def random[A](n: Int, d: NonEmptyList[Interval[A]]) =
      _split(n, d, RVar.shuffle((0 until d.length).toList))

    def sequential[A](n: Int, d: NonEmptyList[Interval[A]]) =
      _split(n, d, RVar.point((0 until d.length).toList))

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
        context     <- StepS.get
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
        newContext  <- Step.evalF[F,A](Position(coopEncodePos(contributor.pos, context.pos, swarm._3))).liftStepS
      } yield newContext
    }

  }

  def coopEncodePos[F[_]: Traverse,A](from: F[A], to: F[A], indices: List[Int])(implicit F: Functor[F]) = {
    F.map(to.zipWithIndex) { x => if (indices.exists(x._2 == _)) from.indexOr(x._1, indices.indexOf(x._2)) else x._1}
  }

  def evalParticle[S,F[_]:Traverse,A](entity: Particle[S,F,A], indices: List[Int]): StepS[F,A,Position[F,A],Particle[S,F,A]] =
    for {
      context <- StepS.get
      evalled <- Entity.evalF(coopEncodePos(_: F[A], context.pos, indices))(entity).liftStepS
    } yield evalled

  def algToCoopAlg[S,F[_]: Traverse,A](
    algorithm: (Entity[S,F,A] => Step[F,A,Entity[S,F,A]]) => Algorithm[S,F,A]
  ): List[Int] => List[Entity[S,F,A]] => Entity[S,F,A] => StepS[F,A,Position[F,A],Entity[S,F,A]] =
    indices => xs => x => StepS(
      s => {
        val eval = Cooperative.evalParticle(_: Entity[S,F,A], indices).run.eval(s)
        val coopAlg = algorithm((p: Entity[S,F,A]) => eval(p))
        coopAlg(xs)(x).map((s,_)) // TODO: Dodgy!! only works cos cooperative's eval doesn't change state
      }
    )

  def coopSync[S,F[_],A](
    algorithm: List[Int] => List[Entity[S,F,A]] => Entity[S,F,A] => StepS[F,A,Position[F,A],Entity[S,F,A]]
  ): List[Int] => Iteration[StepS[F,A,Position[F,A],?], List[Entity[S,F,A]]] =
    indices => Iteration.syncS(algorithm(indices))

  def coopAsync[S,F[_],A](
    algorithm: List[Int] => List[Entity[S,F,A]] => Entity[S,F,A] => StepS[F,A,Position[F,A],Entity[S,F,A]]
  ): List[Int] => Iteration[StepS[F,A,Position[F,A],?], List[Entity[S,F,A]]] =
    indices => Iteration.asyncS(algorithm(indices))

  def cooperative[S,F[_]: Traverse,A: spire.math.Numeric](
    contextUpdate: Subswarm[S,F,A] => StepS[F,A,Position[F,A],Position[F,A]]
  )(implicit M: Memory[S,F,A]): List[Subswarm[S,F,A]] => Subswarm[S,F,A] => StepS[F,A,Position[F,A],Subswarm[S,F,A]] =
    swarms => swarm => {
      val (algorithm, collection, indices) = swarm
      for {
        newCollection <- algorithm(indices).run(collection).map((algorithm, _, indices))
        newContext    <- contextUpdate(newCollection)
        _             <- StepS.put(newContext)
      } yield newCollection
    }

  def subEntity[S,F[_],A](
    algorithm: List[Int] => List[Entity[S,F,A]] => Entity[S,F,A] => StepS[F,A,Position[F,A],Entity[S,F,A]],
    encoder: List[Int] => Entity[S,F,A] => Entity[S,F,A],
    decoder: List[Int] => Entity[S,F,A] => Entity[S,F,A]
  ): List[Int] => List[Entity[S,F,A]] => Entity[S,F,A] => StepS[F,A,Position[F,A],Entity[S,F,A]] =
  indices => collection => entity => {
    algorithm(indices)(collection.map(encoder(indices)(_)))(encoder(indices)(entity)).map(decoder(indices)(_))
  }

  def subPosition[F[_],A](indices: List[Int], pos: Position[F,A]): Position[F,A] = {
    pos
  }

  def cooperativeLS[S,F[_],A](
    algorithm: Iteration[StepS[F,A,Position[F,A],?], List[Subswarm[S,F,A]]]
  ): Iteration[StepS[F,A,Position[F,A],?], List[Subswarm[S,F,A]]] = {
    val K = Kleisli.kleisliMonadReader[StepS[F,A,Position[F,A],?], List[Subswarm[S,F,A]]]
    val hoist = Kleisli.kleisliMonadTrans[List[Subswarm[S,F,A]]]

    for {
      //swarms   <- K.ask
      context1 <- hoist.liftMU(StepS.get[F,A,Position[F,A]])
      updated  <- algorithm
      context2 <- hoist.liftMU(StepS.get[F,A,Position[F,A]])
      improved <- hoist.liftMU(
        StepS.liftK[F,A,Position[F,A],Boolean](
          Fitness.compare(context1, context2).map(_ eq context2)
        )
      )
      newSwarms = if (improved) updated else updated
    } yield newSwarms
  }



  case class Pop(X: Array[Array[Double]], Y: Array[Array[Double]])

  def encode[S](f: (Array[Double], Array[Double]) => Entity[S,List,Double], indices: List[List[Int]]): StepS[List,Double,Pop,List[List[Entity[S,List,Double]]]] =
    for {
      state <- StepS.get
    } yield {
      indices.map { is =>
        state.X.zip(state.Y).toList.map { vals =>
          val X = vals._1
          val Y = vals._2
          val x = is.map(i => X(i))
          val y = is.map(i => Y(i))
          f(x.toArray, y.toArray)
        }
      }
    }

  def decode[S](f: Entity[S,List,Double] => (Array[Double], Array[Double]), indices: List[List[Int]]): List[List[Entity[S,List,Double]]] => StepS[List,Double,Pop,Unit] = {
    collections => for {
      state <- StepS.get
      _     <- StepS.put {
        collections.zip(indices).map { vals =>
          val ents = vals._1
          val ind = vals._2
          
        }
        state
      }
    } yield ()
  }

  def coop(s: List[Int]): StepS[List,Double,Pop,Unit] = {
    for {
      state <- StepS.get

    } yield ()
  }


}
