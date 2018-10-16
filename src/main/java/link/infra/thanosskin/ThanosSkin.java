package link.infra.thanosskin;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ThanosSkin.MODID, name = ThanosSkin.MODNAME, version = ThanosSkin.VERSION, useMetadata = true)
@Mod.EventBusSubscriber(modid = ThanosSkin.MODID)
public class ThanosSkin {

	public static final String MODID = "thanosskin";
	public static final String MODNAME = "thanos skin";
	public static final String VERSION = "1.12.2-1.0.0";

	@Mod.Instance
	public static ThanosSkin instance;
	public static Logger logger;
	private static ThanosPlayerRender renderer;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		OBJLoader.INSTANCE.addDomain(MODID);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		renderer = new ThanosPlayerRender(Minecraft.getMinecraft().getRenderManager());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e) {
	}
	
	@SubscribeEvent
	public static void renderPlayer(RenderPlayerEvent.Pre e) {
		e.setCanceled(true);
		EntityPlayer player = e.getEntityPlayer();
		renderer.doRender(player, e.getX(), e.getY(), e.getZ(), 0F, e.getPartialRenderTick());
	}
	
	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre event) {
		// For some reason I have to manually register sprites
		event.getMap().registerSprite(new ResourceLocation("thanosskin:blocks/thanos_texture_body_d"));
		event.getMap().registerSprite(new ResourceLocation("thanosskin:blocks/thanos_texture_face_d"));
	}
	
}
