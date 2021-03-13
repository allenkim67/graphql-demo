package demo

import demo.Models.{Booking, Itinerary}
import sangria.schema._

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
  lazy val ItineraryType = ObjectType("itinerary", fields[GraphQLCtx, Itinerary](
    Field(
      name = "id",
      fieldType = IntType,
      resolve = ctx => ctx.value.id
    ),
    Field(
      name = "booking",
      fieldType = BookingType,
      resolve = ctx => ctx.value.booking
    )
  ))

  lazy val BookingType = ObjectType("booking", fields[GraphQLCtx, Booking](
    Field(
      name = "id",
      fieldType = IntType,
      resolve = ctx => ctx.value.id
    )
  ))

  // ARGS
  lazy val ItineraryIdsArg = Argument("itineraryIds", ListInputType(IntType))
}
