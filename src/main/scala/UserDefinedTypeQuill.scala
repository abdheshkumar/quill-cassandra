import io.getquill.{CassandraAsyncContext, CassandraMirrorContext, SnakeCase}
import io.getquill.context.cassandra.Udt

import scala.concurrent.Await
import scala.concurrent.duration._
object UserDefinedTypeQuill extends App {

  //val ctx = new CassandraMirrorContext(SnakeCase)
  val ctx = new CassandraAsyncContext(SnakeCase, "db")
  import ctx._
  /*
CREATE KEYSPACE callhandling
  WITH REPLICATION = {
   'class' : 'SimpleStrategy',
   'replication_factor' : 1
  };

  CREATE TYPE callhandling.d (
  s text,
  s1 text,
  i int
);
CREATE TABLE callhandling.hello ( id int PRIMARY KEY, s list<FROZEN<d>>);
   */
  sealed trait A extends Udt
  case class B(s: String) extends A
  case class C(s1: String, i: Int) extends A
  case class Hello(a: Int, s: List[A])

  val l: List[A] = List(B("s"), C("s1", 12))
  val h = Hello(1, l)
  def insert[T <: Hello: InsertMeta](v: Hello) = {
    /*val r = quote {
      query[T].insert(lift(v))
    }*/
    run(query[T].insert(lift(v)))
  }
  val rr = run(insert(h))
  val a = Await.result(rr, 2.seconds)
  println(a)
}
