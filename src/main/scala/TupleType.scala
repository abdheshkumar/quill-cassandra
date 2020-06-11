import com.datastax.driver.core.{TupleType, TupleValue}
import com.typesafe.config.ConfigFactory
import io.getquill.{
  CassandraAsyncContext,
  CassandraContextConfig,
  CassandraMirrorContext,
  SnakeCase
}
import io.getquill.context.cassandra.Udt

import scala.concurrent.Await
import scala.concurrent.duration._
object TupleType extends App {

  //val ctx = new CassandraMirrorContext(SnakeCase)
  val config = CassandraContextConfig(ConfigFactory.load().getConfig("db"))
  val cluster = config.cluster
  lazy val session = cluster.connect(config.keyspace)
  case class CollectThings(k: Int, v: (Int, String, Float))

  /**
  CREATE TABLE ks.collect_things (
  pk int,
  ck1 text,
  ck2 text,
  v tuple<int, text, float>,
  PRIMARY KEY (pk, ck1, ck2)
);
    */
  import com.datastax.driver.core.BoundStatement
  import com.datastax.driver.core.DataType

  val ps = session.prepare(
    "INSERT INTO collect_things (pk, ck1, ck2, v) VALUES (:pk, :ck1, :ck2, :v)"
  )

  val tupleType: TupleType = cluster.getMetadata.newTupleType(
    DataType.cint,
    DataType.text,
    DataType.cfloat
  )
  /* val tupleValue: TupleValue =
    tupleType.newValue(1: java.lang.Integer, "hello", 2.3f: java.lang.Float)*/
  val tupleValue: TupleValue = tupleType.newValue
    .setInt(0, 2: java.lang.Integer)
    .setString(1, "hello")
    .setFloat(2, 2.3f: java.lang.Float)
  val bs = ps.bind
  bs.setInt("pk", 1: java.lang.Integer)
  bs.setString("ck1", "1")
  bs.setString("ck2", "1")
  bs.setTupleValue("v", tupleValue)
  val result = session.execute(bs)

  println(result)

  val row = session.execute("SELECT v FROM collect_things WHERE pk = 1").one

  val tupleValueRead = row.getTupleValue("v")

  val firstValueInTuple = tupleValueRead.getInt(0)

  val secondValueInTuple = tupleValueRead.getString(1)

  val thirdValueInTuple = tupleValueRead.getFloat(2)
  println(tupleValueRead)
  /*
  //This should be table structure
  final case class Service(id: String = "",
                           name: String = "",
                           serviceAudioFiles: List[ServiceAudioFile])
  /*
CREATE TABLE IF NOT EXISTS service (
                           id text,
                           name text,
                           service_audio_files list<???>,
                           PRIMARY KEY(id)
);
 */
  final case class ServiceAudioFile(title: String,
                                    configAudioFile: ConfigAudioFile)
  sealed trait ConfigAudioFile
  final case class MultiFilesAudioFile(id: Option[String], pattern: String = "")
  final case class SingleFileAudioFile(id: Option[String])
 */

}
