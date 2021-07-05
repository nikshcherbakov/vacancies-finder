package com.nikshcherbakov.vacanciesfinder.utils;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;

public class GoogleMapsLeg {

    public static class GoogleMapsTextValueObject {
        private String text;
        private Integer value;

        public GoogleMapsTextValueObject(@NotNull String text, @NotNull Integer value) {
            this.text = text;
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }

    @NotNull
    private GoogleMapsTextValueObject distance;

    @NotNull
    private GoogleMapsTextValueObject duration;

    @Nullable
    private GoogleMapsTextValueObject durationInTraffic;

    public GoogleMapsTextValueObject getDistance() {
        return distance;
    }

    public void setDistance(GoogleMapsTextValueObject distance) {
        this.distance = distance;
    }

    public GoogleMapsTextValueObject getDuration() {
        return duration;
    }

    public void setDuration(GoogleMapsTextValueObject duration) {
        this.duration = duration;
    }

    public GoogleMapsTextValueObject getDurationInTraffic() {
        return durationInTraffic;
    }

    public void setDurationInTraffic(GoogleMapsTextValueObject durationInTraffic) {
        this.durationInTraffic = durationInTraffic;
    }
}
