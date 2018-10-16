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
import net.minecraft.init.Blocks;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.common.model.TRSRTransformation;

public class ThanosPlayerRender extends Render<EntityPlayer> {
	
	private IModel thanosModelUnbaked;
	private IBakedModel thanosModel;
	private final VertexLighterFlat lighter;
	
	private void loadThanosModel() {
		try {
			thanosModelUnbaked = OBJLoader.INSTANCE.loadModel(new ResourceLocation(ThanosSkin.MODID, "models/thanos.obj"));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		thanosModel = thanosModelUnbaked.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
	}

	protected ThanosPlayerRender(RenderManager renderManager) {
		super(renderManager);
		loadThanosModel();
		lighter = new VertexLighterFlat(Minecraft.getMinecraft().getBlockColors());
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityPlayer entity) {
		return null;
	}
	
	@Override
	public void doRender(EntityPlayer entity, double x, double y, double z, float entityYaw, float partialTicks) {
        BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.height, entity.posZ);

        RenderHelper.disableStandardItemLighting();
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        GlStateManager.rotate(-entity.rotationYaw, 0, 1, 0);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        //builder.setTranslation(-0.5, -1.5, -0.5);

        lighter.setParent(new VertexBufferConsumer(builder));
        lighter.setWorld(entity.world);
        lighter.setState(Blocks.AIR.getDefaultState());
        lighter.setBlockPos(pos);
        boolean empty = true;
        List<BakedQuad> quads = thanosModel.getQuads(null, null, 0);
        if(!quads.isEmpty())
        {
            lighter.updateBlockInfo();
            empty = false;
            for(BakedQuad quad : quads)
            {
                quad.pipe(lighter);
            }
        }
        for(EnumFacing side : EnumFacing.values())
        {
            quads = thanosModel.getQuads(null, side, 0);
            if(!quads.isEmpty())
            {
                if(empty) lighter.updateBlockInfo();
                empty = false;
                for(BakedQuad quad : quads)
                {
                    quad.pipe(lighter);
                }
            }
        }

        // debug quad
        /*VertexBuffer.pos(0, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 0).lightmap(240, 0).endVertex();
        VertexBuffer.pos(0, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 1).lightmap(240, 0).endVertex();
        VertexBuffer.pos(1, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 1).lightmap(240, 0).endVertex();
        VertexBuffer.pos(1, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 0).lightmap(240, 0).endVertex();*/

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
