package com.samdobsondev.pyke.model.data.allplayers;

import com.samdobsondev.pyke.model.data.common.Rune;
import com.samdobsondev.pyke.model.data.common.RuneTree;
import lombok.Data;

@Data
public class Runes {
    private Rune keystone;
    private RuneTree primaryRuneTree;
    private RuneTree secondaryRuneTree;
}
