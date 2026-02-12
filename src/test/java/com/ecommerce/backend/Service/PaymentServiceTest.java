package com.ecommerce.backend.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;

import com.ecommerce.backend.dto.ResponseDto.PaymentResponseDto;
import com.ecommerce.backend.dto.requestDto.PaymentRequestDto;
import com.ecommerce.backend.dto.requestDto.PaymentStatusUpdateRequest;
import com.ecommerce.backend.mapper.PaymentMapper;
import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.Payment;
import com.ecommerce.backend.model.enums.PaymentMethod;
import com.ecommerce.backend.model.enums.PaymentStatus;
import com.ecommerce.backend.repository.PaymentRepository;
import com.ecommerce.backend.service.OrderService;
import com.ecommerce.backend.service.PaymentService;
import com.ecommerce.backend.service.events.PaymentCompletedEvent;
import com.ecommerce.backend.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    private Order order;
    private Payment payment;
    private PaymentRequestDto paymentRequestDto;
    private PaymentResponseDto paymentResponseDto;

    @BeforeEach
    void setUp() {

        order = Order.builder()
                .totalAmount(new BigDecimal("1000.00"))
                .build();
        order.setId(1L);
        payment = Payment.builder()
                .order(order)
                .amount(new BigDecimal("1000.00"))
                .method(PaymentMethod.BCI)
                .status(PaymentStatus.PENDING)
                .transactionReference("TXN-123")
                .paidAt(LocalDate.now())
                .build();
        payment.setId(1L);

        paymentRequestDto = new PaymentRequestDto(
                1L,
                PaymentMethod.BCI,
                new BigDecimal("1000.00")

        );
        paymentResponseDto = new PaymentResponseDto(
                1L,
                "TXN-123",
                PaymentMethod.BCI,
                LocalDate.now(),
                new BigDecimal("1000.00"),
                PaymentStatus.PENDING,
                1L,
                "cliente@email.com");
    }

    @Nested
    @DisplayName("Criação de Pagamento - Cenários Críticos")
    class CreatePaymentTests {

        @Test
        @DisplayName("✅ Happy path: deve criar pagamento com sucesso")
        void createPayment_WithValidData_ShouldCreatePayment() {
            // Arrange
            when(orderService.findOrderByid(1L)).thenReturn(order);
            when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
            when(paymentMapper.toEntity(paymentRequestDto, order)).thenReturn(payment);
            when(paymentRepository.save(payment)).thenReturn(payment);
            when(paymentMapper.toResponseDto(payment)).thenReturn(paymentResponseDto);

            // Act
            PaymentResponseDto result = paymentService.createPayment(paymentRequestDto);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals(1L, result.orderid());
            assertEquals(new BigDecimal("1000.00"), result.amount());
            assertEquals(PaymentStatus.PENDING, result.status());

            verify(eventPublisher).publishEvent(any(PaymentCompletedEvent.class));
            verify(paymentRepository, times(1)).save(payment); // 1 vez apenas!
        }

        @Test
        @DisplayName("💰 Regra financeira: deve rejeitar pagamento com valor MENOR que o pedido")
        void createPayment_WithAmountLessThanOrder_ShouldThrowException() {
            // Arrange
            PaymentRequestDto menorValor = new PaymentRequestDto(
                    1L,
                    PaymentMethod.BCI,
                    new BigDecimal("500.00"));

            when(orderService.findOrderByid(1L)).thenReturn(order);
            when(paymentRepository.existsByOrderId(1L)).thenReturn(false);

            // Act & Assert
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.createPayment(menorValor));

            assertEquals("The order amount is greater than the payment amount", ex.getMessage());
            assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("🔒 Integridade: deve rejeitar pagamento DUPLICADO para o mesmo pedido")
        void createPayment_WithExistingPayment_ShouldThrowException() {
            // Arrange
            when(orderService.findOrderByid(1L)).thenReturn(order);
            when(paymentRepository.existsByOrderId(1L)).thenReturn(true);

            // Act & Assert
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.createPayment(paymentRequestDto));

            assertEquals("Payment already done for this order", ex.getMessage());
            assertEquals(HttpStatus.CONFLICT, ex.getHttpStatus());
            verify(paymentRepository, never()).save(any());
        }

        @Test
        @DisplayName("💳 Deve aceitar pagamento com valor MAIOR que o pedido")
        void createPayment_WithAmountGreaterThanOrder_ShouldCreatePayment() {
            // Arrange
            PaymentRequestDto maiorValor = new PaymentRequestDto(
                    1L,
                    PaymentMethod.BCI,
                    new BigDecimal("1500.00"));

            when(orderService.findOrderByid(1L)).thenReturn(order);
            when(paymentRepository.existsByOrderId(1L)).thenReturn(false);
            when(paymentMapper.toEntity(maiorValor, order)).thenReturn(payment);
            when(paymentRepository.save(payment)).thenReturn(payment);
            when(paymentMapper.toResponseDto(payment)).thenReturn(paymentResponseDto);

            // Act
            PaymentResponseDto result = paymentService.createPayment(maiorValor);

            // Assert
            assertNotNull(result);
            verify(paymentRepository).save(payment);
            // Não deve lançar exceção - pagamento maior é permitido!
        }
    }

    @Nested
    @DisplayName("Operações Básicas de Pagamento")
    class BasicOperationsTests {

        @Test
        @DisplayName("🔄 Toggle Status: deve atualizar status do pagamento")
        void togglePaymentStatus_ShouldUpdatePaymentStatus() {
            // Arrange
            PaymentStatusUpdateRequest request = new PaymentStatusUpdateRequest(
                    1L,
                    PaymentStatus.SUCCESS);

            Payment paymentAtualizado = Payment.builder()
                    .order(order)
                    .amount(new BigDecimal("1000.00"))
                    .status(PaymentStatus.SUCCESS) // Status alterado
                    .build();
            paymentAtualizado.setId(1L);

            PaymentResponseDto responseAtualizado = new PaymentResponseDto(
                    1L, "TXN-123", PaymentMethod.BCI,
                    LocalDate.now(), new BigDecimal("1000.00"),
                    PaymentStatus.SUCCESS, 1L, "cliente@email.com");

            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(paymentAtualizado);
            when(paymentMapper.toResponseDto(paymentAtualizado)).thenReturn(responseAtualizado);

            // Act
            PaymentResponseDto result = paymentService.togglePaymentStatus(request);

            // Assert
            assertNotNull(result);
            assertEquals(PaymentStatus.SUCCESS, result.status());
            verify(paymentRepository).save(payment);
        }

        @Test
        @DisplayName("🔍 FindById: deve retornar pagamento quando existe")
        void findById_WithExistingId_ShouldReturnPayment() {
            // Arrange
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
            when(paymentMapper.toResponseDto(payment)).thenReturn(paymentResponseDto);

            // Act
            PaymentResponseDto result = paymentService.findById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("TXN-123", result.transactionReference());
            assertEquals(PaymentMethod.BCI, result.paymentMethod());
        }

        @Test
        @DisplayName("❌ FindById: deve lançar exceção quando pagamento não existe")
        void findById_WithNonExistingId_ShouldThrowException() {
            // Arrange
            when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.findById(999L));

            assertEquals("No payment found with the id: 999", ex.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        }

        @Test
        @DisplayName("🗑️ Delete: deve remover pagamento existente")
        void deletePayment_WithExistingId_ShouldDeletePayment() {
            // Arrange
            when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

            // Act
            paymentService.deletePayment(1L);

            // Assert
            verify(paymentRepository).delete(payment);
        }

        @Test
        @DisplayName("❌ Delete: deve lançar exceção quando pagamento não existe")
        void deletePayment_WithNonExistingId_ShouldThrowException() {
            // Arrange
            when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> paymentService.deletePayment(999L));

            assertEquals("No payment found with the id 999", ex.getMessage());
            assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
            verify(paymentRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("Listagem de Pagamentos")
    class FindAllPaymentsTests {

        @Test
        @DisplayName("📋 Deve retornar lista de todos os pagamentos")
        void findAllPayments_ShouldReturnAllPayments() {
            // Arrange
            List<Payment> payments = List.of(payment);
            List<PaymentResponseDto> responseDtos = List.of(paymentResponseDto);

            when(paymentRepository.findAll()).thenReturn(payments);
            when(paymentMapper.toResponseList(payments)).thenReturn(responseDtos);

            // Act
            List<PaymentResponseDto> result = paymentService.findAllPayments();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            verify(paymentRepository).findAll();
        }
    }
}