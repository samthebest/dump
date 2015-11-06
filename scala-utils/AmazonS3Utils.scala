
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{AmazonS3Exception, GetObjectRequest, ObjectListing, ListObjectsRequest}
import scala.collection.JavaConversions._
import scalaz.{-\/, \/-, \/}

sealed trait S3Fail
case object AmazonS3Outage extends S3Fail
case class KeyNotFound(key: String) extends S3Fail

object AmazonS3Utils {
  def createClient(creds: SimpleAWSCreds): \/[S3Fail, AmazonS3Client] =
    try \/-(new AmazonS3Client(creds))
    catch {
      case ex: AmazonS3Exception if ex.getStatusCode == 500 => -\/(AmazonS3Outage)
    }

  def getDocument(bucket: String, client: AmazonS3Client, enc: String)(s3Key: String): \/[S3Fail, String] =
    try {
      val dataStream = scala.io.Source.fromInputStream(
        is = client.getObject(new GetObjectRequest(bucket, s3Key)).getObjectContent,
        enc = enc
      )
      val slurped = dataStream.mkString
      dataStream.close()
      \/-(slurped)
    } catch {
      case ex: AmazonS3Exception if ex.getErrorCode == "NoSuchKey" => -\/(KeyNotFound(s3Key))
      case ex: AmazonS3Exception if ex.getStatusCode == 500 => -\/(AmazonS3Outage)
    }

  def s3ls(bucket: String, creds: SimpleAWSCreds)(prefix: String): Iterator[String] = {
    // Chosen to minimize the number of calls to s3. It's in this scope to avoid closure crap.
    val maxKeys = 65536

    val client: AmazonS3Client = new AmazonS3Client(creds)

    val request: ListObjectsRequest =
      new ListObjectsRequest().withMaxKeys(maxKeys).withBucketName(bucket).withPrefix(prefix)
    // Have to use mutation unfortunately because AWS API sucks
    var objects: ObjectListing = null

    Iterator.continually({
      objects = client.listObjects(request)
      val l = objects.getObjectSummaries.map(_.getKey)
      request.setMarker(objects.getNextMarker)
      l
    })
    .takeWhile(_ => objects.isTruncated)
    .flatten
  }

  // TODO parameterize
  val backoff = 300
  val backoffs = 3

  def handleS3Outages[T](f: () => \/[S3Fail, T], numTries: Int = 0): T =
    (1 to backoffs).toIterator.map(_ => f())
    .find(_.isRight) match {
      case Some(\/-(t)) => t
      case None => f() match {
        case \/-(t) => t
        case -\/(fail) => throw new RuntimeException("Aborting s3 command, s3 sucks, fail: " + fail)
      }
    }
}
