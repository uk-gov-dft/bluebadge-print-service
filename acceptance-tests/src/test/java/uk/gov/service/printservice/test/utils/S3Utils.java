package uk.gov.service.printservice.test.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

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
			return result.getObjectSummaries().size();
		} else {
			throw new Exception("Bucket `" + bucketName + "` doesn't exist");
		}
	}

	public boolean createFolder(String bucketName, String dir) throws Exception {
		if (s3.doesBucketExistV2(bucketName)) {
		    // create meta-data for your folder and set content-length to 0
		    ObjectMetadata metadata = new ObjectMetadata();
		    metadata.setContentLength(0);

		    // create empty content
		    InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

		    // create a PutObjectRequest passing the folder name suffixed by /
		    PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, dir + "/", emptyContent, metadata);

		    // send request to S3 to create folder
		    return s3.putObject(putObjectRequest) != null;
		} else {
			throw new Exception("Bucket `" + bucketName + "` doesn't exist");
		}
	}
	
	public boolean uploadFile(String bucketName, String dir, File file) throws Exception {
		if (s3.doesBucketExistV2(bucketName)) {			
		    return s3.putObject(bucketName, dir + "/", file)  != null;
		} else {
			throw new Exception("Bucket `" + bucketName + "` doesn't exist");
		}
		
	}
	
	public void cleanBucket(String bucketName) throws Exception {
		if (s3.doesBucketExistV2(bucketName)) {	
			// delete pictures
	        ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
	                .withBucketName(bucketName)
	                .withPrefix("pictures/");

	        ObjectListing objectListing = s3.listObjects(listObjectsRequest);

	        while (true) {
	            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
	                s3.deleteObject(bucketName, objectSummary.getKey());
	            }
	            if (objectListing.isTruncated()) {
	                objectListing = s3.listNextBatchOfObjects(objectListing);
	            } else {
	                break;
	            }
	        }
			s3.deleteObject(bucketName, "pictures/");
			
			// delete any other file in root folder
			listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName);
			objectListing = s3.listObjects(listObjectsRequest);
	        while (true) {
	            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
	                s3.deleteObject(bucketName, objectSummary.getKey());
	            }
	            if (objectListing.isTruncated()) {
	                objectListing = s3.listNextBatchOfObjects(objectListing);
	            } else {
	                break;
	            }
	        }
			
		} else {
			throw new Exception("Bucket `" + bucketName + "` doesn't exist");
		}		
	}
	
	public void setupBucket(String bucketName) throws Exception {
		if (s3.doesBucketExistV2(bucketName)) {			
			createFolder(bucketName, "pictures");
			
		    Path picPath =
		        FileSystems.getDefault()
		            .getPath("resources", "pictures", "smile.jpg")
		            .normalize()
		            .toAbsolutePath();

			
			File file = Files.createFile(picPath).toFile();
			uploadFile(bucketName, "pictures", file);
		} else {
			throw new Exception("Bucket `" + bucketName + "` doesn't exist");
		}				
	}
}
