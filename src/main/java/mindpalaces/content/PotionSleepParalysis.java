package mindpalaces.content;

import mindpalaces.MindPalaces;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class PotionSleepParalysis extends Potion {
    public static PotionSleepParalysis INSTANCE = new PotionSleepParalysis();

    private final ResourceLocation texture;

    protected PotionSleepParalysis() {
        super(true, 0xFFFFFF);
        this.texture = new ResourceLocation(MindPalaces.MODID, "textures/effects/sleepparalysis.png");
        this.setRegistryName(MindPalaces.MODID, "sleepparalysis");
        this.setPotionName("effects.mindpalaces.sleepparalysis");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    public ResourceLocation getTexture() { return this.texture; }

    @Override
    public boolean hasStatusIcon() { return true; }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(@Nonnull PotionEffect effect, @Nonnull Gui gui, int x, int y, float z) {
        renderInventoryEffect(x, y, effect, Minecraft.getMinecraft());
    }

    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int x, int y, @Nonnull PotionEffect effect, @Nonnull Minecraft mc) {
        if(getTexture()!=null) {
            Minecraft.getMinecraft().getTextureManager().bindTexture(getTexture());
            Gui.drawModalRectWithCustomSizedTexture(x+6, y+7, 0, 0, 18, 18, 18, 18);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUDEffect(@Nonnull PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
        renderHUDEffect(x, y, effect, Minecraft.getMinecraft(), alpha);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void renderHUDEffect(int x, int y, @Nonnull PotionEffect effect, @Nonnull Minecraft mc, float alpha) {
        if(getTexture()!=null) {
            mc.getTextureManager().bindTexture(getTexture());
            Gui.drawModalRectWithCustomSizedTexture(x+3, y+3, 0, 0, 18, 18, 18, 18);
        }
    }

    @Mod.EventBusSubscriber
    public static class EventHandler {
        @SubscribeEvent
        public static void registerPotionEvent(RegistryEvent.Register<Potion> event) {
            event.getRegistry().register(PotionSleepParalysis.INSTANCE);
        }
    }
}
