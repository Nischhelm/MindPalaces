package mindpalaces;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(modid = MindPalaces.MODID)
public class ConfigHandler {
    @Config.Comment("Each palace will be a cube of size x size x size air blocks surrounded by bedrock")
    @Config.Name("Palace Size")
    public static int size = 5;

    @Config.Comment("If set to true, will stop any Living Entity spawn inside Mind Palace")
    @Config.Name("Disallow Mobs")
    public static boolean noMobsAllowed = true;

    @Config.Comment("The Mind Palace dimension will try to repair all mind palace bedrock walls every this many ticks. Increase if theres performance issues.")
    @Config.Name("Bedrock Repair Period")
    public static int repairSpeed = 10;

    @Config.Comment("How many ticks the player has to be sleeping in a bed until they get teleported.")
    @Config.Name("Min Sleep Time")
    @Config.RangeInt(max = 99)
    public static int minSleepTime = 99;

    @Config.Comment("How many ticks the player cannot travel to the Mind Palace again after having traveled there last (default: one whole night, 10 minutes).")
    @Config.Name("Travel Delay")
    @Config.RangeInt(min = 0)
    public static int travelDelay = 12000;

    @Config.Comment("How many ticks the player is allowed to stay inside the Mind Palace. This is meant mainly as a safety switch to prevent softlocks.")
    @Config.Name("Max Stay Ticks")
    @Config.RangeInt(min = 1200)
    public static int maxStayTicks = 12000;

    @Config.Comment("Add Dimension ids from which players are not allowed to travel to their mind palace.")
    @Config.Name("Dimension Blacklist")
    public static int[] blacklistedDimensions = {};

    @Config.Comment("Players will only teleport to their Mind Palace when going to sleep if they hold this item in main- or offhand.")
    @Config.Name("Held Item")
    public static String heldItem = "minecraft:clock";

    @Config.Comment("This will prevent Recurrent Complex from generating structures in the Mind Palace Dimension.")
    @Config.Name("Compat - Disable Recurrent Complex")
    @Config.RequiresMcRestart
    public static boolean disableReccomplex = true;

    @Mod.EventBusSubscriber(modid = MindPalaces.MODID)
    @SuppressWarnings("unused")
    private static class EventHandler {
        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(MindPalaces.MODID))
                ConfigManager.sync(MindPalaces.MODID, Config.Type.INSTANCE);
        }
    }
}
