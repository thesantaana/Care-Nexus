package com.carenexus.doctor.health;

import java.math.BigDecimal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "care-nexus.health-threshold")
public class HealthThresholdProperties {
    private int systolicLow = 90;
    private int systolicHigh = 140;
    private int diastolicLow = 60;
    private int diastolicHigh = 90;
    private BigDecimal glucoseLow = new BigDecimal("3.9");
    private BigDecimal glucoseHigh = new BigDecimal("7.0");
    private int heartRateLow = 60;
    private int heartRateHigh = 100;
    private BigDecimal temperatureLow = new BigDecimal("35.0");
    private BigDecimal temperatureHigh = new BigDecimal("37.3");

    public int getSystolicLow() { return systolicLow; }

    public void setSystolicLow(int value) { this.systolicLow = value; }

    public int getSystolicHigh() { return systolicHigh; }

    public void setSystolicHigh(int value) { this.systolicHigh = value; }

    public int getDiastolicLow() { return diastolicLow; }

    public void setDiastolicLow(int value) { this.diastolicLow = value; }

    public int getDiastolicHigh() { return diastolicHigh; }

    public void setDiastolicHigh(int value) { this.diastolicHigh = value; }

    public BigDecimal getGlucoseLow() { return glucoseLow; }

    public void setGlucoseLow(BigDecimal value) { this.glucoseLow = value; }

    public BigDecimal getGlucoseHigh() { return glucoseHigh; }

    public void setGlucoseHigh(BigDecimal value) { this.glucoseHigh = value; }

    public int getHeartRateLow() { return heartRateLow; }

    public void setHeartRateLow(int value) { this.heartRateLow = value; }

    public int getHeartRateHigh() { return heartRateHigh; }

    public void setHeartRateHigh(int value) { this.heartRateHigh = value; }

    public BigDecimal getTemperatureLow() { return temperatureLow; }

    public void setTemperatureLow(BigDecimal value) { this.temperatureLow = value; }

    public BigDecimal getTemperatureHigh() { return temperatureHigh; }

    public void setTemperatureHigh(BigDecimal value) { this.temperatureHigh = value; }
}
