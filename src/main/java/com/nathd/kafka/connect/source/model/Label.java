package com.nathd.kafka.connect.source.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Label {
    private Integer id;
    private String url;
    private String name;
    private String color;
    private Boolean _default;
    private String description;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
}
