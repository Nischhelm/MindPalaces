package mindpalaces.compat;

import ivorius.reccomplex.events.StructureGenerationEvent;
import ivorius.reccomplex.events.StructureGenerationEventLite;
import mindpalaces.MindPalaces;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReccomplexCompat {
    @SubscribeEvent
    public static void disableReccomplexInMindPalace(StructureGenerationEventLite.Suggest event){
        if(event.getWorld().provider.getDimension() == MindPalaces.DIMENSION_ID) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void disableReccomplexInMindPalace(StructureGenerationEvent.Suggest event){
        if(event.getWorld().provider.getDimension() == MindPalaces.DIMENSION_ID) event.setCanceled(true);
    }
}
