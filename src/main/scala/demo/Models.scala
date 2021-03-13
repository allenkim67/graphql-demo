package demo

import io.circe.Json
import sangria.ast.Document

object Models {
  case class GraphQLQuery(document: Document, operationName: Option[String], variables: Json)
  case class Itinerary(id: Int)
}

object MockDB {
  import Models._

  val itineraries = Seq(
    Itinerary(1),
    Itinerary(2),
    Itinerary(3)
  )
}

class GraphQLCtx {
  import Models._
  import MockDB._

  def getItineraries(itineraryIds: Seq[Int]): Seq[Itinerary] = {
    itineraries.filter(itin => itineraryIds.contains(itin.id))
  }
}