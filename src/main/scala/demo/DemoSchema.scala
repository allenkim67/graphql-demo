package demo

import demo.CommonResolver.DeferredBooking
import demo.Models.{Booking, Itinerary}
import sangria.execution.deferred.{DeferredResolver, Fetcher, HasId, Relation, RelationIds}
import sangria.schema._
import sangria.macros.derive._


object DemoSchema {
  lazy val schema = Schema(ObjectType("Query", fields[GraphQLCtx, Unit](
    Field(
      name = "itineraries",
      fieldType = ListType(ItineraryType),
      arguments = ItineraryIdsArg :: Nil,
      resolve = ctx => ctx.ctx.getItineraries(ctx.arg(ItineraryIdsArg))
    )
  )))

  // TYPES
  implicit lazy val BookingType = deriveObjectType[GraphQLCtx, Booking]()
  implicit lazy val ItineraryType = deriveObjectType[GraphQLCtx, Itinerary](
    AddFields(
      Field(
        name = "booking",
        fieldType = ListType(BookingType),
        resolve = ctx => DeferredBooking(ctx.value.id)
      )
    )
  )

  // ARGS
  lazy val ItineraryIdsArg = Argument("itineraryIds", ListInputType(IntType))

  // FETCHERS
  lazy val BookingFetcher = Fetcher.rel(
    (ctx: GraphQLCtx, ids: Seq[Int]) => ctx.getBookings(ids),
    (ctx: GraphQLCtx, idsGetter: RelationIds[Booking]) => ctx.getBookingsByItineraryIds(idsGetter(BookingsByItineraryId))
  )(HasId(_.id))

  lazy val deferredResolver = DeferredResolver.fetchers(BookingFetcher)

  // RELATIONS
  lazy val BookingsByItineraryId =
    Relation[Booking, Int]("bookingsByItineraryId", booking => Seq(booking.itineraryId))
}
