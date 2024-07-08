package com.flash21.caddycom.dto.tee;

import lombok.Getter;

import java.util.List;

@Getter
public class AllTeeSetRequest {
    List<TeeUpdateRequest> teeInfos;
}