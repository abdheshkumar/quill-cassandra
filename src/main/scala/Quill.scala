import io.getquill._

import scala.concurrent.ExecutionContext.Implicits.global

object Quill extends App {

  val db = new CassandraAsyncContext[SnakeCase]("db")

  import db._

  case class WeatherStation(country: String, city: String, stationId: String, entry: Int, value: Int)

  object WeatherStation {

    val getAllByCountry = quote {
      (country: String) =>
        query[WeatherStation].filter(_.country == country)
    }

    val getAllByCountryAndCity = quote {
      (country: String, city: String) =>
        getAllByCountry(country).filter(_.city == city)
    }

    val getAllByCountryCityAndId = quote {
      (country: String, city: String, stationId: String) =>
        getAllByCountryAndCity(country, city).filter(_.stationId == stationId).allowFiltering
    }
  }

  val result = db.run(WeatherStation.getAllByCountryCityAndId("UK", "London", "Bakeerloo"))

  result.onComplete { f => println(f); db.close(); }
}