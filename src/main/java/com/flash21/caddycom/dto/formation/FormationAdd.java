package com.flash21.caddycom.dto.formation;

import lombok.Getter;

import java.util.List;

@Getter
public class FormationAdd {
    private String name;
    private List<CourseInfo> courseInfos;
}
