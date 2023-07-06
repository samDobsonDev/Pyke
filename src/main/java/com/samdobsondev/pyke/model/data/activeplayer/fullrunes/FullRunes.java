package com.samdobsondev.pyke.model.data.activeplayer.fullrunes;

import com.samdobsondev.pyke.model.data.common.Rune;
import com.samdobsondev.pyke.model.data.common.RuneTree;
import lombok.Data;

import java.util.List;

@Data
public class FullRunes {
    private List<Rune> generalRunes;
    private Rune keystone;
    private RuneTree primaryRuneTree;
    private RuneTree secondaryRuneTree;
    private List<StatRune> statRunes;
}
