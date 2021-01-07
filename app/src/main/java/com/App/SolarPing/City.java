package com.App.SolarPing;

public class City{

    public String name;
    public String province;
    public double grade;
    public City(String province,String name,String sum)
    {
        this.province = province;
        this.name = name;
        String[] strArr = sum.split(" ");
        for(String s:strArr){
            System.out.println("strArr = "+s);
        }
        double sunshineduration = Double.parseDouble(strArr[2]);
        double stationNum =  Double.parseDouble(strArr[3]);
        double hazard =  Double.parseDouble(strArr[4]);
        double architecture = Double.parseDouble(strArr[5]);
        grade = (((((Math.log10(stationNum))+1)*(10.0 - sunshineduration))/100)*(1 + Math.log10(1+(Math.pow(hazard,2))))*Math.log10(architecture));
    }

}

