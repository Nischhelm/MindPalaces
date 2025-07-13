package mindpalaces.mindpalace;

import mindpalaces.MindPalaces;
import mindpalaces.handler.ConfigHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber
public class MindPalaceEventHandler {
    @SubscribeEvent
    public static void noMobs(EntityJoinWorldEvent event){
        if(!ConfigHandler.noMobsAllowed) return;
        if(event.getWorld().provider.getDimension() != MindPalaces.DIMENSION_ID) return;
        if(!(event.getEntity() instanceof EntityLivingBase)) return;
        if(event.getEntity() instanceof EntityPlayer) return;
        event.setResult(Event.Result.DENY);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void noExplosions(ExplosionEvent.Start event){
        if(!ConfigHandler.noExplosionsAllowed) return;
        if(event.getWorld().provider.getDimension() == MindPalaces.DIMENSION_ID)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void noWallBreaks(BlockEvent.BreakEvent event){
        World world = event.getWorld();
        if(world.provider.getDimension() != MindPalaces.DIMENSION_ID) return;

        if(event.getState().getBlock() == MindPalace.wallBlock)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public static void regenerateBedrock(TickEvent.WorldTickEvent event){
        World world = event.world;
        if(event.phase != TickEvent.Phase.END) return;
        if(world.provider.getDimension() != MindPalaces.DIMENSION_ID) return;
        if(world.isRemote) return;
        if(world.getTotalWorldTime() % ConfigHandler.repairSpeed != 0) return;

        MindPalaceData.get().getAll().forEach(MindPalace::generateMindPalace);
    }

    @SubscribeEvent
    public static void tickMindPalace(TickEvent.PlayerTickEvent event){
        if(event.phase != TickEvent.Phase.END) return;
        EntityPlayer player = event.player;
        World world = player.world;

        if(world.isRemote) return;

        if(world.getWorldTime() % 10 != 0) return; //enough to do it every 10 ticks

        MindPalace mp = MindPalaceData.get().getForPlayer(player);
        mp.incrementTick();
    }
}
