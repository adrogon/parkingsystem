package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.LocalDateTime;
import java.time.Duration;

import static com.parkit.parkingsystem.constants.Fare.FREE_HALF_HOUR;
import static com.parkit.parkingsystem.constants.Fare.SIXTY_MINUTES;


public class FareCalculatorService {

    public void calculateFare(Ticket ticket){

        if( (ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        LocalDateTime inTime = ticket.getInTime();
        LocalDateTime outTime = ticket.getOutTime();

        Duration d = Duration.between(inTime, outTime);
        double duration = (double) d.toMinutes() / SIXTY_MINUTES;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                double price = (duration < FREE_HALF_HOUR) ? 0: (duration - FREE_HALF_HOUR) * Fare.CAR_RATE_PER_HOUR/SIXTY_MINUTES;
                ticket.setPrice(price);
                break;
            }
            case BIKE: {
                double price = (duration < FREE_HALF_HOUR) ? 0: (duration - FREE_HALF_HOUR) * Fare.BIKE_RATE_PER_HOUR/SIXTY_MINUTES;
                ticket.setPrice(price);
                break;
            }
            default: throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}
