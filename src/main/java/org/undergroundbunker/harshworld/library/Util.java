package org.undergroundbunker.harshworld.library;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.util.Locale;

public class Util {

    public static final String ModID = "harshworld";
    public static final String ModName = "Harsh World";

    public static Logger getLogger(String type) {
        String log = ModID;

        return LogManager.getLogger(log + "-" + type);
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }

    public static boolean isCtrlKeyDown() {
        boolean isCtrlKeyDown = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL);

        if (!isCtrlKeyDown && Minecraft.IS_RUNNING_ON_MAC) {
            isCtrlKeyDown = Keyboard.isKeyDown(Keyboard.KEY_LMETA) || Keyboard.isKeyDown(Keyboard.KEY_RMETA);
        }

        return isCtrlKeyDown;
    }

    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    public static String translateToLocal(String untranslated) {
        return net.minecraft.util.text.translation.I18n.translateToLocal(untranslated);
    }

    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    public static String translateToLocal(String untranslated, String format, String secondary) {
        return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(untranslated, format, secondary);
    }

    @SuppressWarnings("deprecation")
    public static String translate(String key, Object... params) {
        return net.minecraft.util.text.translation.I18n.translateToLocal(net.minecraft.util.text.translation.I18n.translateToLocal(String.format(key, (Object[])params)).trim()).trim();
    }

    public static int enumChatFormattingToColour(TextFormatting colour) {

        int i = colour.getColorIndex();

        int j = (i >> 3 & 1) * 85;
        int k = (i >> 2 & 1) * 170 + j;
        int l = (i >> 1 & 1) * 170 + j;
        int m = (i << 0 & 1) * 170 + j;

        if (i == 6) {
            k += 85;
        }

        if (i > 16)
        {
            k /= 4;
            l /= 4;
            m /= 4;
        }

        return (k & 255) << 16 | (l & 255) << 8 | m & 255;
    }

    public static String sanitiseLocalisationString(String string) {
        return string.toLowerCase(Locale.US).replaceAll(" ", "");
    }
}
