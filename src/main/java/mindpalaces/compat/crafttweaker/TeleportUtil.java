package mindpalaces.compat.crafttweaker;

import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.player.IPlayer;
import mindpalaces.MindPalaces;
import mindpalaces.handler.MPTeleporter;
import mindpalaces.mindpalace.MindPalaceData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenRegister
@ZenClass("mods."+ MindPalaces.MODID+".TeleportUtil")
@SuppressWarnings("unused")
public class TeleportUtil {
    @ZenMethod
    @ZenDoc("Teleports an IPlayer to mind palace (true) or from mind palace to original dimension (false). Only works if not already in that dimension and on server side.")
    public static void teleportPlayer(IPlayer iplayer, boolean toMp){
        EntityPlayer player = (EntityPlayer) iplayer.getInternal();
        World world = player.world;
        if(world.isRemote) return;
        if(toMp == (world.provider.getDimension() == MindPalaces.DIMENSION_ID)) return;

        //TP after wakeup
        int destDim = toMp ? MindPalaces.DIMENSION_ID : MindPalaceData.get().getForPlayer(player).getOriginalDimension();
        MPTeleporter.TeleportHandler.teleportToDimension(player, destDim);
    }
}
