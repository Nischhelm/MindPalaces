package mindpalaces.handler;

import fermiumbooter.annotations.MixinConfig;
import mindpalaces.MindPalaces;
import mindpalaces.mindpalace.MindPalace;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Config(modid = MindPalaces.MODID)
@MixinConfig(name = MindPalaces.MODID)
public class ConfigHandler {
    @Config.Comment("Each palace will be a cube of [size x size x size] air blocks surrounded by bedrock")
    @Config.Name("Palace Size")
    @Config.RangeInt(min = 2, max = 254)
    public static int size = 5;

    @Config.Comment("If set to true, will stop any Living Entity spawn inside Mind Palace")
    @Config.Name("Disallow Mobs")
    public static boolean noMobsAllowed = true;

    @Config.Comment("If set to true, will stop any Explosions inside Mind Palace")
    @Config.Name("Disallow Explosions")
    public static boolean noExplosionsAllowed = true;

    @Config.Comment("The Mind Palace dimension will try to repair all mind palace bedrock walls every this many ticks. Increase if there's performance issues.")
    @Config.Name("Wall Repair Speed")
    @Config.RangeInt(min = 1)
    public static int repairSpeed = 10;

    @Config.Comment("How many ticks the player has to be sleeping in a bed until they get teleported.")
    @Config.Name("Min Sleep Time")
    @Config.RangeInt(min = 0, max = 99)
    public static int minSleepTime = 90;

    @Config.Comment("How many ticks the player cannot travel to the Mind Palace again after having traveled there last (default: one whole night, 10 minutes).")
    @Config.Name("Travel Delay")
    @Config.RangeInt(min = 0)
    public static int travelDelay = 12000;

    @Config.Comment("How many ticks the player is allowed to stay inside the Mind Palace. This is meant mainly as a safety switch to prevent softlocks.")
    @Config.Name("Max Stay Ticks")
    @Config.RangeInt(min = 200)
    public static int maxStayTicks = 12000;

    @Config.Comment("Add Dimension ids from which players are not allowed to travel to their mind palace.")
    @Config.Name("Dimension Blacklist")
    public static int[] blacklistedDimensions = {};

    @Config.Comment("Players will only teleport to their Mind Palace when going to sleep if they hold this item in main- or offhand. To disable teleporting via sleep entirely, clear this value.")
    @Config.Name("Held Item")
    public static String heldItem = "minecraft:clock";

    @Config.Comment("The mind palaces walls, floor and ceiling will be made from this block. Breaking this block will not be allowed in the mind palace dimension, so do not use a block a player could manually place and want to remove again.")
    @Config.Name("Wall Block")
    public static String wallBlock = "minecraft:bedrock";

    @Config.Comment("This will prevent Recurrent Complex from generating structures in the Mind Palace Dimension.")
    @Config.Name("Compat - Disable Recurrent Complex")
    @Config.RequiresMcRestart
    public static boolean disableReccomplex = true;

    @Config.Comment("Will save the original position of the player when traveling to the mind palace using waystones/warp scrolls, so players can return using their bed and will be kicked normally after 10 minutes.")
    @Config.Name("Compat - Waystones Travel")
    @MixinConfig.MixinToggle(lateMixin = "mixins.mindpalaces.waystones.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "waystones", desired = true, reason = "Waystones compat handling needs Waystones to work")
    @Config.RequiresMcRestart
    @SuppressWarnings("unused")
    public static boolean waystonesCompat = true;

    @Config.Comment("Traveling to/from MP will not increase evolution points if this is enabled")
    @Config.Name("Compat - SRP no sleep points")
    @MixinConfig.MixinToggle(lateMixin = "mixins.mindpalaces.srp.json", defaultValue = true)
    @MixinConfig.CompatHandling(modid = "srparasites", desired = true, reason = "SRP compat handling needs SRP to work")
    @Config.RequiresMcRestart
    @SuppressWarnings("unused")
    public static boolean srpCompat = true;

    @Mod.EventBusSubscriber(modid = MindPalaces.MODID)
    @SuppressWarnings("unused")
    private static class EventHandler {
        @SubscribeEvent
        @SideOnly(Side.CLIENT)
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if(event.getModID().equals(MindPalaces.MODID)) {
                ConfigManager.sync(MindPalaces.MODID, Config.Type.INSTANCE);
                MindPalace.wallBlock = null;
                MPTeleporter.TeleportHandler.travelItem = null;
            }
        }
    }
}
