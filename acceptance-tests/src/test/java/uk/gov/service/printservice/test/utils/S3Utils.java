package uk.gov.service.printservice.test.utils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;

public class S3Utils {
  private final AmazonS3 s3;

  public S3Utils() {
    this.s3 = AmazonS3ClientBuilder.defaultClient();
  }

  public int getNumberOfFilesInABucket(String bucketName) throws Exception {
    if (s3.doesBucketExistV2(bucketName)) {
      ObjectListing result = s3.listObjects(bucketName);
      while (result.isTruncated()) {
        result = s3.listNextBatchOfObjects(result);
      }
      System.out.println("files = " + result.getObjectSummaries().size());
      return result.getObjectSummaries().size();
    } else {
      throw new Exception("Bucket `" + bucketName + "` doesn't exist");
    }
  }
}
