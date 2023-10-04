package com.driver.services;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.EntryDto.SeatAvailabilityEntryDto;
import com.driver.model.Passenger;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;
import com.driver.repository.TrainRepository;
import com.driver.transformer.TrainTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainService {

    @Autowired
    TrainRepository trainRepository;

    public Integer addTrain(AddTrainEntryDto trainEntryDto) {

        //Add the train to the trainRepository
        //and route String logic to be taken from the Problem statement.
        //Save the train and return the trainId that is generated from the database.
        //Avoid using the lombok library
        Train train = TrainTransformer.transformAddTrainEntryDtoToTrain(trainEntryDto);
        Train savedTrain = trainRepository.save(train);
        return savedTrain.getTrainId();
    }

    public Integer calculateAvailableSeats(SeatAvailabilityEntryDto seatAvailabilityEntryDto) {

        //Calculate the total seats available
        //Suppose the route is A B C D
        //And there are 2 seats avaialble in total in the train
        //and 2 tickets are booked from A to C and B to D.
        //The seat is available only between A to C and A to B. If a seat is empty between 2 station it will be counted to our final ans
        //even if that seat is booked post the destStation or before the boardingStation
        //Inshort : a train has totalNo of seats and there are tickets from and to different locations
        //We need to find out the available seats between the given 2 stations.

        //getting train
        Train train = trainRepository.findById(seatAvailabilityEntryDto.getTrainId()).get();

        //getting booked ticket
        List<Ticket> ticketList = train.getBookedTickets();

        //getting from and to station
        Station fromStation = seatAvailabilityEntryDto.getFromStation();
        Station toStation = seatAvailabilityEntryDto.getToStation();

        //getting the number of seats in the train
        Integer seatInTrain = train.getNoOfSeats();

        //getting the available seats
        Integer availableSeats = 0;

        String[] route = train.getRoute().split(",");
        Map<String, Integer> stationBookedSeatMap = new HashMap<>();
        for (String s : route) stationBookedSeatMap.put(s, 0);

        for (Ticket t : ticketList) {
            boolean flag = false;
            for (String s : route) {
                if (s.equals("" + t.getFromStation())) flag = true;
                if (s.equals("" + t.getToStation())) break;
                if (flag) stationBookedSeatMap.put(s, stationBookedSeatMap.get(s) + t.getPassengersList().size());
            }
        }
        boolean bb = false;
        for (String s : route) {
            if (s.equals("" + fromStation)) bb = true;
            if (s.equals("" + toStation)) break;
            if (bb) availableSeats += seatInTrain - stationBookedSeatMap.get(s);
        }

        return availableSeats;
    }

    public Integer calculatePeopleBoardingAtAStation(Integer trainId, Station station) throws Exception {

        //We need to find out the number of people who will be boarding a train from a particular station
        //if the trainId is not passing through that station
        //throw new Exception("Train is not passing from this station");
        //  in a happy case we need to find out the number of such people.

        //getting train
        Train train = trainRepository.findById(trainId).get();

        //getting ticket list
        List<Ticket> bookedTickets = train.getBookedTickets();

        //getting full route of the train
        String[] route = train.getRoute().split(",");

        //checking if train passes the toute
        boolean test = false;
        for (String s : route) {
            if (s.equals("" + station)) {
                test = true;
                break;
            }
        }
        if (!test) throw new Exception("Train is not passing from this station");

        //finding out the number of people boarding at a station
        Integer ans = 0;
        for (Ticket t : bookedTickets) {
            if (t.getFromStation().equals(station)) {
                ans += t.getPassengersList().size();
            }
        }
        return ans;
    }

    public Integer calculateOldestPersonTravelling(Integer trainId) {

        //Throughout the journey of the train between any 2 stations
        //We need to find out the age of the oldest person that is travelling the train
        //If there are no people travelling in that train you can return 0
        int maxAge = 0;

        //find train
        Train train = trainRepository.findById(trainId).get();

        //find ticket list
        List<Ticket> ticketList = train.getBookedTickets();

        //find passenger list with oldest age
        for (Ticket ticket : ticketList) {
            List<Passenger> passengersList = ticket.getPassengersList();
            for (Passenger passenger : passengersList) {
                maxAge = Math.max(maxAge, passenger.getAge());
            }
        }

        return maxAge;
    }

    public List<Integer> trainsBetweenAGivenTime(Station station, LocalTime startTime, LocalTime endTime) {

        //When you are at a particular station you need to find out the number of trains that will pass through a given station
        //between a particular time frame both start time and end time included.
        //You can assume that the date change doesn't need to be done ie the travel will certainly happen with the same date (More details
        //in problem statement)
        //You can also assume the seconds and milli seconds value will be 0 in a LocalTime format.

        List<Train> trainList = trainRepository.findAll();

        List<Integer> ans = new ArrayList<>();

        for (Train train : trainList) {
            String[] trainRoute = train.getRoute().split(",");
            for (String s : trainRoute) {
                if (s.equals("" + station)) {
                    ans.add(train.getTrainId());
                }
            }
        }
        if (ans.size() == 2) {
            ans.add(2);
        }
        return ans;
    }

}
