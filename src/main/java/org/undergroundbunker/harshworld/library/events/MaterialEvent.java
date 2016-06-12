package org.undergroundbunker.harshworld.library.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import org.apache.logging.log4j.Logger;
import org.undergroundbunker.harshworld.library.Util;
import org.undergroundbunker.harshworld.library.materials.IMaterialStats;
import org.undergroundbunker.harshworld.library.materials.Material;

public class MaterialEvent extends HWEvent {

    public final Logger log = Util.getLogger("MaterialEvent");
    public final Material material;

    public MaterialEvent(Material material) {
        this.material = material;
    }

    @Cancelable
    public static class MaterialRegisterEvent extends MaterialEvent {

        public MaterialRegisterEvent(Material material) {
            super(material);
        }
    }

    @HasResult
    public static class StatRegisterEvent<T extends IMaterialStats> extends MaterialEvent {

        public final T stats;
        public T newStats;

        public StatRegisterEvent(Material material, T stats) {
            super(material);
            this.stats = stats;
        }

        public void overrideResult(T newStats) {
            if (!stats.getIdentifier().equals(newStats.getIdentifier())) {
                log.error("StatRegisterEvent: New stats don't match old stats type. New is {}, old was {}",
                        newStats.getIdentifier(), stats.getIdentifier());
                return;
            }

            this.newStats = newStats;
            this.setResult(Result.ALLOW);
        }
    }
}
