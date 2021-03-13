package demo

import demo.Models.{Booking, Insurance}
import sangria.execution.deferred.{Deferred, DeferredResolver}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Reference:
  * https://sangria-graphql.github.io/learn/#deferred-value-resolution
  * https://github.com/sangria-graphql/sangria/issues/376
  */
object CommonResolver {
  case class DeferredBooking(itineraryId: Int) extends Deferred[List[Booking]]
  case class DeferredInsurance(itineraryId: Int) extends Deferred[List[Insurance]]

  class CommonResolver extends DeferredResolver[GraphQLCtx] {
    override def resolve(deferred: Vector[Deferred[Any]], ctx: GraphQLCtx, queryState: Any)(implicit ec: ExecutionContext): Vector[Future[Any]] = {
      val itinIds = deferred.collect { case d: DeferredBooking => d.itineraryId }

      val bookingsFuture = ctx.getBookingsByItineraryIds(itinIds)
      val insurancesFuture = bookingsFuture.flatMap { bookings =>
        val bookingIds = bookings.map(_.id)
        ctx.getInsuranceByBookingIds(bookingIds)
      }

      deferred.map {
        case d: DeferredBooking => bookingsFuture.map(bookings => bookings.filter(_.itineraryId == d.itineraryId))
        case d: DeferredInsurance => for {
          bookings <- bookingsFuture
          insurances <- insurancesFuture
        } yield {
          val bookingIds = bookings.filter(_.itineraryId == d.itineraryId).map(_.id)
          insurances.filter(insurance => bookingIds.contains(insurance.bookingId))
        }
      }
    }
  }
}
