import io.getquill.{CassandraAsyncContext, SnakeCase}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object QuillApp extends App {

  sealed trait TokenStatus {
    def value: Int

    def index: Int

    def name: String

    def isIssued: _root_.scala.Boolean = false

    def isVerified: _root_.scala.Boolean = false

    def isUsed: _root_.scala.Boolean = false
  }

  object TokenStatus {
    def fromValue(value: _root_.scala.Int): TokenStatus = value match {
      case 0 => Issued
      case 1 => Verified
      case 2 => Used
    }
  }

  @SerialVersionUID(0L)
  case object Issued extends TokenStatus {
    val value = 0
    val index = 0
    val name = "Issued"

    override def isIssued: _root_.scala.Boolean = true
  }

  @SerialVersionUID(0L)
  case object Verified extends TokenStatus {
    val value = 1
    val index = 1
    val name = "Verified"

    override def isVerified: _root_.scala.Boolean = true
  }

  @SerialVersionUID(0L)
  case object Used extends TokenStatus {
    val value = 2
    val index = 2
    val name = "Used"

    override def isUsed: _root_.scala.Boolean = true
  }

  sealed trait TokenType {
    def value: Int

    def index: Int

    def name: String

    def isVerifyEmail: _root_.scala.Boolean = false

    def isChangePassword: _root_.scala.Boolean = false
  }

  object TokenType {
    def fromValue(value: _root_.scala.Int): TokenType = value match {
      case 0 => VerifyEmail
      case 1 => ChangePassword
    }
  }

  case object VerifyEmail extends TokenType {
    val value = 0
    val index = 0
    val name = "VerifyEmail"

    override def isVerifyEmail: _root_.scala.Boolean = true
  }

  @SerialVersionUID(0L)
  case object ChangePassword extends TokenType {
    val value = 1
    val index = 1
    val name = "ChangePassword"

    override def isChangePassword: _root_.scala.Boolean = true
  }

  final case class TokenDetails(tokenId: _root_.scala.Predef.String = "",
                                issuer: _root_.scala.Predef.String = "",
                                audience: _root_.scala.Predef.String = "",
                                tokenValue: _root_.scala.Predef.String = "",
                                subject: _root_.scala.Predef.String = "",
                                tokenStatus: TokenStatus = Issued,
                                tokenType: TokenType = VerifyEmail)

  val ctx = new CassandraAsyncContext(SnakeCase, "db")

  import ctx._

  implicit val decodeTokenStatus = MappedEncoding[Int, TokenStatus](
    tokenStatus => TokenStatus.fromValue(tokenStatus)
  )
  implicit val decodeTokenType =
    MappedEncoding[Int, TokenType](tokenType => TokenType.fromValue(tokenType))
  implicit val encodeTokenType = MappedEncoding[TokenType, Int](_.value)

  val tokenType: TokenType = VerifyEmail
  val token = "eyJraWQ"
  val q = quote {
    query[TokenDetails]
      .filter(td => td.tokenValue == lift(token))
      .filter(td => td.tokenType == liftScalar(tokenType))
      .allowFiltering
  }
  val r = run(q)
    .map(_.headOption)

  println(Await.result(r, Duration.Inf))
}
