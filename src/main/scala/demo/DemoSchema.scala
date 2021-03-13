package demo

import demo.Models.{Booking, Itinerary}
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
  implicit lazy val ItineraryType = deriveObjectType[GraphQLCtx, Itinerary]()

  // ARGS
  lazy val ItineraryIdsArg = Argument("itineraryIds", ListInputType(IntType))
}
