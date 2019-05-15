package com.pombingsoft.planet_iot.model;

/**
 * Created by Admin on 2/9/2018.
 */

public class Switch {
    private String PinName;
    private String PinNum;
    private String Status;
    private String Value;
    private String Scheduler_1;
    private String Scheduler_2;
    private String Scheduler_3;
    private boolean hasDimmer;
    private String InsertDate;
    private String DimmerType;
    private String Current_DimmerProgress;
    public Switch(String PinName,
                  String PinNum,
           String Status,
           String Value,
           String Scheduler_1,
           String Scheduler_2,
           String Scheduler_3,
                  boolean hasDimmer,
                  String DimmerType,
                  String Current_DimmerProgress
           ) {
        this.PinName = PinName;
        this.PinNum = PinNum;
        this.Status = Status;
        this.Value = Value;
        this.Scheduler_1 = Scheduler_1;
        this.Scheduler_2 = Scheduler_2;
        this.Scheduler_3 = Scheduler_3;
        this.hasDimmer = hasDimmer;
        this.DimmerType = DimmerType;
        this.Current_DimmerProgress = Current_DimmerProgress;


    }

    public String getPinName() {
        return PinName;
    }
    public String getPinNum() {
        return PinNum;
    }
    public String getStatus() {
        return Status;
    }
    public String getValue() {
        return Value;
    }
    public String getScheduler_1() {
        return Scheduler_1;
    }
    public String getScheduler_2() {
        return Scheduler_2;
    }
    public String getScheduler_3() {
        return Scheduler_3;
    }
    public boolean gethasDimmer() {
        return hasDimmer;
    }
    public String getCurrent_DimmerProgress() {
        return Current_DimmerProgress;
    }
    public String getDimmerType() {
        return DimmerType;
    }


  /*  public void setPinName(String PinName) {
        this.PinName = PinName;
    }

    public String getPinName() {
        return statusPinNameD5;
    }
*/


}

