package com.citamedica.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.medical-history.documents")
public class MedicalDocumentStorageProperties {

    /**
     * Directory root for stored medical document bytes (Phase 1 local disk; S3 later).
     */
    private String storageDir = "./data/medical-documents";

    /**
     * Maximum upload size in bytes (default 50 MiB).
     */
    private long maxBytes = 52_428_800L;

    public String getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(String storageDir) {
        this.storageDir = storageDir;
    }

    public long getMaxBytes() {
        return maxBytes;
    }

    public void setMaxBytes(long maxBytes) {
        this.maxBytes = maxBytes;
    }
}
