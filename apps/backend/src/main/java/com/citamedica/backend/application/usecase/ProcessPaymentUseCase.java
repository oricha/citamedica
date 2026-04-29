package com.citamedica.backend.application.usecase;

import com.citamedica.backend.domain.model.Invoice;
import com.citamedica.backend.domain.model.InvoiceStatus;
import com.citamedica.backend.domain.model.Payment;
import com.citamedica.backend.domain.model.PaymentProvider;
import com.citamedica.backend.domain.model.PaymentStatus;
import com.citamedica.backend.domain.model.Patient;
import com.citamedica.backend.domain.port.payment.PaymentGatewayChargeRequest;
import com.citamedica.backend.domain.port.payment.PaymentGatewayPort;
import com.citamedica.backend.domain.repository.AppointmentRepository;
import com.citamedica.backend.domain.repository.InvoiceRepository;
import com.citamedica.backend.domain.repository.PatientRepository;
import com.citamedica.backend.domain.repository.PaymentRepository;
import com.citamedica.backend.exception.domain.EntityNotFoundDomainException;
import com.citamedica.backend.exception.domain.PaymentDeclinedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProcessPaymentUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentUseCase.class);

    private final PaymentRepository paymentRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final InvoiceRepository invoiceRepository;
    private final PaymentGatewayPort paymentGateway;

    public ProcessPaymentUseCase(
            PaymentRepository paymentRepository,
            PatientRepository patientRepository,
            AppointmentRepository appointmentRepository,
            InvoiceRepository invoiceRepository,
            PaymentGatewayPort paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.invoiceRepository = invoiceRepository;
        this.paymentGateway = paymentGateway;
    }

    @Transactional
    public Payment execute(
            Long patientId,
            Long appointmentId,
            Long invoiceId,
            BigDecimal amount,
            String currency,
            PaymentProvider provider,
            String providerTokenOrPaymentMethodId) {

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundDomainException("Patient not found: " + patientId));

        Payment payment = new Payment();
        payment.setPatient(patient);
        payment.setAmount(amount);
        payment.setCurrency(currency != null ? currency : "USD");
        payment.setPaymentProvider(provider);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        if (provider == PaymentProvider.STRIPE) {
            payment.setStripeToken(providerTokenOrPaymentMethodId);
        } else {
            payment.setPaypalOrderId(providerTokenOrPaymentMethodId);
        }
        if (appointmentId != null) {
            payment.setAppointment(appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new EntityNotFoundDomainException("Appointment not found: " + appointmentId)));
        }
        if (invoiceId != null) {
            payment.setInvoice(invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new EntityNotFoundDomainException("Invoice not found: " + invoiceId)));
        }
        payment.validateAmount();
        payment = paymentRepository.save(payment);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("payment_id", String.valueOf(payment.getId()));
        metadata.put("patient_id", String.valueOf(patientId));

        try {
            var result = paymentGateway.charge(new PaymentGatewayChargeRequest(
                    provider,
                    providerTokenOrPaymentMethodId,
                    amount,
                    currency != null ? currency : "USD",
                    metadata));

            if (result.success()) {
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setCompletedAt(LocalDateTime.now());
                if (provider == PaymentProvider.STRIPE) {
                    payment.setStripeTransactionId(result.providerTransactionId());
                } else {
                    payment.setPaypalOrderId(result.providerTransactionId());
                }
                if (invoiceId != null) {
                    Invoice inv = invoiceRepository.findById(invoiceId)
                            .orElseThrow(() -> new EntityNotFoundDomainException("Invoice not found: " + invoiceId));
                    inv.setStatus(InvoiceStatus.PAID);
                    inv.setUpdatedAt(LocalDateTime.now());
                    invoiceRepository.save(inv);
                }
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                log.warn("Payment {} declined: {}", payment.getId(), result.declineReason());
            }
        } catch (PaymentDeclinedException ex) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw ex;
        }

        return paymentRepository.save(payment);
    }
}
