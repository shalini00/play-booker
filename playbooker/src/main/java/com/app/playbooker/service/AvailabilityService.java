package com.app.playbooker.service;

import com.app.playbooker.dto.SlotInfoDTO;
import com.app.playbooker.entity.Booking;
import com.app.playbooker.entity.PlaySpace;
import com.app.playbooker.enums.BookingStatus;
import com.app.playbooker.exceptions.PlaySpaceNotFoundException;
import com.app.playbooker.models.OpeningHours;
import com.app.playbooker.repository.BookingRepository;
import com.app.playbooker.repository.PlaySpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AvailabilityService {

    @Autowired
    private PlaySpaceRepository playSpaceRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public List<SlotInfoDTO> getSlotInfo(String playSpaceId, LocalDate date) {
        PlaySpace playSpace = playSpaceRepository.findById(playSpaceId).orElseThrow(() -> new PlaySpaceNotFoundException(playSpaceId));

        List<OpeningHours> openingHours = playSpace.getWeeklyOpeningHours();
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        LocalTime open = openingHours.stream()
                .filter(o -> o.getDayOfWeek() == dayOfWeek)
                .map(OpeningHours::getOpenTime)
                .findFirst()
                .orElse(null);

        LocalTime close = openingHours.stream()
                .filter(o -> o.getDayOfWeek() == dayOfWeek)
                .map(OpeningHours::getCloseTime)
                .findFirst()
                .orElse(null);

        List<Booking> bookings = bookingRepository.findByPlaySpaceIdAndBookingDateAndBookingStatusIn(playSpaceId, date, List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING));

        List<SlotInfoDTO> slotInfoDTOList = new ArrayList<>();

        LocalTime slotStart = open;
        while(slotStart.isBefore(close)) {
            LocalTime slotEnd = slotStart.plusMinutes(30);
            LocalTime finalSlotStart = slotStart;
            boolean isBooked = bookings.stream()
                    .anyMatch(b -> finalSlotStart.isBefore(LocalTime.from(b.getEndTime())) && slotEnd.isAfter(LocalTime.from(b.getStartTime())));

            slotInfoDTOList.add(new SlotInfoDTO(slotStart, !isBooked));
            slotStart = slotStart.plusMinutes(30);
        }

        return slotInfoDTOList;
    }
}
