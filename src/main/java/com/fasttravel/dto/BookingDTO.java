package com.fasttravel.dto;
import jakarta.validation.constraints.*;import java.math.BigDecimal;import java.util.List;
public final class BookingDTO{private BookingDTO(){}public record Passenger(@NotBlank String fullName,@Pattern(regexp="0[0-9]{9}") String phone,@Email String email,@NotNull Long tripSeatId){}public record CreateRequest(@NotNull Long tripId,@NotEmpty List<Passenger> passengers,@NotBlank String paymentMethod){}public record Created(Long bookingId,String bookingCode,BigDecimal subtotal,BigDecimal discountAmount,BigDecimal totalAmount,String paymentUrl,int holdMinutes){}}
