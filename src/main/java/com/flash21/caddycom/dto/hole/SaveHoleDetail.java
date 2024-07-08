package com.flash21.caddycom.dto.hole;

import com.flash21.caddycom.dto.tee.TeeData;
import com.flash21.caddycom.dto.tipInfo.TipInfoData;
import lombok.Getter;

import java.util.List;

@Getter
public class SaveHoleDetail {
    private Long holeId;
    private int par;
    private int handicap;
    private List<TeeData> teeData;
    private List<TipInfoData> tipInfoData;
    private List<Long> deleteTeeIds;
    private List<Long> deleteTipInfoIds;
}
