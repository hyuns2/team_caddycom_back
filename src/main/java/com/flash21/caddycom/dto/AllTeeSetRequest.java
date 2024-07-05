package com.flash21.caddycom.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AllTeeSetRequest {
    List<TeeUpdateRequest> teeInfos;
}