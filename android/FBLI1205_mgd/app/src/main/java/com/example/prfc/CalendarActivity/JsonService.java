package com.example.prfc.CalendarActivity;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

public interface JsonService {

    @GET("/1kpjf")
    void listEvents(Callback<List<Event>> eventsCallback);

}
