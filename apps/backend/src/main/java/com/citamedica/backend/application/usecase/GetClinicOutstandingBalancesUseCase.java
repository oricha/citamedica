package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.repository.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetClinicOutstandingBalancesUseCase {

    private final InvoiceRepository invoiceRepository;

    public GetClinicOutstandingBalancesUseCase(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional(readOnly = true)
    public List<InvoiceRepository.OutstandingBalanceRow> execute(Long clinicId) {
        return invoiceRepository.sumOutstandingByClinicGroupedByPatient(clinicId);
    }
}
