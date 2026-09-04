package com.fasttravel.listener;

import com.fasttravel.entity.Booking;
import com.fasttravel.entity.Ticket;
import com.fasttravel.event.BookingSuccessEvent;
import com.fasttravel.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final JavaMailSender mailSender;
    private final TicketRepository tickets;

    @Async
    @EventListener
    public void handle(BookingSuccessEvent event) {
        Booking b = event.booking();
        List<Ticket> ticketList = tickets.findByBookingId(b.getId());

        if (ticketList.isEmpty()) return;

        String email = ticketList.get(0).getPassengerEmail();
        if (email == null || email.isBlank()) return;

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("FastTravel - Xác nhận đặt vé thành công (Mã: " + b.getCode() + ")");
        msg.setText(String.format(
                "Cảm ơn bạn đã đặt vé tại FastTravel!\n\n" +
                        "Mã đơn hàng: %s\n" +
                        "Tổng tiền: %s VNĐ\n\n" +
                        "Vui lòng có mặt tại bến xe trước giờ khởi hành ít nhất 30 phút để làm thủ tục.\n" +
                        "Chúc bạn có một chuyến đi an toàn và vui vẻ!",
                b.getCode(), b.getTotalAmount()
        ));

        mailSender.send(msg);
    }
}