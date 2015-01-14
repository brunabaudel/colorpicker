package br.ufpe.dm.colorpicker;

import android.content.Context;

import java.text.DecimalFormat;

/**
 * Created by brunasantana on 23/10/14.
 */
public class ConvertToCMYK {

    private static final int RANGE = 255;
    private int red;
    private int green;
    private int blue;

    public ConvertToCMYK () {
        this.red = 0;
        this.green = 0;
        this.blue = 0;
    }

    public double get_red() {
        return (double) this.red/RANGE;
    }

    public double get_green() {
        return (double) this.green/RANGE;
    }

    public double get_blue() {
        return (double) this.blue/RANGE;
    }

    public void set_red(int red) {
        this.red = red;
    }

    public void set_green(int green) {
        this.green = green;
    }

    public void set_blue(int blue) {
        this.blue = blue;
    }

    public String getKey() {
        double k = calcKey() * 100;
        return new DecimalFormat("#").format(k);
    }

    public String getCyan() {
        double c = (1 - get_red() - calcKey()) / (1 - calcKey()) * 100;
        return new DecimalFormat("#").format(c);
    }

    public String getMagenta() {
        double m = (1 - get_green() - calcKey()) / (1 - calcKey()) * 100;
        return new DecimalFormat("#").format(m);
    }

    public String getYellow() {
        double y = (1 - get_blue() - calcKey()) / (1 - calcKey()) * 100;
        return new DecimalFormat("#").format(y);
    }

    private double maxRGB(){
        return Math.max(Math.max(get_blue(), get_green()), get_red());
    }

    private double calcKey() {
        return 1 - maxRGB();
    }


}
