package com.citamedica.backend.adapter.in.dto.medical;

public final class DocumentDtos {
    private DocumentDtos(){}

    public record UpdateMetadataRequest(String notes) {}
}
