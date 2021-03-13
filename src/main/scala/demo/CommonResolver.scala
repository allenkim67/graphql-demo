package demo

import demo.Models.Booking
import sangria.execution.deferred.{Deferred, DeferredResolver}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Reference:
  * https://sangria-graphql.github.io/learn/#deferred-value-resolution
  * https://github.com/sangria-graphql/sangria/issues/376
  */
object CommonResolver {
  case class DeferredBooking(itineraryId: Int) extends Deferred[List[Booking]]

  class CommonResolver extends DeferredResolver[GraphQLCtx] {
    override def resolve(deferred: Vector[Deferred[Any]], ctx: GraphQLCtx, queryState: Any)(implicit ec: ExecutionContext): Vector[Future[Any]] = {
      val itinIds = deferred.collect { case d: DeferredBooking => d.itineraryId }
      val bookingsFuture = ctx.getBookingsByItineraryIds(itinIds)
      deferred.map {
        case d: DeferredBooking => bookingsFuture.map(bookings => bookings.filter(_.itineraryId == d.itineraryId))
      }
    }
  }
}
