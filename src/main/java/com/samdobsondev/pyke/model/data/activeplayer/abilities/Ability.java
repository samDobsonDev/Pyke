package com.samdobsondev.pyke.model.data.activeplayer.abilities;

import lombok.Data;

@Data
public class Ability {
    private Long abilityLevel;
    private String displayName;
    private String id;
    private String rawDescription;
    private String rawDisplayName;
}
