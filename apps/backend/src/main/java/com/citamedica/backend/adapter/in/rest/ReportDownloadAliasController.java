package com.citamedica.backend.adapter.in.rest;

import com.citamedica.backend.application.usecase.DownloadReportUseCase;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ReportDownloadAliasController {

    private final DownloadReportUseCase downloadReportUseCase;

    public ReportDownloadAliasController(DownloadReportUseCase downloadReportUseCase) {
        this.downloadReportUseCase = downloadReportUseCase;
    }

    /**
     * Spec alias: GET /api/v1/reports/{id} scoped by clinic for authorization.
     */
    @PreAuthorize("hasAnyRole('CLINIC_MANAGER','ADMIN')")
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<byte[]> downloadAlias(
            @RequestParam("clinicId") Long clinicId,
            @PathVariable("reportId") Long reportId) {
        DownloadReportUseCase.DownloadedReport downloaded = downloadReportUseCase.execute(clinicId, reportId);
        MediaType mediaType = switch (downloaded.format()) {
            case PDF -> MediaType.APPLICATION_PDF;
            case CSV -> new MediaType("text", "csv");
            case XLSX -> MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        };
        String ext = downloaded.format().name().toLowerCase();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report-" + reportId + "." + ext + "\"")
                .contentType(mediaType)
                .body(downloaded.content());
    }
}
