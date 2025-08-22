package com.joaquin.backend.storage;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
@Service
public class S3StorageService implements StorageService{

    private final AmazonS3 s3;
    private final String bucket;

    public S3StorageService(
            @Value("${storage.s3.endpoint}") String endpoint,
            @Value("${storage.s3.region}") String region,
            @Value("${storage.s3.bucket}") String bucket,
            @Value("${storage.s3.access-key}") String accessKey,
            @Value("${storage.s3.secret-key}") String secretKey,
            @Value("${storage.s3.path-style}") boolean pathStyle
    ){
        this.bucket = bucket;
        var creds = new BasicAWSCredentials(accessKey, secretKey);
        var builder = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(creds));

        if (endpoint != null && !endpoint.isEmpty()) {
            builder.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint,region))
                    .withPathStyleAccessEnabled(pathStyle);
        } else {
            builder.withRegion(region);
        }

        builder.withClientConfiguration(new ClientConfiguration()
                .withConnectionTimeout(1000)
                .withSocketTimeout(60000));

        this.s3 = builder.build();
        if (!s3.doesBucketExistV2(bucket)) {
            s3.createBucket(bucket);
        }
    }

    @Override
    public String put(String key, InputStream in, long size, String contentType) {
        ObjectMetadata md = new  ObjectMetadata();
        md.setContentType(contentType);
        md.setContentLength(size);

        var req = new PutObjectRequest(bucket, key, in, md)
                .withCannedAcl(CannedAccessControlList.Private);
        s3.putObject(req);
        return key;
    }

    @Override
    public Resource get(String key) {
        S3Object obj = s3.getObject(new GetObjectRequest(bucket, key));
        return new InputStreamResource(obj.getObjectContent());
    }

    @Override
    public void delete(String key) {
        s3.deleteObject(bucket, key);
    }
}
