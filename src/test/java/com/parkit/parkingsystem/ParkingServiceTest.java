package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    @BeforeEach
    private void setUpPerTest() throws Exception {
        try {
            lenient().when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            lenient().when(inputReaderUtil.readSelection()).thenReturn(1);
            LocalDateTime inTime = LocalDateTime.now().minusMinutes(60);

            ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            Ticket ticket = new Ticket();
            ticket.setInTime(inTime);
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber("ABCDEF");
            lenient().when(ticketDAO.getTicket(anyString())).thenReturn(ticket);

            lenient().when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            lenient().when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            lenient().when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(23);
            lenient().when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(751);

            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    public void processExitingVehicleTest() {
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    @Test
    public void processIncomingRegularVehicleTest() {
        parkingService.processIncomingVehicle();
        verify(ticketDAO, Mockito.times(1)).getTicket(anyString());
    }

    @Test
    public void processExitingRegularVehicleTest() {
        List<Ticket> tickets = new ArrayList<Ticket>();

        Ticket ticket = new Ticket();
        ticket.setInTime(LocalDateTime.now().minusHours(1));
        ticket.setVehicleRegNumber("ABCDEF");
        tickets.add(ticket);

        Ticket ticketBis = new Ticket();
        ticket.setInTime(LocalDateTime.now().minusHours(1));
        ticket.setVehicleRegNumber("ABCDEF");
        tickets.add(ticketBis);

        lenient().when(ticketDAO.getTickets(anyString())).thenReturn(tickets);

        parkingService.processExitingVehicle();
        verify(ticketDAO, Mockito.times(1)).getTickets(anyString());
        Ticket ticketRegular = ticketDAO.getTicket("ABCDEF");
        Assertions.assertEquals(0.7125, ticketRegular.getPrice());

    }

}
