package com.samdobsondev.lcde4j.model.data.allplayers;

import lombok.Data;

@Data
public class Item
{
    private Boolean canUse;
    private Boolean consumable;
    private Long count;
    private String displayName;
    private Long itemID;
    private Long price;
    private String rawDescription;
    private String rawDisplayName;
    private Long slot;
}
