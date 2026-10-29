package com.hotelreservation;

import com.hotelreservation.dao.HotelDAO;
import com.hotelreservation.model.Hotel;
import com.hotelreservation.model.Role;
import com.hotelreservation.service.HotelService;
import java.util.Arrays;
import java.util.List;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Hotel partner profile operations, validation, and detail editing.
 */
public class HotelServiceTest {

    private HotelDAO mockHotelDAO;
    private HotelService hotelService;

    @Before
    public void setUp() {
        mockHotelDAO = Mockito.mock(HotelDAO.class);
        hotelService = new HotelService(mockHotelDAO);
    }

    @Test
    public void testRoleHotelEnum() {
        assertEquals(Role.HOTEL, Role.fromString("HOTEL"));
        assertEquals("Hotel Partner", Role.HOTEL.getDisplayName());
    }

    @Test
    public void testUpdateHotelDetailsSuccess() {
        ObjectId hotelId = new ObjectId();
        ObjectId managerUserId = new ObjectId();

        Hotel existingHotel = new Hotel("Old Hotel Name", "Miami", "123 Beach Blvd", "555-1234", "info@oldhotel.com", managerUserId);
        existingHotel.setId(hotelId);

        when(mockHotelDAO.findById(hotelId)).thenReturn(existingHotel);
        when(mockHotelDAO.update(any(Hotel.class))).thenReturn(true);

        List<String> newAmenities = Arrays.asList("Free High-Speed Wi-Fi", "Swimming Pool", "Luxury Spa");

        Hotel updated = hotelService.updateHotelDetails(
                hotelId,
                managerUserId,
                false, // not admin
                "Grand Ocean View Resort",
                "Luxury oceanfront retreat with private cabanas and infinity pool.",
                "456 Coastal Highway",
                "Miami Beach",
                "Florida",
                "USA",
                "+1 (305) 555-9999",
                "reservations@grandocean.com",
                5,
                newAmenities,
                "assets/images/hero-hotel.svg",
                "15:00",
                "11:00"
        );

        assertNotNull(updated);
        assertEquals("Grand Ocean View Resort", updated.getName());
        assertEquals("Miami Beach", updated.getCity());
        assertEquals(5, updated.getStarRating());
        assertEquals("reservations@grandocean.com", updated.getEmail());
        assertEquals(3, updated.getAmenities().size());
        verify(mockHotelDAO, times(1)).update(existingHotel);
    }

    @Test(expected = SecurityException.class)
    public void testUnauthorizedHotelUpdateBlocked() {
        ObjectId hotelId = new ObjectId();
        ObjectId ownerUserId = new ObjectId();
        ObjectId impostorUserId = new ObjectId();

        Hotel existingHotel = new Hotel("Legitimate Hotel", "Azure Bay", "100 Pier", "555-0000", "hotel@test.com", ownerUserId);
        existingHotel.setId(hotelId);

        when(mockHotelDAO.findById(hotelId)).thenReturn(existingHotel);

        // Impostor attempts to update another hotel's details
        hotelService.updateHotelDetails(
                hotelId,
                impostorUserId,
                false,
                "Hijacked Hotel Name",
                "New description",
                "Street",
                "City",
                "State",
                "Country",
                "Phone",
                "email@test.com",
                4,
                Arrays.asList("Wi-Fi"),
                "image.jpg",
                "14:00",
                "11:00"
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateWithEmptyHotelNameThrowsException() {
        ObjectId hotelId = new ObjectId();
        ObjectId managerUserId = new ObjectId();

        hotelService.updateHotelDetails(
                hotelId,
                managerUserId,
                false,
                "", // empty name
                "Desc",
                "Addr",
                "City",
                "State",
                "Country",
                "Phone",
                "email@test.com",
                4,
                Arrays.asList("Wi-Fi"),
                "image.jpg",
                "14:00",
                "11:00"
        );
    }
}
