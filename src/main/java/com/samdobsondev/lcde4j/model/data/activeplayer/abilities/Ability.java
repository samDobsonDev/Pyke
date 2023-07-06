package com.samdobsondev.lcde4j.model.data.activeplayer.abilities;

import lombok.Data;

@Data
public class Ability {
    private Long abilityLevel;
    private String displayName;
    private String id;
    private String rawDescription;
    private String rawDisplayName;
}
