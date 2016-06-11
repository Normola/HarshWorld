package org.undergroundbunker.harshworld.library.modifiers;

import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class CustomFontColour {

    protected static int MARKER = 0xE700;

    private CustomFontColour() { }

    public static String encodeColour(int colour) {
        int r = ((colour >> 16) & 255);
        int g = ((colour >>  8) & 255);
        int b = ((colour >>  0) & 255);

        return encodeColour(r, g, b);
    }

    public static String encodeColour(float r, float g, float b) {
        return encodeColour((int)r*255, (int)g*255, (int)b*255);
    }

    public static String encodeColour(int r, int g, int b) {
        char red = ((char)(MARKER + (r&0xFF)));
        char grn = ((char)(MARKER + (g&0xFF)));
        char blu = ((char)(MARKER + (b&0xFF)));

        return String.format("%c%c%c", red, grn, blu);
    }

    public static String valueToColourCode(float val) {
        val /= 3f;
        val = MathHelper.clamp_float(val, 0.01f, 0.5f);
        int colour = Color.HSBtoRGB(val, 0.65f, 0.8f);

        return encodeColour(colour);
    }
}
