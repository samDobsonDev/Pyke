package com.samdobsondev.pyke.model.data.activeplayer.abilities;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Abilities {
    @SerializedName("E")
    private Ability e;
    @SerializedName("Passive")
    private Passive passive;
    @SerializedName("Q")
    private Ability q;
    @SerializedName("R")
    private Ability r;
    @SerializedName("W")
    private Ability w;
}
