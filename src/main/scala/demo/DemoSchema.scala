package demo

import sangria.schema._

object DemoSchema {
  val MyArg = Argument("str", StringType)

  val schema = Schema(ObjectType("Query", fields[Unit, Unit](
    Field(
      name = "echo",
      fieldType = StringType,
      arguments = MyArg :: Nil,
      resolve = c => c.arg(MyArg).toString
    )
  )))
}
