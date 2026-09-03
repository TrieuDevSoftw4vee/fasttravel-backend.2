package com.fasttravel.service.impl;
import com.fasttravel.dao.*;import com.fasttravel.dto.BookingDTO.*;import com.fasttravel.entity.*;import com.fasttravel.exception.AppException;import com.fasttravel.repository.*;import com.fasttravel.service.BookingService;import com.fasttravel.util.VnPayUtil;import lombok.RequiredArgsConstructor;import org.springframework.beans.factory.annotation.Value;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.math.BigDecimal;import java.time.LocalDateTime;import java.util.*;
@Service @RequiredArgsConstructor public class BookingServiceImpl implements BookingService{
private final BookingDAO bookings;private final UserDAO users;private final TripDAO trips;private final TripSeatRepository seats;private final TicketRepository tickets;private final PaymentRepository payments;private final VnPayUtil vnpay;@Value("${app.booking.hold-minutes}") int holdMinutes;
@Transactional public Created create(Long uid,CreateRequest r,String ip){if(!"VNPAY".equalsIgnoreCase(r.paymentMethod()))throw AppException.bad("Bản rút gọn chỉ hỗ trợ VNPay Sandbox");User u=users.findById(uid).orElseThrow(()->AppException.notFound("Tài khoản không tồn tại"));Trip t=trips.findById(r.tripId()).orElseThrow(()->AppException.notFound("Chuyến xe không tồn tại"));if(t.getStatus()!=Trip.Status.SCHEDULED||t.getDepartureTime().isBefore(LocalDateTime.now()))throw AppException.bad("Chuyến xe không còn mở bán");List<Long> ids=r.passengers().stream().map(Passenger::tripSeatId).distinct().toList();if(ids.size()!=r.passengers().size())throw AppException.bad("Không được chọn trùng ghế");List<TripSeat> locked=seats.lockSeats(t.getId(),ids);if(locked.size()!=ids.size()||locked.stream().anyMatch(x->x.getStatus()!=TripSeat.Status.AVAILABLE))throw AppException.conflict("Một hoặc nhiều ghế vừa được người khác giữ");String token=UUID.randomUUID().toString();locked.forEach(x->{x.setStatus(TripSeat.Status.HELD);x.setHoldToken(token);x.setHoldExpiresAt(LocalDateTime.now().plusMinutes(holdMinutes));});seats.saveAll(locked);BigDecimal subtotal=t.getPrice().multiply(BigDecimal.valueOf(r.passengers().size()));Booking b=new Booking();b.setCode("FT"+System.currentTimeMillis());b.setUser(u);b.setTrip(t);b.setSubtotal(subtotal);b.setDiscountAmount(BigDecimal.ZERO);b.setTotalAmount(subtotal);b.setPaymentMethod("VNPAY");b.setExpiresAt(LocalDateTime.now().plusMinutes(holdMinutes));b=bookings.save(b);Map<Long,TripSeat> seatMap=new HashMap<>();locked.forEach(x->seatMap.put(x.getId(),x));for(Passenger p:r.passengers()){Ticket x=new Ticket();x.setCode("TK"+UUID.randomUUID().toString().substring(0,12).toUpperCase());x.setBooking(b);x.setTripSeat(seatMap.get(p.tripSeatId()));x.setPassengerName(p.fullName());x.setPassengerPhone(p.phone());x.setPassengerEmail(p.email());x.setTicketPrice(t.getPrice());x.setQrPayload("FASTTRAVEL|"+x.getCode()+"|"+b.getCode());tickets.save(x);}Payment pay=new Payment();pay.setBooking(b);pay.setTransactionCode("PAY"+System.currentTimeMillis());pay.setAmount(b.getTotalAmount());payments.save(pay);String url=vnpay.create(b.getCode(),b.getTotalAmount().longValue(),ip);return new Created(b.getId(),b.getCode(),subtotal,BigDecimal.ZERO,b.getTotalAmount(),url,holdMinutes);}
public List<Map<String,Object>> history(Long uid){return bookings.findByUser(uid).stream().map(this::view).toList();}
private Map<String,Object> view(Booking b){var m=new LinkedHashMap<String,Object>();m.put("id",b.getId());m.put("code",b.getCode());m.put("status",b.getStatus());m.put("trip",Map.of("id",b.getTrip().getId(),"code",b.getTrip().getCode(),"route",b.getTrip().getRoute().getName(),"departureTime",b.getTrip().getDepartureTime()));m.put("subtotal",b.getSubtotal());m.put("discountAmount",b.getDiscountAmount());m.put("totalAmount",b.getTotalAmount());m.put("paymentMethod",b.getPaymentMethod());m.put("createdAt",b.getCreatedAt());m.put("tickets",tickets.findByBookingId(b.getId()).stream().map(x->Map.of("code",x.getCode(),"seat",x.getTripSeat().getSeat().getSeatNumber(),"passengerName",x.getPassengerName(),"status",x.getStatus(),"qrPayload",x.getQrPayload())).toList());return m;}
@Transactional public void confirmPayment(String code,String gateway,String response,long vnpAmount,String bankCode){Booking b=bookings.findByCode(code).orElseThrow(()->AppException.notFound("Booking không tồn tại"));long expected=b.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValueExact();if(vnpAmount!=expected)throw AppException.bad("Số tiền VNPay trả về không khớp booking");Payment p=payments.findFirstByBookingIdAndStatusOrderByCreatedAtDesc(b.getId(),Payment.Status.PENDING).orElse(null);if(!"00".equals(response)){if(p!=null){p.setStatus(Payment.Status.FAILED);p.setGatewayTransactionId(gateway);p.setBankCode(bankCode);p.setResponseCode(response);p.setErrorMessage("VNPay response code: "+response);p.setTransactionTime(LocalDateTime.now());payments.save(p);}throw AppException.bad("Thanh toán VNPay không thành công (mã "+response+")");}if(b.getStatus()==Booking.Status.PAID)return;if(b.getStatus()!=Booking.Status.PENDING_PAYMENT)throw AppException.bad("Booking không còn ở trạng thái chờ thanh toán");if(b.getExpiresAt()!=null&&b.getExpiresAt().isBefore(LocalDateTime.now()))throw AppException.bad("Booking đã hết thời gian giữ ghế");if(p==null)throw AppException.bad("Không tìm thấy giao dịch thanh toán đang chờ");b.setStatus(Booking.Status.PAID);b.setPaidAt(LocalDateTime.now());bookings.save(b);tickets.findByBookingId(b.getId()).forEach(x->{x.setStatus(Ticket.Status.VALID);x.getTripSeat().setStatus(TripSeat.Status.BOOKED);x.getTripSeat().setHoldToken(null);x.getTripSeat().setHoldExpiresAt(null);seats.save(x.getTripSeat());tickets.save(x);});p.setStatus(Payment.Status.SUCCESS);p.setGatewayTransactionId(gateway);p.setBankCode(bankCode);p.setResponseCode(response);p.setTransactionTime(LocalDateTime.now());payments.save(p);}
    @Transactional
    public Map<String, Object> cancelBooking(Long userId, String bookingCode, String reason) {
        Booking booking = bookings.findByCode(bookingCode)
                .orElseThrow(() -> AppException.notFound("Không tìm thấy đơn đặt vé"));

        if (!booking.getUser().getId().equals(userId)) {
            throw AppException.bad("Bạn không có quyền thực hiện thao tác này");
        }

        if (booking.getStatus() != Booking.Status.PAID) {
            throw AppException.bad("Chỉ có thể hủy đơn hàng đã thanh toán thành công");
        }

        LocalDateTime departureTime = booking.getTrip().getDepartureTime();
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(departureTime)) {
            throw AppException.bad("Chuyến xe đã khởi hành, không thể hủy vé");
        }

        long hoursUntilDeparture = java.time.Duration.between(now, departureTime).toHours();
        double refundRate;

        if (hoursUntilDeparture >= 24) {
            refundRate = 0.90;
        } else if (hoursUntilDeparture >= 12) {
            refundRate = 0.70;
        } else if (hoursUntilDeparture >= 3) {
            refundRate = 0.50;
        } else {
            throw AppException.bad("Vé sát giờ khởi hành (dưới 3 tiếng) không hỗ trợ hủy refund");
        }

        BigDecimal totalAmount = booking.getTotalAmount();
        BigDecimal refundAmount = totalAmount.multiply(BigDecimal.valueOf(refundRate));

        booking.setStatus(Booking.Status.REFUNDED);
        booking.setCancelledAt(now);
        booking.setCancellationReason(reason);
        bookings.save(booking);

        List<Ticket> ticketList = tickets.findByBookingId(booking.getId());
        for (Ticket ticket : ticketList) {
            ticket.setStatus(Ticket.Status.CANCELLED);
            TripSeat seat = ticket.getTripSeat();
            seat.setStatus(TripSeat.Status.AVAILABLE);
            seat.setHoldToken(null);
            seat.setHoldExpiresAt(null);
            seats.save(seat);
            tickets.save(ticket);
        }

        Payment refundPayment = new Payment();
        refundPayment.setBooking(booking);
        refundPayment.setTransactionCode("REF" + System.currentTimeMillis());
        refundPayment.setType(Payment.Type.REFUND);
        refundPayment.setStatus(Payment.Status.SUCCESS);
        refundPayment.setAmount(refundAmount);
        refundPayment.setTransactionTime(now);
        payments.save(refundPayment);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("bookingCode", bookingCode);
        result.put("totalAmount", totalAmount);
        result.put("refundRate", (int)(refundRate * 100) + "%");
        result.put("refundAmount", refundAmount);
        result.put("cancelledAt", now);
        return result;
    }
@Transactional public void releaseExpired(){seats.releaseExpired(LocalDateTime.now(),TripSeat.Status.AVAILABLE,TripSeat.Status.HELD);for(Booking b:bookings.findExpiredPending(LocalDateTime.now())){b.setStatus(Booking.Status.EXPIRED);bookings.save(b);}}
}
