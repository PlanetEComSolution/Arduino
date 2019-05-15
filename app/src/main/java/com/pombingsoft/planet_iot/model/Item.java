package com.pombingsoft.planet_iot.model;

/**
 * Created by Admin on 2/9/2018.
 */

public class Item {
    private String Title;
    private Integer Icon;

    public Item(String Title,
                  Integer Icon

           ) {
        this.Title = Title;
        this.Icon = Icon;

    }

 public String getTitle() {
        return Title;
    }
    public Integer getIcon() {
        return Icon;
    }


}

