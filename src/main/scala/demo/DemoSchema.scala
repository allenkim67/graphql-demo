package demo

import demo.Models.{Booking, Itinerary}
import sangria.execution.deferred.{DeferredResolver, Fetcher, HasId}
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
    ExcludeFields("bookingId"),
    AddFields(
      Field(
        name = "booking",
        fieldType = OptionType(BookingType),
        resolve = ctx => BookingFetcher.deferOpt(ctx.value.bookingId)
      )
    )
  )

  // ARGS
  lazy val ItineraryIdsArg = Argument("itineraryIds", ListInputType(IntType))

  // FETCHERS
  val BookingFetcher = Fetcher(
    (ctx: GraphQLCtx, ids: Seq[Int]) => ctx.getBookings(ids)
  )(HasId(_.id))

  val deferredResolver = DeferredResolver.fetchers(BookingFetcher)
}
