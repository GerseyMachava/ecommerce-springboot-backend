package com.ecommerce.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ecommerce.backend.dto.ResponseDto.PaymentResponseDto;
import com.ecommerce.backend.dto.requestDto.PaymentRequestDto;
import com.ecommerce.backend.dto.requestDto.PaymentStatusUpdateRequest;
import com.ecommerce.backend.mapper.PaymentMapper;
import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.Payment;
import com.ecommerce.backend.repository.PaymentRepository;
import com.ecommerce.backend.service.events.PaymentCompletedEvent;
import com.ecommerce.backend.shared.exception.BusinessException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PaymentService {

    private PaymentMapper mapper;
    private PaymentRepository repository;
    private OrderService orderService;
    private ApplicationEventPublisher eventPublisher;

    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) {
        Order order = orderService.findOrderByid(requestDto.orderId());
        if (repository.existsByOrderId(order.getId())) {
            throw new BusinessException("Payment already done for this order", HttpStatus.CONFLICT);
        }
        CheckAmount(order, requestDto);
        Payment newPayment = mapper.toEntity(requestDto, order);
        repository.save(newPayment);
        eventPublisher.publishEvent(new PaymentCompletedEvent(order.getId(), newPayment.getId()));
        return mapper.toResponseDto(repository.save(newPayment));
    }

    public List<PaymentResponseDto> findAllPayments() {
        List<Payment> paymentList = repository.findAll();
        return mapper.toResponseList(paymentList);
    }

    public PaymentResponseDto tooglePaymentStatus(PaymentStatusUpdateRequest requestDto) {
        Payment payment = repository.findById(requestDto.paymentId())
                .orElseThrow(() -> new BusinessException("No payment found with the id: " + requestDto.paymentId(),
                        HttpStatus.NOT_FOUND));
        payment.setStatus(requestDto.status());
        Payment updatedPayment = repository.save(payment);
        return mapper.toResponseDto(updatedPayment);
    }

    public PaymentResponseDto findById(Long id) {
        Payment payment = repository.findById(id)
                .orElseThrow(() -> new BusinessException("No payment found with the id: " + id, HttpStatus.NOT_FOUND));
        return mapper.toResponseDto(payment);
    }

    public void deletePayment(Long id) {
        repository.findById(id).ifPresentOrElse(repository::delete,
                () -> {
                    throw new BusinessException("No payment found with the id " + id, HttpStatus.NOT_FOUND);

                });
    }

    private void CheckAmount(Order order, PaymentRequestDto requestDto) {
        BigDecimal orderAmount = order.getTotalAmount();
        BigDecimal paymentAmount = requestDto.amount();

        if (orderAmount.compareTo(paymentAmount) == 1) {
            throw new BusinessException("The order amount is greater than the payment amount", HttpStatus.CONFLICT);

        }
    }

}
