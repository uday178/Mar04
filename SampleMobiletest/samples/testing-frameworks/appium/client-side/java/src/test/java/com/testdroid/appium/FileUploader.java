package com.testdroid.appium;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.testdroid.api.http.MultipartFormDataContent;

public class FileUploader { 
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();
    protected static Logger logger = LoggerFactory.getLogger(FileUploader.class);
    
    protected static String uploadFile(String targetAppPath, String serverURL, String testdroid_apikey)
            throws IOException {
        final HttpHeaders headers = new HttpHeaders().setBasicAuthentication(testdroid_apikey, "");

        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                request.setParser(new JsonObjectParser(JSON_FACTORY));
                request.setHeaders(headers);
            }

        });
        MultipartFormDataContent multipartContent = new MultipartFormDataContent();
        FileContent fileContent = new FileContent("application/octet-stream", new File(targetAppPath));

        MultipartFormDataContent.Part filePart = new MultipartFormDataContent.Part("file", fileContent);
        multipartContent.addPart(filePart);

        HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(serverURL + "/api/v2/me/files"), multipartContent);

        UploadResponse uploadResponse = request.execute().parseAs(UploadResponse.class);

        logger.debug("File id: " + uploadResponse.id);

        return uploadResponse.id.toString();
    }

    public static class UploadResponse {
        @Key("id")
        Long id;
    }
}
