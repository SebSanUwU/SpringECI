package edu.escuelaing.arem.ASE.app.tools;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;

public class Response<T> {
    private String contentType;
    private String codeResponse;
    private byte[] fileData;
    private String statusText;
    private T data;

    public Response(String contentType, String codeResponse, String statusText, byte[] fileData, T data) {
        this.contentType = contentType;
        this.codeResponse = codeResponse;
        this.fileData = fileData;
        this.statusText = statusText;
        this.data = data;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getContentType() {
        return contentType;
    }

    public void setCodeResponse(String codeResponse) {
        this.codeResponse = codeResponse;
    }

    public String getCodeResponse() {
        return codeResponse;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "contentType='" + contentType + '\'' +
                ", codeResponse='" + codeResponse + '\'' +
                ", fileData=" + Arrays.toString(fileData) +
                ", statusText='" + statusText + '\'' +
                ", data=" + data +
                '}';
    }
}
