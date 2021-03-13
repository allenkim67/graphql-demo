package demo

import scala.concurrent.Future
import io.circe.Json
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import sangria.marshalling.circe._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import sangria.execution.{ErrorWithResolver, Executor, QueryAnalysisError}
import demo.Models.GraphQLQuery


object Routes {
  import scala.concurrent.ExecutionContext.Implicits.global
  import FailFastCirceSupport._
  import QueryDecoder._

  val routes = path("graphql") {
    getFromResource("playground.html") ~
    post {
      entity(as[GraphQLQuery]) { query =>
        complete {
          try { executeGraphql(query) } catch { case e => println(e.getMessage); e.getMessage }
        }
      }
    }
  }

  def executeGraphql(query: GraphQLQuery): Future[(StatusCode, Json)] = Executor
    .execute(
      schema = DemoSchema.schema,
      userContext = new GraphQLCtx,
      queryAst = query.document,
      operationName = query.operationName,
      variables = query.variables
    )
    .map(OK -> _)
    .recover {
      case error: QueryAnalysisError => BadRequest -> error.resolveError
      case error: ErrorWithResolver => InternalServerError -> error.resolveError
    }
}
