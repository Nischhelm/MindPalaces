package mindpalaces;

import mindpalaces.mindpalace.MindPalace;
import mindpalaces.mindpalace.MindPalaceData;
import mindpalaces.world.MPTeleporter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.util.Arrays;

@Mod.EventBusSubscriber
public class ModEventHandler {
	private static Field sleepingTimer = null;
	private static int getSleepTimer(EntityPlayer player){
		if(sleepingTimer == null) {
			try {
				sleepingTimer = EntityPlayer.class.getDeclaredField("field_71076_b"); //sleepTimer
				sleepingTimer.setAccessible(true);
			} catch (Exception e){
				e.printStackTrace(System.out);
			}
		}
		try {
			return (int) sleepingTimer.get(player);
		}
		catch (Exception e){
			return 0;
		}
	}

	@SubscribeEvent
	public static void teleportOnPlayerSleep(SleepingTimeCheckEvent event) {
		//event is serverside only
		EntityPlayer player = event.getEntityPlayer();
		World world = player.getEntityWorld();

		if (getSleepTimer(player) < ConfigHandler.minSleepTime) return;

		ResourceLocation locMain = player.getHeldItemMainhand().getItem().getRegistryName();
		boolean itemInMainHand = locMain != null && locMain.toString().equals(ConfigHandler.heldItem);

		if(!itemInMainHand) {
			ResourceLocation locOff = player.getHeldItemOffhand().getItem().getRegistryName();
			boolean itemInOffhand = locOff != null && locOff.toString().equals(ConfigHandler.heldItem);

			if(!itemInOffhand) return;
		}

		MindPalace mp = MindPalaceData.get().getForPlayer(player);

		//Teleport back
		if(world.provider.getDimension() == MindPalaces.DIMENSION_ID){
			int dim = mp.getOriginalDimension();
			player.changeDimension(dim, MPTeleporter.INSTANCE.setFrom(MindPalaces.DIMENSION_ID));
		} else {
			//Only allow each x ticks
			if(MindPalaces.getWorld(0).getWorldTime() < mp.getLastTravelTick() + ConfigHandler.travelDelay) return;
			//Blacklisted Dimensions
			if(Arrays.stream(ConfigHandler.blacklistedDimensions).anyMatch(dimId -> dimId == player.dimension)) return;

			//Teleport to Mind Palace
			player.changeDimension(MindPalaces.DIMENSION_ID, MPTeleporter.INSTANCE.setFrom(player.dimension));
		}

		event.setResult(Event.Result.DENY);
	}

	@SubscribeEvent
	public static void dontAllowMobSpawns(EntityJoinWorldEvent event){
		if(!ConfigHandler.noMobsAllowed) return;
		if(event.getWorld().provider.getDimension() != MindPalaces.DIMENSION_ID) return;
		if(!(event.getEntity() instanceof EntityLivingBase)) return;
		if(event.getEntity() instanceof EntityPlayer) return;
		event.setResult(Event.Result.DENY);
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void relocatePlayer(TickEvent.PlayerTickEvent event){
		EntityPlayer player = event.player;
		if(player.dimension != MindPalaces.DIMENSION_ID) return;
		if(player.world.isRemote) return;
		if(player.world.getTotalWorldTime() % 10 != 7) return;
		if(player.isCreative() || player.isSpectator()) return;

		MindPalace mp = MindPalaceData.get().getForPlayer(player);
		if(mp == null) return;

		if(mp.positionIsInMindPalace(player.getPosition())) return;

		Vec3d spawn = mp.getSpawnPos();
		player.setPositionAndUpdate(spawn.x, spawn.y, spawn.z);
		player.fallDistance = 0;
	}

	@SubscribeEvent
	public static void regenerateBedrock(TickEvent.WorldTickEvent event){
		World world = event.world;
		if(world.provider.getDimension() != MindPalaces.DIMENSION_ID) return;
		if(world.isRemote) return;
		if(world.getTotalWorldTime() % ConfigHandler.repairSpeed != 0) return;

		MindPalaceData.get().getAll().forEach(MindPalace::generateMindPalace);
	}
}