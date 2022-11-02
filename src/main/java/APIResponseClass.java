//import com.fasterxml.jackson.annotation.JsonProperty;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class APIResponseClass implements Serializable{
    @JsonProperty("location")
    private Object location;

    @JsonProperty("timeStamp")
    private Object date;

    @JsonProperty("value")
    private double rating;

    @JsonProperty("duration")
    private Object duration;

    public APIResponseClass(){

    }

    public void setLocation(Object loc){
        this.location = loc;
    }
    public Object getLocation(){
        return this.location;
    }
    public void setTime(Object time){
        this.date = time;
    }
    public Object getTime(){
        return this.date;
    }
    public void setRating(double rating){
        this.rating = rating;
    }
    public double getValue(){
        return this.rating;
    }
    public void setDuration(Object duration){
        this.duration = duration;
    }
    public Object getDuration(){
        return this.duration;
    }


}

