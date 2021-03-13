package demo

import io.circe.Json
import sangria.ast.Document
import scala.concurrent.Future

object Models {
  case class GraphQLQuery(document: Document, operationName: Option[String], variables: Json)
  case class Itinerary(id: Int)
  case class Booking(id: Int, itineraryId: Int)
}

object MockDB {
  import Models._

  val itineraries = Seq(
    Itinerary(1),
    Itinerary(2),
    Itinerary(3)
  )

  val bookings = Seq(
    Booking(11, itineraryId = 1),
    Booking(12, itineraryId = 1),
    Booking(13, itineraryId = 2),
    Booking(14, itineraryId = 2),
    Booking(15, itineraryId = 3),
    Booking(16, itineraryId = 3)
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

  def getBookingsByItineraryIds(itineraryIds: Seq[Int]): Future[Seq[Booking]] = {
    println("fetching bookings by itineraryIds")
    val result = bookings.filter(booking => itineraryIds.contains(booking.itineraryId))
    Future.successful(result)
  }
}