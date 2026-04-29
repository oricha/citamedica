package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.adapter.in.dto.medical.DocumentDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicalHistoryApiDtos;
import com.citamedica.backend.application.medical.PatientMedicalDocumentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@PreAuthorize("hasAnyRole('STAFF','DOCTOR','ADMIN')")
public class MedicalDocumentController {

    private final PatientMedicalDocumentService documentService;

    public MedicalDocumentController(PatientMedicalDocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/{documentId}")
    public MedicalHistoryApiDtos.DocumentResponse metadata(@PathVariable Long documentId) {
        return documentService.getMetadata(documentId);
    }

    @GetMapping("/{documentId}/file")
    public ResponseEntity<byte[]> download(@PathVariable Long documentId, HttpServletRequest request) {
        PatientMedicalDocumentService.DocumentDownload dl =
                documentService.readForDownload(documentId, request.getRemoteAddr());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"medical-document-" + documentId + "\"")
                .contentType(MediaType.parseMediaType(dl.mimeType()))
                .body(dl.bytes());
    }

    @PatchMapping("/{documentId}")
    public MedicalHistoryApiDtos.DocumentResponse patchMetadata(
            @PathVariable Long documentId,
            @Valid @RequestBody DocumentDtos.UpdateMetadataRequest body) {
        return documentService.updateMetadata(documentId, body);
    }

    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long documentId) {
        documentService.softDelete(documentId);
    }

    @GetMapping("/{documentId}/versions")
    public List<MedicalHistoryApiDtos.DocumentVersionResponse> versions(@PathVariable Long documentId) {
        return documentService.listVersions(documentId);
    }

    @PostMapping("/{documentId}/versions/{versionId}/restore")
    public ResponseEntity<Void> restore(
            @PathVariable Long documentId,
            @PathVariable int versionId) {
        throw new ResponseStatusException(
                HttpStatus.NOT_IMPLEMENTED,
                "Version restore is not available in local disk storage mode");
    }
}
