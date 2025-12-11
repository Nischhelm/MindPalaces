package mindpalaces.mindpalace;

import mindpalaces.MindPalaces;
import mindpalaces.content.PotionSleepParalysis;
import mindpalaces.handler.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MindPalaceData extends WorldSavedData {
    private static final String KEY = "mindpalaces";
    private static MindPalaceData clientCache = null;

    private final Map<UUID, MindPalace> mindPalaces = new HashMap<>();
    public static boolean isLoading = false;

    public MindPalaceData(String dataName) {
        super(dataName);
    }

    public static MindPalaceData get(){
        World world = MindPalaces.getOverworld();

        if(world.isRemote){ //should never happen but protection against other mods
            if(clientCache == null) clientCache = new MindPalaceData(KEY);
            return clientCache;
        }

        MindPalaceData instance = (MindPalaceData) world.loadData(MindPalaceData.class, KEY);

        if (instance == null) {
            instance = new MindPalaceData(KEY);
            world.setData(KEY, instance);
        }

        return instance;
    }

    public void setMindPalace(EntityPlayer player, MindPalace mindPalace){
        mindPalaces.put(player.getUniqueID(), mindPalace);
        this.markDirty();
    }

    public MindPalace createMindPalace(){
        int xPos = (ConfigHandler.size + 1) * (ConfigHandler.size + 1);
        int zPos = (ConfigHandler.size + 4) * mindPalaces.size();
        return new MindPalace(new BlockPos(xPos, 1, zPos), ConfigHandler.size);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        isLoading = true;
        if(!nbt.hasKey("list")) return;
        try {
            nbt.getTagList("list", 10).forEach(tags ->
                    mindPalaces.put(
                            UUID.fromString(((NBTTagCompound) tags).getString("uuid")),
                            MindPalace.createFromNBT(((NBTTagCompound) tags).getCompoundTag("mp"))
                    )
            );
        } catch (Exception e){
            MindPalaces.LOGGER.info("Crashed while trying to read Mind Palace data - some might be corrupted");
            e.printStackTrace(System.out);
        }
        isLoading = false;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        mindPalaces.forEach((uuid, mp) -> {
            NBTTagCompound tags = new NBTTagCompound();
            tags.setString("uuid", uuid.toString());
            tags.setTag("mp", mp.writeToNBT());
            list.appendTag(tags);
        });
        compound.setTag("list", list);
        return compound;
    }

    @Nonnull
    public MindPalace getForPlayer(EntityPlayer player){
        MindPalace mp = mindPalaces.get(player.getUniqueID());
        if(mp == null) {
            mp = this.createMindPalace();
            this.setMindPalace(player, mp);
        }
        return mp;
    }

    public Collection<MindPalace> getAll(){
        return mindPalaces.values();
    }

    public MindPalace prepareToTravel(EntityPlayer player, int originalDimension){
        if(player.world.isRemote) return null;

        MindPalace mp = getForPlayer(player);
        mp.generateMindPalace();
        mp.setOriginalPosition(originalDimension, player.getPosition());
        mp.resetTick(player.world.getWorldTime());
        player.addPotionEffect(new PotionEffect(PotionSleepParalysis.INSTANCE, ConfigHandler.maxStayTicks));

        return mp;
    }
}
