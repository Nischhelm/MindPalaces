package mindpalaces.mixin.waystones;

import com.llamalad7.mixinextras.sugar.Local;
import mindpalaces.MindPalaces;
import mindpalaces.content.PotionSleepParalysis;
import mindpalaces.mindpalace.MindPalaceData;
import net.blay09.mods.waystones.WaystoneManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WaystoneManager.class)
public abstract class WaystoneManagerMixin {
    @Inject(
            method = "transferPlayerToDimension",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldServer;removeEntityDangerously(Lnet/minecraft/entity/Entity;)V")
    )
    private static void mindpalaces_waystoneManager_teleportToPosition(EntityPlayerMP player, int dimension, PlayerList manager, CallbackInfo ci, @Local(name = "oldDim") int oldDim){
        if(oldDim == MindPalaces.DIMENSION_ID)
            player.removeActivePotionEffect(PotionSleepParalysis.INSTANCE);
        else if(dimension == MindPalaces.DIMENSION_ID)
            MindPalaceData.get().prepareToTravel(player, oldDim);
    }
}
