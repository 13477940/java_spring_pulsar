package framework.controller;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import framework.random.RandomServiceStatic;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 適用於增強 Spring 的 RestController 功能
 */
public abstract class BaseRestController {

    /**
     * 將 UTF-8 字串轉換為瀏覽器可用的 url-encoding 檔案名稱
     */
    protected String url_encode_str(String fileName) {
        String encodeFileName = null;
        try {
            encodeFileName = java.net.URLEncoder
                    .encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodeFileName;
    }

    /**
     * 如果是檔案路徑，取得該路徑的檔案副檔名
     */
    protected String get_file_extension(String path) {
        Path filePath = Paths.get(path);
        if(filePath.toFile().isDirectory()) return null; // 如果是資料夾
        int begin_index = filePath.getFileName().toString().lastIndexOf(".") + 1;
        return filePath.getFileName().toString().substring(begin_index);
    }

    protected void output_file_to_response(File file, HttpServletResponse response) {
        output_file_to_response(file, false, response);
    }

    protected void output_file_to_response(File file, boolean isAttachment, HttpServletResponse response) {
        output_file_to_response(file, null, isAttachment, response);
    }

    protected void output_file_to_response(File file, MediaType mediaType, boolean isAttachment, HttpServletResponse response) {
        String file_mime = "application/octet-stream"; // Any kind of binary data
        // use tika
        try {
            file_mime = new Tika().detect(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // response header
        {
            // ContentType
            // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types#textjavascript
            if("js".equalsIgnoreCase(get_file_extension(file.getName()))) file_mime = "text/javascript";
            // https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types#textcss
            if("css".equalsIgnoreCase(get_file_extension(file.getName()))) file_mime = "text/css";
            String str_charset = String.valueOf(StandardCharsets.UTF_8);
            if(null != mediaType) file_mime = mediaType.toString();
            response.addHeader("Content-Type", file_mime+"; charset="+str_charset);
            // Content-Length
            response.setHeader("Content-Length", String.valueOf(file.length()));
            // Content-Disposition
            StringBuilder sbd = new StringBuilder();
            {
                String encode_file_name = url_encode_str(file.getName());
                if (isAttachment) {
                    sbd.append("attachment;filename=\"");
                } else {
                    sbd.append("inline;filename=\"");
                }
                sbd.append(encode_file_name);
                sbd.append("\";");
                sbd.append("filename*=utf-8''"); // use for modern browser
                sbd.append(encode_file_name);
            }
            response.setHeader("Content-Disposition", sbd.toString());
        }
        try ( InputStream inputStream = new WeakReference<>( new FileInputStream(file) ).get() ) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            ServletOutputStream outputStream = new WeakReference<>( response.getOutputStream() ).get();
            while ( (bytesRead = inputStream.read(buffer)) != -1 ) {
                // 在 response 對象可寫入數據時，持續向輸出流寫入數據
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void print_to_response(JsonObject obj, HttpServletResponse response) {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .disableHtmlEscaping()
                .serializeNulls();
        String tmp = gsonBuilder.create().toJson(obj);
        print_to_response(tmp, response);
    }

    protected void print_to_response(CharSequence charSequence, HttpServletResponse response) {
        print_to_response(charSequence, null, false, response);
    }

    protected void print_to_response(CharSequence charSequence, MediaType mediaType, boolean isAttachment, HttpServletResponse response) {
        // response header
        {
            // Content-Type
            String str_charset = String.valueOf(StandardCharsets.UTF_8);
            String str_mime = "text/plain";
            if(null != mediaType) str_mime = mediaType.toString();
            response.addHeader("Content-Type", str_mime+"; charset="+str_charset);
            // Content-Length
            response.addHeader("Content-Length", String.valueOf(charSequence.toString().getBytes(StandardCharsets.UTF_8).length));
            // Content-Disposition
            StringBuilder sbd = new StringBuilder();
            {
                String encode_file_name = url_encode_str("text_"+RandomServiceStatic.getInstance().getLowerCaseRandomString(6));
                if (isAttachment) {
                    sbd.append("attachment;filename=\"");
                } else {
                    sbd.append("inline;filename=\"");
                }
                sbd.append(encode_file_name);
                sbd.append("\";");
                sbd.append("filename*=utf-8''"); // use for modern browser
                sbd.append(encode_file_name);
            }
            response.setHeader("Content-Disposition", sbd.toString());
        }
        byte[] bytes = charSequence.toString().getBytes(StandardCharsets.UTF_8);
        try ( InputStream inputStream = new WeakReference<>( new ByteArrayInputStream(bytes) ).get() ) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            ServletOutputStream outputStream = new WeakReference<>( response.getOutputStream() ).get();
            while ( (bytesRead = inputStream.read(buffer)) != -1 ) {
                // 在 response 對象可寫入數據時，持續向輸出流寫入數據
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void output_error_404(HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
