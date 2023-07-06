package com.samdobsondev.lcde4j.model.data.allplayers;

import com.samdobsondev.lcde4j.model.data.common.Rune;
import com.samdobsondev.lcde4j.model.data.common.RuneTree;
import lombok.Data;

@Data
public class Runes {
    private Rune keystone;
    private RuneTree primaryRuneTree;
    private RuneTree secondaryRuneTree;
}
