package nezd53.swagmod;


import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.io.File;
import java.io.IOException;


public class SwagEventHandler {
    final String OVERWORLD_NOON_COLOR = "82caff", OVERWORLD_SUNSET_COLOR = "368bc1", OVERWORLD_DUSK_COLOR = "2b3856", OVERWORLD_NIGHT_COLOR = "0c090a";
    final String NETHER_COLOR = "632222", END_COLOR = "c2bb89", MOON_COLOR = "838c88", MARS_COLOR = "6c3a24", ASTEROIDS_COLOR = "3e3e3e", VENUS_COLOR = "a8862f";
    final String DAMAGE_COLOR = "ff1313", DEATH_COLOR = "c01313";
    String currentColor = "000000";
    int currentHotbar = -1, currentHealth = -1, currentDimension = -999;

    @SubscribeEvent
    public void onOverlayChange(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
            EntityPlayer player = Minecraft.getMinecraft().player;
            if (InventoryPlayer.isHotbar(player.inventory.currentItem) && player.inventory.currentItem + 1 != currentHotbar)
                notifyHotbarChange(player.inventory.currentItem);
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH && Minecraft.getMinecraft().player.getHealth() != currentHealth)
            notifyHealthChange();
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer)
            notifyDimensionChange((EntityPlayer) event.getEntity());
    }

    @SubscribeEvent
    public void onLogOut(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        currentColor = "000000";
        currentHotbar = -1;
        updateColor();
    }

    private void notifyHotbarChange(int currentItem) {
        currentHotbar = currentItem + 1;
        updateColor();
    }

    private void notifyHealthChange() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player.getHealth() < currentHealth) {
            final String oldColor = currentColor;
            currentColor = DAMAGE_COLOR;
            new Thread(() -> {
                updateColor();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    SwagMod.logger.error(e.getMessage());
                }
                if (player.isEntityAlive())
                    currentColor = oldColor;
                else
                    currentColor = DEATH_COLOR;

                updateColor();
            }).start();
        } else if (!player.isEntityAlive()) {
            currentColor = DEATH_COLOR;
            updateColor();
        }

        currentHealth = (int) player.getHealth();

    }

    private void notifyDimensionChange(EntityPlayer player) {
        switch (player.dimension) {
            case 0:
                inOverworld(player);
                break;
            case -1:
                currentColor = NETHER_COLOR;
                break;
            case 1:
                currentColor = END_COLOR;
                break;
            case -28:
                currentColor = MOON_COLOR;
                break;
            case -29:
                currentColor = MARS_COLOR;
                break;
            case -30:
                currentColor = ASTEROIDS_COLOR;
                break;
            case -31:
                currentColor = VENUS_COLOR;
                break;
            default:
                SwagMod.logger.warn("Unknown dimension " + player.dimension);
        }
        updateColor();
        currentDimension = player.dimension;
    }

    private void inOverworld(EntityPlayer player) {
        new Thread(() -> {
            while (player.dimension == DimensionType.OVERWORLD.getId() && player.isEntityAlive()) {
                long time = player.getEntityWorld().getWorldTime();

                if (time < 10500)
                    currentColor = OVERWORLD_NOON_COLOR;
                else if (time < 12040 || time > 23961)
                    currentColor = OVERWORLD_SUNSET_COLOR;
                else if (time < 13000 || time > 23000)
                    currentColor = OVERWORLD_DUSK_COLOR;
                else currentColor = OVERWORLD_NIGHT_COLOR;

                updateColor();

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    SwagMod.logger.error(e.getMessage());
                }
            }
        }).start();
    }

    private void updateColor() {
        writeToFile("rgb " + currentColor + "ff");
        writeToFile("rgb " + currentHotbar + ":ffffffff");
    }

    static void writeToFile(String str) {

        String[] command = new String[]{"/home/max/Documents/topipe.sh", str};
        //SwagMod.logger.info(Arrays.toString(command));

        try {
            Runtime.getRuntime().exec(command, null, new File("/"));
        } catch (IOException e) {
            SwagMod.logger.error(e.getMessage());
        }

    }

}

