package com.example.dto;

public class AudioProcessingResult {
    private double[] F;
    private double[] phaseSpectrum1;
    private double[] phaseSpectrum2;
    private double[] phaseDifference;
    private double[][] filteredSegments;

    public AudioProcessingResult(double[] F, double[] phaseSpectrum1, double[] phaseSpectrum2, double[] phaseDifference, double[][] filteredSegments) {
        this.F = F;
        this.phaseSpectrum1 = phaseSpectrum1;
        this.phaseSpectrum2 = phaseSpectrum2;
        this.phaseDifference = phaseDifference;
        this.filteredSegments = filteredSegments;
    }

    public double[] getF() { return F; }
    public double[] getPhaseSpectrum1() { return phaseSpectrum1; }
    public double[] getPhaseSpectrum2() { return phaseSpectrum2; }
    public double[] getPhaseDifference() { return phaseDifference; }
    public double[][] getFilteredSegments() { return filteredSegments; }
}
