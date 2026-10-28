package com.hotelreservation;

import com.hotelreservation.dao.BookingDAO;
import com.hotelreservation.dao.RoomDAO;
import com.hotelreservation.dao.UserDAO;
import com.hotelreservation.model.Booking;
import com.hotelreservation.model.BookingStatus;
import com.hotelreservation.model.Room;
import com.hotelreservation.model.RoomType;
import com.hotelreservation.model.dto.BookingCostBreakdown;
import com.hotelreservation.service.BookingService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Automated Unit Tests verifying core BookingService business logic,
 * validation rules, ownership enforcement, and conflict rejection.
 */
public class BookingServiceLogicTest {

    private BookingDAO mockBookingDAO;
    private RoomDAO mockRoomDAO;
    private UserDAO mockUserDAO;
    private BookingService bookingService;

    private ObjectId testRoomId;
    private Room testRoom;
    private ObjectId testUserId;

    @Before
    public void setUp() {
        mockBookingDAO = Mockito.mock(BookingDAO.class);
        mockRoomDAO = Mockito.mock(RoomDAO.class);
        mockUserDAO = Mockito.mock(UserDAO.class);

        bookingService = new BookingService(mockBookingDAO, mockRoomDAO, mockUserDAO);

        testRoomId = new ObjectId();
        testRoom = new Room("201", RoomType.DELUXE, "Deluxe Ocean View", "Stunning view",
                new BigDecimal("200.00"), 2, new ArrayList<>(), "assets/images/room-deluxe-1.svg");
        testRoom.setId(testRoomId);
        testRoom.setActive(true);

        testUserId = new ObjectId();

        when(mockRoomDAO.findById(testRoomId)).thenReturn(testRoom);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectPastCheckInDate() {
        String pastDate = LocalDate.now().minusDays(2).toString();
        String checkout = LocalDate.now().plusDays(2).toString();

        bookingService.createReservation(testUserId, testRoomId.toHexString(), pastDate, checkout, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectCheckOutBeforeCheckIn() {
        String checkin = LocalDate.now().plusDays(5).toString();
        String checkout = LocalDate.now().plusDays(2).toString();

        bookingService.createReservation(testUserId, testRoomId.toHexString(), checkin, checkout, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectGuestCountExceedingCapacity() {
        String checkin = LocalDate.now().plusDays(1).toString();
        String checkout = LocalDate.now().plusDays(3).toString();

        // testRoom capacity is 2, requesting 4
        bookingService.createReservation(testUserId, testRoomId.toHexString(), checkin, checkout, 4);
    }

    @Test(expected = IllegalStateException.class)
    public void testRejectOverlappingBookingConflict() {
        String checkin = LocalDate.now().plusDays(2).toString();
        String checkout = LocalDate.now().plusDays(5).toString();

        // Simulate an existing conflicting booking returned by DAO
        Booking conflict = new Booking();
        conflict.setStatus(BookingStatus.CONFIRMED);
        when(mockBookingDAO.findOccupyingBookingsForRoom(eq(testRoomId), eq(checkin), eq(checkout)))
                .thenReturn(Collections.singletonList(conflict));

        bookingService.createReservation(testUserId, testRoomId.toHexString(), checkin, checkout, 2);
    }

    @Test
    public void testCreateValidBooking() {
        String checkin = LocalDate.now().plusDays(2).toString();
        String checkout = LocalDate.now().plusDays(5).toString();

        when(mockBookingDAO.findOccupyingBookingsForRoom(eq(testRoomId), eq(checkin), eq(checkout)))
                .thenReturn(Collections.emptyList());
        when(mockBookingDAO.create(any(Booking.class))).thenReturn(true);

        Booking booking = bookingService.createReservation(testUserId, testRoomId.toHexString(), checkin, checkout, 2);

        assertNotNull(booking);
        assertTrue(booking.getBookingNumber().startsWith("HRS-"));
        assertEquals(3, booking.getNumberOfNights());
        assertEquals(new BigDecimal("200.00"), booking.getPricePerNightAtBooking());
        assertEquals(new BigDecimal("600.00"), booking.getSubtotal());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        verify(mockBookingDAO, times(1)).create(any(Booking.class));
    }

    @Test(expected = SecurityException.class)
    public void testCancelBookingOwnershipEnforcement() {
        ObjectId otherUserId = new ObjectId();
        ObjectId bookingId = new ObjectId();

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setUserId(otherUserId); // Owned by another customer
        booking.setStatus(BookingStatus.CONFIRMED);

        when(mockBookingDAO.findById(bookingId)).thenReturn(booking);

        // Attempting to cancel with testUserId who does NOT own the booking and is NOT admin
        bookingService.cancelReservation(bookingId.toHexString(), testUserId, false);
    }

    @Test
    public void testAdminCanCancelAnyBooking() {
        ObjectId otherUserId = new ObjectId();
        ObjectId bookingId = new ObjectId();

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setUserId(otherUserId);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(mockBookingDAO.findById(bookingId)).thenReturn(booking);
        when(mockBookingDAO.updateStatus(eq(bookingId), eq(BookingStatus.CANCELLED))).thenReturn(true);

        // Admin (isAdmin = true) can cancel without matching userId
        boolean result = bookingService.cancelReservation(bookingId.toHexString(), null, true);
        assertTrue("Admin must be able to cancel any eligible booking", result);
    }
}

