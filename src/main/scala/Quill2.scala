import io.getquill._

import scala.concurrent.ExecutionContext.Implicits.global

object Quill2 extends App {

  val db = new CassandraMirrorContext(SnakeCase)

  import db._

  case class Country(code: String) extends AnyVal

  object Country {

    implicit val decode: MappedEncoding[String, Country] =
      MappedEncoding[String, Country](Country(_))

    implicit val encode: MappedEncoding[Country, String] =
      MappedEncoding[Country, String](_.code)
  }

  case class WeatherStation(country: Country,
                            city: String,
                            stationId: String,
                            entry: Int,
                            value: Int)

  object WeatherStation {

    val getAllByCountry = quote { (country: Country) =>
      query[WeatherStation].filter(_.country == country)
    }
  }

  val result = db.run(WeatherStation.getAllByCountry(lift(Country("UK"))))

  println(result.string)
}
