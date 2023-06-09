package gsbs.common.util;

import org.lwjgl.nanovg.NVGColor;

public class Color {
    private static final NVGColor nvgColor = NVGColor.create();
    private float red;
    private float green;
    private float blue;
    private float alpha;

    public Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public static NVGColor rgba(float red, float green, float blue, float alpha) {
        nvgColor.r(red);
        nvgColor.g(green);
        nvgColor.b(blue);
        nvgColor.a(alpha);

        return nvgColor;
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
    }

    public float getGreen() {
        return green;
    }

    public void setGreen(float green) {
        this.green = green;
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
}
