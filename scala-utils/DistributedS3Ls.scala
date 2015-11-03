import com.amazonaws.auth.AWSCredentials
import org.apache.spark.{SparkConf, SparkContext}
import org.rogach.scallop.ScallopConf

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.{ObjectListing, ListObjectsRequest}
import scala.collection.JavaConversions._

import scalaz.Scalaz._

// requires: "com.amazonaws" % "aws-java-sdk" % "1.10.30",
// spark-submit --executor-cores 4 --num-executors 3 --class com.mendeley.altmetrics.DistributedS3Ls altmetrics-assembly-0.1.7.jar
object DistributedS3Ls {
  def main(args: Array[String]): Unit = {
    val conf = new ScallopConf(args) {
      val s3Bucket = opt[String](default = Some("bucket"),
        descr = "Bucket to ls")
      val listResultsPath = opt[String](default = Some("/user/hadoop/s3-ls-results"),
        descr = "Place to put the ls results")
    }

    val awsAccessKey = sys.env("AWS_ACCESS_KEY_ID")
    val awsSecretKey = sys.env("AWS_SECRET_KEY")

    if (awsAccessKey == "" || awsSecretKey == "") {
      println("ERROR: Creds not set fool!")
      System.exit(1)
    }

    implicit val client  = new AmazonS3Client()

    val confMap =
      Map(
        "spark.default.parallelism" -> 100.toString,
        "spark.hadoop.validateOutputSpecs" -> "false",
        "spark.storage.memoryFraction" -> 0.6.toString,
        "spark.shuffle.memoryFraction" -> 0.3.toString,
        "spark.akka.frameSize" -> 500.toString,
        "spark.akka.askTimeout" -> 100.toString,
        "spark.worker.timeout" -> 150.toString,
        "spark.shuffle.consolidateFiles" -> "true",
        "spark.core.connection.ack.wait.timeout" -> "600"
      )

    @transient val sparkConf: SparkConf =
      new SparkConf().setAppName("Test Median")
      .setAll(confMap)

    @transient val sc = new SparkContext(sparkConf)

    val bucket = conf.s3Bucket()

    sc.makeRDD(allHexPairs, 200)
    .flatMap(s3ls(bucket, awsAccessKey, awsSecretKey))
    .saveAsTextFile(conf.listResultsPath())
  }

  def allHexPairs: List[String] =
    ((('a' to 'f') ++ ('0' to '9')).toList |@| (('a' to 'f') ++ ('0' to '9')).toList)(_.toString + _)

  def s3ls(bucket: String, awsAccessKey: String, awsSecretKey: String)(prefix: String): Iterator[String] = {
    val client: AmazonS3Client = new AmazonS3Client(new AWSCredentials() {
      def getAWSAccessKeyId: String = awsAccessKey
      def getAWSSecretKey: String = awsSecretKey
    })

    val request: ListObjectsRequest = new ListObjectsRequest().withBucketName(bucket).withPrefix(prefix)
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
  
  
  // MORE s3 useful stuff
  
  case class SimpleAWSCreds(awsAccessKey: String, awsSecretKey: String) extends AWSCredentials {
  def getAWSAccessKeyId: String = awsAccessKey
  def getAWSSecretKey: String = awsSecretKey
}
def getS3Creds: SimpleAWSCreds = {
    val awsAccessKey = sys.env("AWS_ACCESS_KEY_ID")
    val awsSecretKey = sys.env("AWS_SECRET_KEY")

    if (awsAccessKey == "" || awsSecretKey == "") {
      println("ERROR: Creds not set fool!")
      System.exit(1)
    }

    SimpleAWSCreds(awsAccessKey, awsSecretKey)
  }
  
  def getDocuments(bucket: String, creds: SimpleAWSCreds, enc: String = "UTF-8")
                  (partition: Iterator[String]): Iterator[String] = {
    val client = new AmazonS3Client(creds)
    partition.map { s3Key =>
      val dataStream = scala.io.Source.fromInputStream(
        is = client.getObject(new GetObjectRequest(bucket, s3Key)).getObjectContent,
        enc = enc
      )
      val slurped = dataStream.mkString
      dataStream.close()
      slurped
    }
  }

  def getDocument(bucket: String, creds: SimpleAWSCreds, enc: String = "UTF-8")(s3key: String): String =
    scala.io.Source.fromInputStream(
      is = new AmazonS3Client(creds).getObject(new GetObjectRequest(bucket, s3key)).getObjectContent,
      enc = enc
    )
    .mkString
  
  
  
  
  
}
