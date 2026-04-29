package com.citamedica.backend.application.medical;

import com.citamedica.backend.adapter.in.dto.medical.DocumentDtos;
import com.citamedica.backend.adapter.in.dto.medical.MedicalHistoryApiDtos;
import com.citamedica.backend.adapter.out.persistence.jpa.MedicalDocumentJpaRepository;
import com.citamedica.backend.config.MedicalDocumentStorageProperties;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.model.medical.MedicalDocument;
import com.citamedica.backend.domain.model.medical.MedicalDocumentType;
import com.citamedica.backend.domain.model.medical.MedicalHistoryAuditAction;
import com.citamedica.backend.domain.model.medical.MedicalHistorySourceTypes;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.MedicalDocumentException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class PatientMedicalDocumentService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/jpeg",
            "image/png",
            "application/dicom");

    private final MedicalDocumentJpaRepository documentJpaRepository;
    private final PatientRepository patientRepository;
    private final MedicalDocumentStorageProperties storageProperties;
    private final MedicalHistoryRecorder recorder;
    private final ObjectMapper objectMapper;

    public PatientMedicalDocumentService(
            MedicalDocumentJpaRepository documentJpaRepository,
            PatientRepository patientRepository,
            MedicalDocumentStorageProperties storageProperties,
            MedicalHistoryRecorder recorder,
            ObjectMapper objectMapper) {
        this.documentJpaRepository = documentJpaRepository;
        this.patientRepository = patientRepository;
        this.storageProperties = storageProperties;
        this.recorder = recorder;
        this.objectMapper = objectMapper;
    }

    private Path storageRoot() {
        Path root = Paths.get(storageProperties.getStorageDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new MedicalDocumentException("Cannot create medical document storage directory: " + root);
        }
        return root;
    }

    private String actor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getName() != null ? auth.getName() : "system";
    }

    private String jsonAudit(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private void validateUpload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new MedicalDocumentException("File is required");
        }
        String ct = file.getContentType();
        if (ct == null || !ALLOWED_CONTENT_TYPES.contains(ct)) {
            throw new MedicalDocumentException("Unsupported or missing content type. Allowed: PDF, JPEG, PNG, DICOM.");
        }
    }

    private static String extension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        if (ext.length() > 16) {
            return "";
        }
        return ext.replaceAll("[^a-zA-Z0-9._-]", "");
    }

    private static String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new MedicalDocumentException("Hash algorithm not available");
        }
    }

    private Patient loadPatient(Long patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));
    }

    @Transactional
    public MedicalHistoryApiDtos.DocumentResponse upload(
            Long patientId,
            MultipartFile file,
            MedicalDocumentType documentType,
            String notes) {
        Patient patient = loadPatient(patientId);
        validateUpload(file);
        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new MedicalDocumentException("Failed to read uploaded file");
        }
        if (bytes.length > storageProperties.getMaxBytes()) {
            throw new MedicalDocumentException("File exceeds maximum size of " + storageProperties.getMaxBytes() + " bytes");
        }
        String hash = sha256Hex(bytes);
        String ext = extension(file.getOriginalFilename());
        String rel = patientId + "/" + UUID.randomUUID() + ext;
        Path root = storageRoot();
        Path absolute = root.resolve(rel).normalize();
        if (!absolute.startsWith(root)) {
            throw new MedicalDocumentException("Invalid storage path");
        }
        try {
            Files.createDirectories(absolute.getParent());
            Files.write(absolute, bytes);
        } catch (IOException e) {
            throw new MedicalDocumentException("Failed to write medical document: " + e.getMessage());
        }

        MedicalDocument doc = new MedicalDocument();
        doc.setPatient(patient);
        doc.setDocumentType(documentType);
        doc.setFilePath(rel.replace('\\', '/'));
        doc.setFileHash(hash);
        doc.setFileSize(bytes.length);
        doc.setMimeType(file.getContentType());
        doc.setUploadedBy(actor());
        doc.setNotes(notes);
        MedicalDocument saved = documentJpaRepository.save(doc);

        recorder.appendTimelineEvent(
                patient,
                "DOCUMENT",
                LocalDateTime.now(),
                "Medical document uploaded (" + documentType + ")",
                notes,
                saved.getId(),
                MedicalHistorySourceTypes.DOCUMENT);
        recorder.audit(
                patient,
                MedicalHistorySourceTypes.DOCUMENT,
                saved.getId(),
                MedicalHistoryAuditAction.CREATE,
                jsonAudit(Map.of("documentType", documentType.name(), "fileHash", hash)),
                actor(),
                null);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<MedicalHistoryApiDtos.DocumentResponse> list(
            Long patientId,
            MedicalDocumentType type,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            String search,
            Pageable pageable) {
        loadPatient(patientId);
        Specification<MedicalDocument> spec = documentSpec(patientId, type, dateFrom, dateTo, search);
        return documentJpaRepository.findAll(spec, pageable).map(this::toDto);
    }

    private static Specification<MedicalDocument> documentSpec(
            Long patientId,
            MedicalDocumentType type,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            String search) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            p.add(cb.equal(root.get("patient").get("id"), patientId));
            p.add(cb.isNull(root.get("deletedAt")));
            if (type != null) {
                p.add(cb.equal(root.get("documentType"), type));
            }
            if (dateFrom != null) {
                p.add(cb.greaterThanOrEqualTo(root.get("uploadedAt"), dateFrom));
            }
            if (dateTo != null) {
                p.add(cb.lessThanOrEqualTo(root.get("uploadedAt"), dateTo));
            }
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                p.add(cb.like(cb.lower(cb.coalesce(root.get("notes"), "")), pattern));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
    }

    @Transactional(readOnly = true)
    public MedicalHistoryApiDtos.DocumentResponse getMetadata(Long documentId) {
        MedicalDocument doc = documentJpaRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Document not found: " + documentId));
        return toDto(doc);
    }

    @Transactional
    public MedicalHistoryApiDtos.DocumentResponse updateMetadata(Long documentId, DocumentDtos.UpdateMetadataRequest req) {
        MedicalDocument doc = documentJpaRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Document not found: " + documentId));
        if (req.notes() != null) {
            doc.setNotes(req.notes());
        }
        MedicalDocument saved = documentJpaRepository.save(doc);
        recorder.audit(
                saved.getPatient(),
                MedicalHistorySourceTypes.DOCUMENT,
                saved.getId(),
                MedicalHistoryAuditAction.UPDATE,
                jsonAudit(Map.of("documentId", saved.getId())),
                actor(),
                null);
        return toDto(saved);
    }

    @Transactional
    public void softDelete(Long documentId) {
        MedicalDocument doc = documentJpaRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Document not found: " + documentId));
        doc.setDeletedAt(LocalDateTime.now());
        documentJpaRepository.save(doc);
        recorder.audit(
                doc.getPatient(),
                MedicalHistorySourceTypes.DOCUMENT,
                doc.getId(),
                MedicalHistoryAuditAction.SOFT_DELETE,
                jsonAudit(Map.of("documentId", documentId)),
                actor(),
                null);
    }

    public record DocumentDownload(String mimeType, byte[] bytes) {}

    @Transactional
    public DocumentDownload readForDownload(Long documentId, String clientIp) {
        MedicalDocument doc = documentJpaRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Document not found: " + documentId));
        Path root = storageRoot();
        Path absolute = root.resolve(doc.getFilePath()).normalize();
        if (!absolute.startsWith(root)) {
            throw new MedicalDocumentException("Invalid document path");
        }
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(absolute);
        } catch (IOException e) {
            throw new MedicalDocumentException("Failed to read medical document from storage");
        }
        recorder.audit(
                doc.getPatient(),
                MedicalHistorySourceTypes.DOCUMENT,
                doc.getId(),
                MedicalHistoryAuditAction.ACCESS,
                jsonAudit(Map.of("documentId", documentId, "action", "DOWNLOAD")),
                actor(),
                clientIp);
        String mime = doc.getMimeType() != null ? doc.getMimeType() : "application/octet-stream";
        return new DocumentDownload(mime, bytes);
    }

    @Transactional
    public DocumentDownload readForDownloadForPatient(Long documentId, Long patientId, String clientIp) {
        MedicalDocument doc = documentJpaRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Document not found: " + documentId));
        if (!doc.getPatient().getId().equals(patientId)) {
            throw new EntityNotFoundDomainException("Document not found: " + documentId);
        }
        Path root = storageRoot();
        Path absolute = root.resolve(doc.getFilePath()).normalize();
        if (!absolute.startsWith(root)) {
            throw new MedicalDocumentException("Invalid document path");
        }
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(absolute);
        } catch (IOException e) {
            throw new MedicalDocumentException("Failed to read medical document from storage");
        }
        recorder.audit(
                doc.getPatient(),
                MedicalHistorySourceTypes.DOCUMENT,
                doc.getId(),
                MedicalHistoryAuditAction.ACCESS,
                jsonAudit(Map.of("documentId", documentId, "action", "DOWNLOAD", "channel", "PORTAL")),
                actor(),
                clientIp);
        String mime = doc.getMimeType() != null ? doc.getMimeType() : "application/octet-stream";
        return new DocumentDownload(mime, bytes);
    }

    @Transactional(readOnly = true)
    public MedicalHistoryApiDtos.DocumentResponse getMetadataForPatient(Long documentId, Long patientId) {
        return documentJpaRepository.findByIdAndPatient_Id(documentId, patientId)
                .filter(d -> d.getDeletedAt() == null)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundDomainException("Document not found: " + documentId));
    }

    @Transactional(readOnly = true)
    public List<MedicalHistoryApiDtos.DocumentVersionResponse> listVersions(Long documentId) {
        MedicalDocument doc = documentJpaRepository.findByIdAndDeletedAtIsNull(documentId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Document not found: " + documentId));
        return List.of(new MedicalHistoryApiDtos.DocumentVersionResponse(
                Long.valueOf(doc.getVersionId()),
                doc.getUploadedAt(),
                doc.getFileHash()));
    }

    private MedicalHistoryApiDtos.DocumentResponse toDto(MedicalDocument d) {
        return new MedicalHistoryApiDtos.DocumentResponse(
                d.getId(),
                d.getDocumentType().name(),
                d.getMimeType(),
                d.getFileSize(),
                d.getFileHash(),
                d.getUploadedBy(),
                d.getUploadedAt(),
                d.getVersionId(),
                d.getNotes());
    }
}
