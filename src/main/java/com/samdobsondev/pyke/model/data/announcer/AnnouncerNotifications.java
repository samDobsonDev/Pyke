package com.samdobsondev.pyke.model.data.announcer;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

@Data
public class AnnouncerNotifications {
    @SerializedName("Events")
    private List<AnnouncerNotification> announcerNotifications;
}
