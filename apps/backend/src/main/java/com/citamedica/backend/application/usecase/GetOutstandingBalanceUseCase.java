package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class GetOutstandingBalanceUseCase {

    private final InvoiceRepository invoiceRepository;

    public GetOutstandingBalanceUseCase(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal execute(Long patientId) {
        return invoiceRepository.sumOutstandingBalanceByPatientId(patientId);
    }
}
