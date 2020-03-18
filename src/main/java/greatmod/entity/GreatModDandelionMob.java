package greatmod.entity;

import greatmod.GreatModConfig;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SnowballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class GreatModDandelionMob extends MonsterEntity
{
	public static final double MAX_HEALTH = GreatModConfig.monsterHealth;
	public static final double BASE_ARMOR = GreatModConfig.monsterArmor;
	public static final double SPAWN_CHANCE = GreatModConfig.spawnChance;

	/// Ticks until the next attack phase change.
	public int attackDelay = 0;
	/// True if the texture index is increasing.
	private boolean textureInc = true;
	/// Counter to next texture update.
	private byte textureTicks = 0;
	/// The current texture index.
	private byte textureIndex = 0;

	public GreatModDandelionMob(EntityType<? extends GreatModDandelionMob> type, World world)
	{
		super(type, world);

		// this.setPathPriority(PathNodeType.WATER, -1.0F);
	}

	@Override
	protected void registerGoals()
	{
		this.goalSelector.addGoal(5, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		this.goalSelector.addGoal(6, new LookAtGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.addGoal(6, new LookRandomlyGoal(this));
		this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setCallsForHelp());
		this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
	}

	@Override
	protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn)
	{
		return 1.0F;
	}

	@Override
	protected void registerAttributes()
	{
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(MAX_HEALTH);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.24D);
	}

	@Override
	protected SoundEvent getAmbientSound()
	{
		return null;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSourceIn)
	{
		return SoundEvents.ITEM_CROP_PLANT;
	}

	@Override
	protected SoundEvent getDeathSound()
	{
		return SoundEvents.ENTITY_GHAST_DEATH;
	}

	@Override
	public void livingTick()
	{
		super.livingTick();

		if (this.world.isRemote)
		{
			this.updateTexture();
		}
		else
		{
			this.attackDelay = Math.max(0, this.attackDelay - 1);

			if (this.isWet())
			{
				this.attackEntityFrom(DamageSource.DROWN, 1);
			}
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount)
	{
		if (this.isInvulnerableTo(source))
		{
			return false;
		}

		if (source.getImmediateSource() instanceof SnowballEntity)
		{
			return super.attackEntityFrom(source, Math.max(3.0F, amount));
		}
		else
		{
			return super.attackEntityFrom(source, amount);
		}
	}

	@Override
	protected void registerData()
	{
		super.registerData();
	}

	@Override
	public void writeAdditional(CompoundNBT compound)
	{
		super.writeAdditional(compound);
	}

	@Override
	public void read(CompoundNBT compound)
	{
		super.read(compound);
	}

	public boolean getCanSpawnHere() // areCollisionShapesEmpty areCollisionBoxesEmpty
	{
        ResourceLocation regName = new ResourceLocation("overworld");

        if (world.getDimension().getType().getRegistryName().equals(regName))
        {
            //System.out.println(regName + " is valid!");
            return this.rand.nextDouble() < SPAWN_CHANCE && this.world.hasNoCollisions(this.getBoundingBox()) && this.world.hasNoCollisions(this, this.getBoundingBox());
        }

		return false;
	}

	public void updateTexture()
	{
		if (++this.textureTicks < 2)
			return;

		this.textureTicks = 0;
		this.textureIndex += this.textureInc ? 1 : -1;

		if (this.textureIndex < 0)
		{
			this.textureIndex = 1;
			this.textureInc = true;
		}
		else if (this.textureIndex > 19)
		{
			this.textureIndex = 18;
			this.textureInc = false;
		}
	}

	public int getTextureIndex()
	{
		return this.textureIndex;
	}

	@Override
	public ItemEntity entityDropItem(ItemStack stack, float offsetY)
	{
		if (stack.isEmpty())
		{
			return null;
		}
		else if (this.world.isRemote)
		{
			return null;
		}
		else
		{
			ItemEntity itementity = new ItemEntity(this.world, this.getPosX(), this.getPosY() + (double) offsetY, this.getPosZ(), stack);
			itementity.setDefaultPickupDelay();
			itementity.setInvulnerable(true);
			if (captureDrops() != null)
				captureDrops().add(itementity);
			else
				this.world.addEntity(itementity);
			return itementity;
		}
	}
}
