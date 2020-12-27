package nezd53.swagmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = SwagMod.MODID, name = SwagMod.NAME, version = SwagMod.VERSION)
public class SwagMod {
    public static final String MODID = "swagmod";
    public static final String NAME = "Swag Mod!";
    public static final String VERSION = "1.0";

    protected static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        logger.info("Loaded " + NAME + " v" + VERSION);
        if (event.getSide().isClient())
            MinecraftForge.EVENT_BUS.register(new SwagEventHandler());

    }

}
