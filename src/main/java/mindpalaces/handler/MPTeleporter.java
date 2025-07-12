package mindpalaces.handler;

import mindpalaces.MindPalaces;
import mindpalaces.content.PotionSleepParalysis;
import mindpalaces.mindpalace.MindPalace;
import mindpalaces.mindpalace.MindPalaceData;
import mindpalaces.mixin.vanilla.EntityPlayerAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Arrays;

public class MPTeleporter implements ITeleporter {
    public static final MPTeleporter INSTANCE = new MPTeleporter();

    private int currentDim = 0;
    public MPTeleporter setFrom(int dim){
        this.currentDim = dim;
        return this;
    }

    @Override
    public void placeEntity(World world, Entity entity, float yaw) {
        if(!(entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) entity;

        if(this.currentDim != MindPalaces.DIMENSION_ID) {
            //TO MP
            MindPalaceData data = MindPalaceData.get();
            MindPalace mindPalace = data.prepareToTravel(player, this.currentDim);
            Vec3d mpPos = mindPalace.getSpawnPos();
            player.setLocationAndAngles(mpPos.x, mpPos.y, mpPos.z, yaw, player.rotationPitch);
        } else {
            //FROM MP
            MindPalace mindPalace = MindPalaceData.get().getForPlayer(player);
            BlockPos origPos = mindPalace.getOriginalPosition();
            //TODO: check if theres a good spot in origin dimension as well, might have been griefed
            player.setLocationAndAngles(origPos.getX() + 0.7, origPos.getY(), origPos.getZ() + 0.7, yaw, player.rotationPitch);
            player.removeActivePotionEffect(PotionSleepParalysis.INSTANCE);
        }
    }

    @Mod.EventBusSubscriber
    public static class TeleportHandler {
        public static Item travelItem = null;

        @SubscribeEvent
        public static void teleportOnPlayerSleep(SleepingTimeCheckEvent event) {
            //event is serverside only
            EntityPlayer player = event.getEntityPlayer();
            World world = player.getEntityWorld();

            if (((EntityPlayerAccessor) player).getSleepTimer() < ConfigHandler.minSleepTime) return;

            MindPalace mp = MindPalaceData.get().getForPlayer(player);

            //Teleport back
            if(world.provider.getDimension() == MindPalaces.DIMENSION_ID)
                player.changeDimension(mp.getOriginalDimension(), MPTeleporter.INSTANCE.setFrom(MindPalaces.DIMENSION_ID));
            //Teleport to MP
            else {
                //Only allow each x ticks
                if(MindPalaces.getOverworld().getWorldTime() < mp.getLastTravelTick() + ConfigHandler.travelDelay) return;
                //Blacklisted Dimensions
                if(Arrays.stream(ConfigHandler.blacklistedDimensions).anyMatch(dimId -> dimId == player.dimension)) return;

                //Has Item in hand
                if(travelItem == null){
                    travelItem = Item.getByNameOrId(ConfigHandler.heldItem);
                    if(travelItem == null) travelItem = Items.CLOCK;
                }
                if(!player.getHeldItemMainhand().getItem().equals(travelItem) && !player.getHeldItemOffhand().getItem().equals(travelItem)) return;

                //Teleport to Mind Palace
                player.changeDimension(MindPalaces.DIMENSION_ID, MPTeleporter.INSTANCE.setFrom(player.dimension));
            }

            event.setResult(Event.Result.DENY);
        }

        @SubscribeEvent
        public static void relocateOrKickPlayer(TickEvent.PlayerTickEvent event){
            EntityPlayer player = event.player;
            if(player.dimension != MindPalaces.DIMENSION_ID) return;
            if(player.world.isRemote) return;
            if(player.world.getTotalWorldTime() % 10 != 7) return;
            if(player.isCreative() || player.isSpectator()) return;

            MindPalace mp = MindPalaceData.get().getForPlayer(player);

            //Relocate if outside of MP
            if(!mp.positionIsInMindPalace(player.getPosition())) {
                Vec3d spawn = mp.getSpawnPos();
                player.setPositionAndUpdate(spawn.x, spawn.y, spawn.z);
                player.fallDistance = 0;
            }
            //Kick if time's up
            else if(MindPalaces.getOverworld().getWorldTime() > mp.getLastTravelTick() + ConfigHandler.maxStayTicks)
                player.changeDimension(mp.getOriginalDimension(), MPTeleporter.INSTANCE.setFrom(MindPalaces.DIMENSION_ID));
        }
    }
}

