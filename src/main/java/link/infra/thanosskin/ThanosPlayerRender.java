package link.infra.thanosskin;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.common.model.TRSRTransformation;

public class ThanosPlayerRender extends Render<EntityPlayer> {
	
	private IModel thanosModelUnbaked;
	private IBakedModel thanosModel;
	
	private void loadThanosModel() {
		try {
			thanosModelUnbaked = OBJLoader.INSTANCE.loadModel(new ResourceLocation(ThanosSkin.MODID, "models/thanos.obj"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		thanosModel = thanosModelUnbaked.bake(TRSRTransformation.identity(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
	}

	protected ThanosPlayerRender(RenderManager renderManager) {
		super(renderManager);
		loadThanosModel();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPlayer entity) {
		return null;
	}
	
	@Override
	public void doRender(EntityPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.rotate(-entity.rotationYaw, 0, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.ITEM);
        
        List<BakedQuad> quads = thanosModel.getQuads(null, null, 0);
        if(!quads.isEmpty())
        {
            for(BakedQuad quad : quads)
            {
            	LightUtil.renderQuadColor(builder, quad, -1);
            }
        }
        for(EnumFacing side : EnumFacing.values())
        {
            quads = thanosModel.getQuads(null, side, 0);
            if(!quads.isEmpty())
            {
                for(BakedQuad quad : quads)
                {
                    LightUtil.renderQuadColor(builder, quad, -1);
                }
            }
        }
        
        builder.setTranslation(0, 0, 0);

        tessellator.draw();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
        
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
	
	// Copypasted from RenderLivingBase
	
	@Override
	public void renderName(EntityPlayer entity, double x, double y, double z) {
		if (this.canRenderName(entity)) {
			double d0 = entity.getDistanceSq(this.renderManager.renderViewEntity);
			float f = entity.isSneaking() ? RenderLivingBase.NAME_TAG_RANGE_SNEAK : RenderLivingBase.NAME_TAG_RANGE;

			if (d0 < (double) (f * f)) {
				String s = entity.getDisplayName().getFormattedText();
				GlStateManager.alphaFunc(516, 0.1F);
				this.renderEntityName(entity, x, y, z, s, d0);
			}
		}
	}

	@Override
	protected boolean canRenderName(EntityPlayer entity) {
		EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
		boolean flag = !entity.isInvisibleToPlayer(entityplayersp);

		if (entity != entityplayersp) {
			Team team = entity.getTeam();
			Team team1 = entityplayersp.getTeam();

			if (team != null) {
				Team.EnumVisible team$enumvisible = team.getNameTagVisibility();

				switch (team$enumvisible) {
				case ALWAYS:
					return flag;
				case NEVER:
					return false;
				case HIDE_FOR_OTHER_TEAMS:
					return team1 == null ? flag
							: team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
				case HIDE_FOR_OWN_TEAM:
					return team1 == null ? flag : !team.isSameTeam(team1) && flag;
				default:
					return true;
				}
			}
		}

		return Minecraft.isGuiEnabled() && entity != this.renderManager.renderViewEntity && flag
				&& !entity.isBeingRidden();
	}
	
	// Override height above head
	@Override
    protected void renderLivingLabel(EntityPlayer entityIn, String str, double x, double y, double z, int maxDistance)
    {
        double d0 = entityIn.getDistanceSq(this.renderManager.renderViewEntity);

        if (d0 <= (double)(maxDistance * maxDistance))
        {
            boolean flag = entityIn.isSneaking();
            float f = this.renderManager.playerViewY;
            float f1 = this.renderManager.playerViewX;
            boolean flag1 = this.renderManager.options.thirdPersonView == 2;
            float f2 = entityIn.height + 0.5F - (flag ? 0.25F : 0.0F);
            int i = -20;
            EntityRenderer.drawNameplate(this.getFontRendererFromRenderManager(), str, (float)x, (float)y + f2, (float)z, i, f, f1, flag1, flag);
        }
    }
}
