package com.github.thelampgod.snow;

public class DrawUtil {
    public static double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }
}
