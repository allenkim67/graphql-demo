package demo

import demo.Models.GraphQLQuery
import io.circe.{Decoder, HCursor, Json}
import sangria.parser.QueryParser

import scala.util.{Failure, Success}

/**
  * Custom circe decoder to parse request using Sangria's QueryParser.
  *
  * https://circe.github.io/circe/codecs/custom-codecs.html
  * https://sangria-graphql.github.io/learn/#query-parser-and-renderer
  */
object QueryDecoder {
  implicit val decodeQuery: Decoder[GraphQLQuery] = (c: HCursor) => for {
    query         <- c.downField("query").as[String]
    operationName <- c.downField("operationName").as[Option[String]]
    variables     <- c.downField("variables").as[Json]
  } yield {
    QueryParser.parse(query) match {
      case Success(document) ⇒ GraphQLQuery(document, operationName, variables)
      case Failure(error) ⇒ throw error
    }
  }
}