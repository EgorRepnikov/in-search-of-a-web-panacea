package com.erepnikov.cask

import cask._
import com.github.plokhotnyuk.jsoniter_scala.core.{JsonValueCodec, writeToArray}
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import io.undertow.Undertow

case class TinyEntity(message: String)

case class LargeEntity(id: Int, message: String, entity: TinyEntity, extra: Array[String])

case class TinyEntityWithId(id: Int, message: String)

object Controller extends Routes {

  implicit val tinyEntityCodec: JsonValueCodec[TinyEntity] = JsonCodecMaker.make[TinyEntity](CodecMakerConfig())
  implicit val largeEntityCodec: JsonValueCodec[LargeEntity] = JsonCodecMaker.make[LargeEntity](CodecMakerConfig())
  implicit val tinyEntityWithIdCodec: JsonValueCodec[TinyEntityWithId] = JsonCodecMaker.make[TinyEntityWithId](CodecMakerConfig())

  @get("/api/get-tiny-json-entity")
  def getTinyJsonEntity() = Response(
    data = writeToArray(TinyEntity("Hello There")),
    headers = Seq("Content-Type" -> "application/json")
  )

  @get("/api/get-large-json-entity")
  def getLargeJsonEntity() = Response(
    data = writeToArray(
      LargeEntity(123, "Hello There", TinyEntity("Hello There Again"), Array("And", "Again"))),
    headers = Seq("Content-Type" -> "application/json")
  )

  @get("/api/get-plain-text")
  def getPlainText() = Response(
    data = "Hello There",
    headers = Seq("Content-Type" -> "text/plain")
  )

  @get("/api/get-tiny-json-entity-by-id/:id")
  def getTinyJsonEntityWithId(id: Int) = Response(
    data = writeToArray(TinyEntityWithId(id, "Hello There")),
    headers = Seq("Content-Type" -> "application/json")
  )

  initialize()
}

object Server extends Main(Controller) {

  override def main(args: Array[String]): Unit = {
    val server = Undertow.builder
      .addHttpListener(8080, host)
      .setHandler(defaultHandler)
      .build
    server.start()
    println("START")
  }
}
