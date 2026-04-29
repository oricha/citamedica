package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class GetClinicRevenueReportUseCase {

    private final PaymentRepository paymentRepository;

    public GetClinicRevenueReportUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal execute(Long clinicId, LocalDateTime from, LocalDateTime to) {
        return paymentRepository.sumCompletedAmountForClinicBetween(clinicId, from, to);
    }
}
