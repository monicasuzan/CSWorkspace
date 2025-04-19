package com.example.myadmin;

import java.io.Serializable;

public class Attachment implements Serializable {
    private String fileName;
    private String fileData;

    public Attachment() {
        // Default constructor required for Firebase
    }

    public Attachment(String fileName, String fileData) {
        this.fileName = fileName;
        this.fileData = fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileData() {
        return fileData;
    }
}
