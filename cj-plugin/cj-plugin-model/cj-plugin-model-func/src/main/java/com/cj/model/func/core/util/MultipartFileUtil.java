package com.cj.model.func.core.util;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;

public class MultipartFileUtil {

    public static MultipartFile inputStreamToMultipartFile(InputStream inputStream,String name) throws IOException {
        byte[] bytes = readInputStream(inputStream);
        DefaultMultiPartFile defaultMultiPartFile = new DefaultMultiPartFile(name, bytes);
        return defaultMultiPartFile;
    }

    public static MultipartFile multipartFileByUrl(String url,String name) throws IOException{
        URL downloadUrl = new URL(url);
        URLConnection urlConnection = downloadUrl.openConnection();
        // 超时时间20s
        urlConnection.setConnectTimeout(20*1000);
        urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        InputStream inputStream = urlConnection.getInputStream();
        return inputStreamToMultipartFile(inputStream,name);
    }

    private static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }

    static class DefaultMultiPartFile implements MultipartFile {
        private final String name;
        private String originalFilename;
        @Nullable
        private String contentType;
        private final byte[] content;

        DefaultMultiPartFile(String name, @Nullable byte[] content) {
            this(name, "", (String)null, content);
        }

        DefaultMultiPartFile(String name, String originalFilename, @Nullable String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getOriginalFilename() {
            return this.originalFilename;
        }

        @Override
        public String getContentType() {
            return this.contentType;
        }

        @Override
        public boolean isEmpty() {
            return content.length==0;
        }

        @Override
        public long getSize() {
            return content.length;
        }

        @Override
        public byte[] getBytes() throws IOException {
            return content;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(content);
        }

        @Override
        public void transferTo(File file) throws IOException, IllegalStateException {
            FileCopyUtils.copy(this.content, file);
        }
    }

}
