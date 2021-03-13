package demo

import io.circe.Json
import sangria.ast.Document
import scala.concurrent.Future

object Models {
  case class GraphQLQuery(document: Document, operationName: Option[String], variables: Json)
  case class Itinerary(id: Int, bookingId: Int)
  case class Booking(id: Int)
}

object MockDB {
  import Models._

  val itineraries = Seq(
    Itinerary(1, bookingId = 11),
    Itinerary(2, bookingId = 12),
    Itinerary(3, bookingId = 13)
  )

  val bookings = Seq(
    Booking(11),
    Booking(12),
    Booking(13),
  )
}

class GraphQLCtx {
  import Models._
  import MockDB._

  def getItineraries(itineraryIds: Seq[Int]): Seq[Itinerary] = {
    println("fetching itineraries")
    itineraries.filter(itin => itineraryIds.contains(itin.id))
  }

  def getBooking(bookingId: Int): Option[Booking] = {
    println("fetching booking")
    bookings.find(booking => booking.id == bookingId)
  }

  def getBookings(bookingIds: Seq[Int]): Future[Seq[Booking]] = {
    println("fetching bookings")
    val result = bookings.filter(booking => bookingIds.contains(booking.id))
    Future.successful(result)
  }
}