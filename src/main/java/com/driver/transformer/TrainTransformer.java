package com.driver.transformer;

import com.driver.EntryDto.AddTrainEntryDto;
import com.driver.model.Station;
import com.driver.model.Ticket;
import com.driver.model.Train;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainTransformer {
    public static Train transformAddTrainEntryDtoToTrain(AddTrainEntryDto trainEntryDto) {
        Train train = new Train();

        //creating route
        StringBuilder route = new StringBuilder();
        for (Station station : trainEntryDto.getStationRoute()) {
            route.append(station).append(",");
        }
        route.deleteCharAt(route.length() - 1);

        //setting the values
        train.setRoute(route.toString());
        train.setBookedTickets(new ArrayList<>());
        train.setDepartureTime(trainEntryDto.getDepartureTime());
        train.setNoOfSeats(trainEntryDto.getNoOfSeats());

        //returning train
        return train;
    }

    public static int getBookedSeat(List<Ticket> bookedTickets, Station fromStation, Station toStation, Train train) {
        int from = 0, to = 0;

        String[] routes = train.getRoute().split(",");
        Map<String, Integer> stationBooking = new HashMap<>();
        for (String r : routes) {
            stationBooking.put(r, 0);
        }

        for (Ticket ticket : bookedTickets) {
            Station fromStation1 = ticket.getFromStation();
            Station toStation1 = ticket.getToStation();
            boolean flag = false;
            for (String route : routes) {
                if (route.equals("" + fromStation1)) flag = true;
                if (route.equals("" + toStation1)) break;
                if (flag) stationBooking.put(route, stationBooking.get(route) + ticket.getPassengersList().size());
            }
        }
        from = stationBooking.get("" + fromStation);
        to = stationBooking.get("" + toStation);

        return Math.max(from, to);
    }
}
