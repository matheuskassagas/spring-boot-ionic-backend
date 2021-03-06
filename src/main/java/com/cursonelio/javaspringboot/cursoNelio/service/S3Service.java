package com.cursonelio.javaspringboot.cursoNelio.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.cursonelio.javaspringboot.cursoNelio.service.exception.FileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class S3Service {

    private Logger LOG = LoggerFactory.getLogger(S3Service.class);

    @Autowired
    private AmazonS3 s3Client;

    @Value("${s3.bucket}")
    private String bucketName;


    public URI uploadFile (MultipartFile multipartFile){
        try{
            // informações basicas do upload que chega
            String fileName = multipartFile.getOriginalFilename(); // pega o nome do arquivo que foi enviado
            InputStream is = multipartFile.getInputStream(); // leitura, encapsula o arquivo
            String contentType = multipartFile.getContentType(); // contem a info do tipo do arquivo
            return uploadFile(is, fileName, contentType);
        }
        catch (IOException e){
            throw new FileException("Erro de IO: " + e.getMessage());
        }
    }

    public URI uploadFile (InputStream is, String fileName, String contentType)  {
        try {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentType(contentType);
            LOG.info("Iniciando Upload");
            s3Client.putObject(bucketName, fileName, is, meta);
            LOG.info("Finalizando Upload");
            return s3Client.getUrl(bucketName, fileName).toURI();
        }
        catch (URISyntaxException e){
            throw new FileException("Erro ao converter URL para URI");
        }



//    public void uploadFile (String localFilePath){
//        try{
//            File file = new File(localFilePath);
//            LOG.info("Iniciando Upload");
//            s3Client.putObject(new PutObjectRequest(bucketName, "teste.jpg", file));
//            LOG.info("Finalizando Upload");
//        } catch (AmazonServiceException e){
//            LOG.info("AmazonServiceException: " + e.getErrorMessage());
//            LOG.info("Status code: " + e.getErrorCode());
//        } catch (AmazonClientException e){
//            LOG.info("AmazonClientException: " + e.getMessage());
//        }
    }
}
