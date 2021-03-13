package demo

import demo.Models.Itinerary
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
  val ItineraryType = ObjectType("itinerary", fields[GraphQLCtx, Itinerary](
    Field(
      name = "id",
      fieldType = IntType,
      resolve = ctx => ctx.value.id
    )
  ))

  // ARGS
  val ItineraryIdsArg = Argument("itineraryIds", ListInputType(IntType))
}
