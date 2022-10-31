import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Time;
import java.util.Date;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Time;
import java.util.Date;

public class APIResponseClass {
    @JsonProperty("location")
    private String location;

    @JsonProperty("time")
    private Date date;

    @JsonProperty("rating")
    private double rating;

    @JsonProperty("duration")
    private Time duration;

    public APIResponseClass(){

    }

    public void setLocation(String loc){
        this.location = loc;
    }
    public String getLocation(){
        return this.location;
    }
    public void setTime(Date time){
        this.date = time;
    }
    public Date getTime(){
        return this.date;
    }
    public void setRating(double rating){
        this.rating = rating;
    }
    public double getRating(){
        return this.rating;
    }
    public void setDuration(Time duration){
        this.duration = duration;
    }
    public Time getDuration(){
        return this.duration;
    }


}

