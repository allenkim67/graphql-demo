package demo

import io.circe.Json
import sangria.ast.Document

object Models {
  case class GraphQLQuery(document: Document, operationName: Option[String], variables: Json)
}
