package org.undergroundbunker.harshworld.library.events;

import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import org.undergroundbunker.harshworld.library.materials.Material;
import net.minecraftforge.fml.common.eventhandler.Event;


public class HWEvent extends Event {

    public static class OnItemBuilding extends HWEvent {
        public NBTTagCompound tag;
        public final ImmutableList<Material> materials;

        public OnItemBuilding(NBTTagCompound tag, ImmutableList<Material> materials) {

            this.tag = tag;
            this.materials = materials;
        }

        public static OnItemBuilding fireEvent(NBTTagCompound tag, ImmutableList<Material> materials) {

            OnItemBuilding event = new OnItemBuilding(tag, materials);
            MinecraftForge.EVENT_BUS.post(event);
            return event;
        }
    }
}

