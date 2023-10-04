package com.driver.services;


import com.driver.EntryDto.BookTicketEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.PassengerRepository;
import com.driver.repository.TicketRepository;
import com.driver.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TicketService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    TrainRepository trainRepository;

    @Autowired
    PassengerRepository passengerRepository;


    public Integer bookTicket(BookTicketEntryDto bookTicketEntryDto) throws Exception {

        //Check for validity
        //Use bookedTickets List from the TrainRepository to get bookings done against that train
        // Incase the there are insufficient tickets
        // throw new Exception("Less tickets are available");
        //otherwise book the ticket, calculate the price and other details
        //Save the information in corresponding DB Tables
        //Fare System : Check problem statement
        //Incase the train doesn't pass through the requested stations
        //throw new Exception("Invalid stations");
        //Save the bookedTickets in the train Object
        //Also in the passenger Entity change the attribute bookedTickets by using the attribute bookingPersonId.
        //And the end return the ticketId that has come from db

        //get the train
        Train train = trainRepository.findById(bookTicketEntryDto.getTrainId()).get();

        //getting the booked list
        List<Ticket> bookedTickets = train.getBookedTickets();

        //getting the booked tickets from src to dest
        int bookedTicketFromSrcToDest = TrainService
                .getBookedSeat(bookedTickets, bookTicketEntryDto.getFromStation(), bookTicketEntryDto.getToStation(), train);

        //checking if the seats are available
        int totalSeats = train.getNoOfSeats();
        int seatsAfterBooking = bookedTicketFromSrcToDest + bookTicketEntryDto.getNoOfSeats();
        if (seatsAfterBooking > totalSeats) throw new Exception("Less tickets are available");

        //creating new Ticket
        Ticket ticket = new Ticket();

        //getting list of passenger
        List<Passenger> passengerList = passengerRepository.findAllById(bookTicketEntryDto.getPassengerIds());

        //setting passenger in ticket
        ticket.setPassengersList(passengerList);

        //setting train in ticket
        ticket.setTrain(train);


        //checking if the train passes through the stations
        Set<String> hs = new HashSet<>(Arrays.asList(train.getRoute().split(",")));
        if (!hs.contains("" + bookTicketEntryDto.getFromStation()) || !hs.contains("" + bookTicketEntryDto.getToStation())) {
            throw new Exception("Invalid stations");
        }


        //setting src and dest in ticket
        ticket.setFromStation(bookTicketEntryDto.getFromStation());
        ticket.setToStation(bookTicketEntryDto.getToStation());

        //calculating fare
        int totalFare = calculateFare(bookTicketEntryDto, train);
        ticket.setTotalFare(totalFare);


        //adding ticket to passenger
        Passenger bookingPerson = passengerRepository.findById(bookTicketEntryDto.getBookingPersonId()).get();
        bookingPerson.getBookedTickets().add(ticket);

        //saving ticket
        Ticket savedTicket = ticketRepository.save(ticket);

        return savedTicket.getTicketId();
    }

    private int calculateFare(BookTicketEntryDto bookTicketEntryDto, Train train) {
        int vias = 0;

        Station fromStation = bookTicketEntryDto.getFromStation();
        Station toStation = bookTicketEntryDto.getToStation();
        String[] stations = train.getRoute().split(",");
        boolean flag = false;
        for (String s : stations) {
            if (s.equals("" + fromStation)) flag = true;
            if (s.equals("" + toStation)) break;
            if (flag) vias++;
        }

        return vias * 300 * bookTicketEntryDto.getNoOfSeats();
    }
}
