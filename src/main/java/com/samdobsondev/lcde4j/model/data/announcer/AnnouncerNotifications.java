package com.samdobsondev.lcde4j.model.data.announcer;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class AnnouncerNotifications {
    @SerializedName("Events")
    private List<AnnouncerNotification> announcerNotifications;
}
