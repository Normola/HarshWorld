package org.undergroundbunker.harshworld.library.modifiers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import org.apache.logging.log4j.Logger;
import org.undergroundbunker.harshworld.library.Util;

public class ModifierNBT {
    private final static Logger log = Util.getLogger("ModifierNBT");

    public String identifier;
    public int colour;
    public int level;
    public String extraInfo;

    public ModifierNBT() {
        identifier = "";
        colour = 0xffffff;
        level = 0;
    }

    public ModifierNBT(IModifier modifier) {
        identifier = modifier.getIdentifier();
        level = 0;
        colour = Util.enumChatFormattingToColour(TextFormatting.GRAY);
    }

    public ModifierNBT(NBTTagCompound tag) {
        this();
        read(tag);
    }

    public static ModifierNBT readTag(NBTTagCompound tag) {
        ModifierNBT data = new ModifierNBT();
        if (tag != null) {
            data.read(tag);
        }

        return data;
    }

    public void read(NBTTagCompound tag) {
        identifier = tag.getString("identifier");
        colour = tag.getInteger("colour");
        level = tag.getInteger("level");
        extraInfo = tag.getString("extraInfo");
    }

    public void write(NBTTagCompound tag) {
        tag.setString("identifier", identifier);
        tag.setInteger("colour", colour);
        if (level > 0) {
            tag.setInteger("level", level);
        }
        else {
            tag.removeTag("level");
        }
        if (extraInfo != null && !extraInfo.isEmpty()) {
            tag.setString("extraInfo", extraInfo);
        }
    }

    public String getColourString() {
        return CustomFontColour.encodeColour(colour);
    }

    public static <T extends ModifierNBT> T readTag(NBTTagCompound tag, Class<T> aClass) {
        try {
            T data = aClass.newInstance();
            data.read(tag);
            return data;
        }
        catch(ReflectiveOperationException e) {
            log.error(e);
        }
        return null;
    }

    public static IntegerNBT readInteger(NBTTagCompound tag) {
        return ModifierNBT.readTag(tag, IntegerNBT.class);
    }

    public static BooleanNBT readBoolean(NBTTagCompound tag) {
        return ModifierNBT.readTag(tag, BooleanNBT.class);
    }

    public static class BooleanNBT extends ModifierNBT {
        public boolean status;

        public BooleanNBT() {
        }

        public BooleanNBT(IModifier modifier, boolean status) {
            super(modifier);
            this.status = status;
        }

        @Override
        public void write(NBTTagCompound tag) {
            super.write(tag);
            tag.setBoolean("status", status);
        }

        @Override
        public void read(NBTTagCompound tag) {
            super.read(tag);
            status = tag.getBoolean("status");
        }
    }

    public static class IntegerNBT extends ModifierNBT {

        public int current;
        public int max;

        public IntegerNBT() {

        }

        public IntegerNBT(IModifier modifier, int current, int max) {
            super(modifier);
            this.current = current;
            this.max = max;

            this.extraInfo = calcInfo();
        }

        @Override
        public void write(NBTTagCompound tag) {
            calcInfo();
            super.write(tag);
            tag.setInteger("current", current);
            tag.setInteger("max", max);
        }

        @Override
        public void read(NBTTagCompound tag) {
            super.read(tag);
            current = tag.getInteger("current");
            max = tag.getInteger("max");

            extraInfo = calcInfo();
        }

        public String calcInfo() {
            if (max > 0) {
                return String.format("%d / %d", current, max);
            }

            return current > 0 ? String.valueOf(current) : "";
        }
    }
}