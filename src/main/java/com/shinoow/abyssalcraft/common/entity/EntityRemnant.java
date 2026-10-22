/*******************************************************************************
 * AbyssalCraft
 * Copyright (c) 2012 - 2025 Shinoow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Contributors:
 *     Shinoow -  implementation
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.entity;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.AbyssalCraft;
import com.shinoow.abyssalcraft.api.APIUtils;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.AbyssalCraftAPI.ACEntities;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.entity.IOmotholEntity;
import com.shinoow.abyssalcraft.api.item.ACItems;
import com.shinoow.abyssalcraft.common.entity.ai.LookAtTypeGoal;
import com.shinoow.abyssalcraft.common.entity.ai.WorshipGoal;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Remnant: the citizens of Omothol. They trade with players who carry a Necronomicon (one new offer each time the
 * newest one is used, like the 1.7.10 villagers), insult those who don't, and turn on whatever hurts them, calling the
 * other Remnants nearby for help.<br>
 * Professions: 0 farmer, 1 librarian, 2 priest, 3 blacksmith, 4 butcher, 5 banker, 6 master blacksmith.
 */
public class EntityRemnant extends Monster implements Merchant, IOmotholEntity {

	private static final EntityDataAccessor<Integer> PROFESSION = SynchedEntityData.defineId(EntityRemnant.class, EntityDataSerializers.INT);
	private static final String[] LOOT = {"remnant", "remnant_librarian", "remnant_priest", "remnant_blacksmith", "remnant_butcher", "remnant_banker",
			"remnant_master_blacksmith"};
	/** 1.12.2 MerchantRecipe default */
	private static final int MAX_USES = 7;

	@Nullable
	private Player tradingPlayer;
	@Nullable
	private MerchantOffers offers;
	private int timeUntilReset;
	private boolean needsInitilization;
	private int wealth;
	private int timer;
	private float field_82191_bN;
	/** What an angry Remnant hunts: the class of whatever made it angry (1.12.2 swapped in a target task for it) */
	@Nullable
	private Class<? extends LivingEntity> angerTargetClass;
	/** How many of an item the Remnants buy for an Elder Engraved Coin (min, max) */
	public static final Map<Item, int[]> itemSellingList = new HashMap<>();
	/** What an item costs: coins, or (negative) how many of it a coin buys (min, max) */
	public static final Map<Item, int[]> coinSellingList = new HashMap<>();

	public EntityRemnant(EntityType<? extends EntityRemnant> type, Level level) {
		super(type, level);
		if(ACConfig.hardcoreMode) {
			getAttribute(Attributes.MAX_HEALTH).setBaseValue(200.0D);
			getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(20.0D);
			setHealth(getMaxHealth());
		}
		if(getNavigation() instanceof net.minecraft.world.entity.ai.navigation.GroundPathNavigation nav)
			nav.setCanOpenDoors(true);
	}

	public static AttributeSupplier.Builder createAttributes() {
		return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 64.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.2D)
				.add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.ATTACK_DAMAGE, 10.0D);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(0, new FloatGoal(this));
		goalSelector.addGoal(3, new MeleeAttackGoal(this, 0.35D, false));
		goalSelector.addGoal(4, new OpenDoorGoal(this, true));
		goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.35D));
		goalSelector.addGoal(6, new RandomStrollGoal(this, 0.35D));
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
		goalSelector.addGoal(7, new LookAtPlayerGoal(this, EntityRemnant.class, 8.0F));
		goalSelector.addGoal(7, new LookAtTypeGoal(this, ACEntities.minion_of_the_gatekeeper, 8.0F));
		goalSelector.addGoal(8, new WorshipGoal(this));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
		targetSelector.addGoal(2, new AngerTargetGoal());
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		entityData.define(PROFESSION, 0);
	}

	@Override
	public boolean canBreatheUnderwater() {
		return true;
	}

	@Override
	public boolean doHurtTarget(Entity target) {
		swing(InteractionHand.MAIN_HAND);
		swing(InteractionHand.OFF_HAND);
		boolean flag = super.doHurtTarget(target);

		if(ACConfig.hardcoreMode && target instanceof Player)
			target.hurt(AbyssalCraftAPI.damage(level(), AbyssalCraftAPI.hardcore_attack, this), 3 * (float) Math.max(ACConfig.damageAmpl, 1.0D));

		return flag;
	}

	@Override
	protected void customServerAiStep() {
		if(!isTrading() && timeUntilReset > 0) {
			--timeUntilReset;

			if(timeUntilReset <= 0) {
				if(needsInitilization) {
					// sold out offers come back with a few more uses (configurator shards are sold once)
					if(offers.size() > 1)
						for(int i = 0; i < offers.size(); i++) {
							MerchantOffer offer = offers.get(i);
							if(offer.isOutOfStock() && !isConfiguratorShard(offer.getResult()))
								offers.set(i, new MerchantOffer(offer.getBaseCostA(), offer.getCostB(), offer.getResult(), offer.getUses(),
										offer.getMaxUses() + random.nextInt(6) + random.nextInt(6) + 2, 0, 0.0F));
						}

					addDefaultEquipmentAndRecipies(1);
					needsInitilization = false;
				}

				addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
			}
		}

		super.customServerAiStep();
	}

	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand) {
		if(isAlive() && !player.isShiftKeyDown() && !isAngry())
			if(EntityUtil.hasNecronomicon(player)) {
				if(!isTrading()) {
					if(!level().isClientSide) {
						setTradingPlayer(player);
						openTradingScreen(player, getDisplayName(), 1);
					}
					return InteractionResult.sidedSuccess(level().isClientSide);
				} else if(!level().isClientSide && !tradingPlayer.getUUID().equals(player.getUUID()))
					player.sendSystemMessage(Component.literal(getName().getString() + ": ").append(Component.translatable("message.remnant.busy")));
			} else {
				insult(player);
				return InteractionResult.sidedSuccess(level().isClientSide);
			}

		return super.mobInteract(player, hand);
	}

	/**
	 * Used by Remnants to insult oblivious Players. Players nearby with a Necronomicon understand it.<br>
	 * (1.12.2 sent this from the client, so only the insulted player ever saw it, untranslated)
	 * @param player The target to insult
	 */
	private void insult(Player player) {
		if(level().isClientSide) return;
		int insultNum = random.nextInt(3);
		String name = getName().getString() + ": ";
		Component insult = Component.literal(name + String.format(getInsult(insultNum), player.getName().getString()));
		Component translated = Component.literal(name).append(Component.translatable("message.remnant.insult." + insultNum, player.getName()));
		for(Player player1 : level().getEntitiesOfClass(Player.class, player.getBoundingBox().inflate(16D)))
			player1.sendSystemMessage(EntityUtil.hasNecronomicon(player1) ? translated : insult);
	}

	/**
	 * Insult generator
	 * @param num Which insult to pick, number should be generated through Random.nextInt(3)
	 * @return A insult that humans don't understand
	 */
	private String getInsult(int num) {
		return switch(num) {
		case 1 -> "%s ukhoyah g'ka-dish-tu.";
		case 2 -> "%s go-tha ukhoyah Necronomicon g'mnahn'.";
		default -> "%s, Aklo g'ai ftrr nfto lagln f'tifh mtli ot aishgft 'ai.";
		};
	}

	/**
	 * Used by Minions of The Gatekeeper to set the attack target and enrage the Remnant
	 * @param call If the Remnant should call for backup
	 * @param enemy The target
	 */
	public void enrage(boolean call, @Nullable LivingEntity enemy) {
		setTarget(enemy);
		enrage(call);
	}

	/**
	 * Calling this method will make the Remnant hostile.
	 * Ever wanted to see a angry Remnant? No you don't.
	 * @param call If the Remnant should call for backup
	 */
	public void enrage(boolean call) {
		if(call) {
			for(EntityRemnant friend : level().getEntitiesOfClass(EntityRemnant.class, getBoundingBox().inflate(16D)))
				friend.enrage(false, getTarget());
			playSound(ACSounds.remnant_scream.get(), 3F, 1F);
		}

		setAngry();
		if(getTarget() != null)
			angerTargetClass = getTarget().getClass();
	}

	@Override
	public boolean hurt(DamageSource source, float amount) {
		if(!level().isClientSide && source.getEntity() instanceof LivingEntity attacker) {
			if(getTarget() != attacker)
				enrage(true, attacker);
			if(!isAngry()) enrage(true);
			else enrage(random.nextInt(10) == 0);
		}
		return super.hurt(source, amount);
	}

	@Override
	public boolean canBeAffected(MobEffectInstance effect) {
		return effect.getEffect() != MobEffects.POISON && super.canBeAffected(effect);
	}

	@Override
	public void aiStep() {
		super.aiStep();
		if(level().isClientSide) return;
		if(isAngry()) {
			if(getTarget() != null)
				angerTargetClass = getTarget().getClass();
			timer--;
		} else {
			if(getTarget() != null)
				setTarget(null);
			angerTargetClass = null;
		}
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("Profession", getProfession());
		tag.putInt("Money", wealth);
		tag.putInt("AngerTimer", timer);

		if(offers != null)
			tag.put("Offers", offers.createTag());
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		setProfession(tag.getInt("Profession"));
		wealth = tag.getInt("Money");
		timer = tag.getInt("AngerTimer");

		if(tag.contains("Offers", 10)) {
			offers = new MerchantOffers(tag.getCompound("Offers"));
			offers.removeIf(r -> r.getResult().isEmpty());
		}
	}

	public boolean isAngry() {
		return timer > 0;
	}

	public void setAngry() {
		timer = 600;
	}

	@Override
	protected ResourceLocation getDefaultLootTable() {
		int profession = getProfession();
		return AbyssalCraft.loc("entities/" + LOOT[profession >= 0 && profession < LOOT.length ? profession : 0]);
	}

	@Override
	public boolean removeWhenFarAway(double distance) {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return getProfession() == 2 && random.nextBoolean() ? ACSounds.remnant_priest_chant.get() : null;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return ACSounds.shadow_death.get();
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
	}

	public void setProfession(int profession) {
		entityData.set(PROFESSION, profession);
	}

	public int getProfession() {
		return entityData.get(PROFESSION);
	}

	public int getWealth() {
		return wealth;
	}

	@Override
	public void setTradingPlayer(@Nullable Player player) {
		tradingPlayer = player;
	}

	@Nullable
	@Override
	public Player getTradingPlayer() {
		return tradingPlayer;
	}

	public boolean isTrading() {
		return tradingPlayer != null;
	}

	@Override
	public MerchantOffers getOffers() {
		if(offers == null)
			addDefaultEquipmentAndRecipies(1);

		return offers;
	}

	/** Client side only (the trading screen's copy) */
	@Override
	public void overrideOffers(MerchantOffers offers) {}

	@Override
	public int getVillagerXp() {
		return 0;
	}

	@Override
	public void overrideXp(int xp) {}

	@Override
	public boolean showProgressBar() {
		return false;
	}

	@Override
	public SoundEvent getNotifyTradeSound() {
		return ACSounds.remnant_yes.get();
	}

	@Override
	public boolean isClientSide() {
		return level().isClientSide;
	}

	@Override
	public void die(DamageSource source) {
		super.die(source);
		setTradingPlayer(null);
	}

	private float adjustProbability(float par1) {
		float f1 = par1 + field_82191_bN;
		return f1 > 0.9F ? 0.9F - (f1 - 0.9F) : f1;
	}

	private void addDefaultEquipmentAndRecipies(int par1) {
		if(offers != null)
			field_82191_bN = Mth.sqrt(offers.size()) * 0.2F;
		else
			field_82191_bN = 0.0F;

		List<MerchantOffer> list = new ArrayList<>();
		RandomSource rand = random;
		int k;

		switch(getProfession()) {
		case 0:
			addItemTrade(list, Items.WHEAT, rand, adjustProbability(0.9F));
			addItemTrade(list, Items.WHITE_WOOL, rand, adjustProbability(0.5F));
			addItemTrade(list, Items.CHICKEN, rand, adjustProbability(0.5F));
			addItemTrade(list, Items.COOKED_COD, rand, adjustProbability(0.4F));
			addCoinTrade(list, Items.BREAD, rand, adjustProbability(0.9F));
			addCoinTrade(list, Items.MELON_SLICE, rand, adjustProbability(0.3F));
			addCoinTrade(list, Items.APPLE, rand, adjustProbability(0.3F));
			addCoinTrade(list, Items.COOKIE, rand, adjustProbability(0.3F));
			addCoinTrade(list, Items.SHEARS, rand, adjustProbability(0.3F));
			addCoinTrade(list, Items.FLINT_AND_STEEL, rand, adjustProbability(0.3F));
			if(ACConfig.foodstuff)
				addCoinTrade(list, ACItems.fish_on_a_plate.get(), rand, adjustProbability(0.3F));
			addCoinTrade(list, Items.ARROW, rand, adjustProbability(0.5F));

			if(rand.nextFloat() < adjustProbability(0.5F))
				list.add(offer(new ItemStack(Blocks.GRAVEL, 10), coins(1), new ItemStack(Items.FLINT, 4 + rand.nextInt(2))));

			break;
		case 1:
			addItemTrade(list, Items.PAPER, rand, adjustProbability(0.8F));
			addItemTrade(list, Items.BOOK, rand, adjustProbability(0.8F));
			addItemTrade(list, Items.WRITTEN_BOOK, rand, adjustProbability(0.3F));
			addCoinTrade(list, Items.BOOKSHELF, rand, adjustProbability(0.8F));
			addCoinTrade(list, Items.GLASS, rand, adjustProbability(0.2F));
			addCoinTrade(list, Items.COMPASS, rand, adjustProbability(0.2F));
			addCoinTrade(list, Items.CLOCK, rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.necronomicon.get(), rand, adjustProbability(0.3F));
			addCoinTrade(list, ACItems.abyssal_wasteland_necronomicon.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.dreadlands_necronomicon.get(), rand, adjustProbability(0.1F));
			// scrolls (ported with the spells; until then these offers are skipped)
			RegistryObject<Item>[] scrolls = scrolls();
			for(int i = 0; i < 3; i++)
				if(rand.nextFloat() < adjustProbability(0.1F)) {
					k = 3 * (rand.nextInt(10) + 1) + 1;
					ItemStack input = i == 0 ? new ItemStack(Items.BOOK) : stack(scrolls[i - 1]);
					addIfPresent(list, input, coins(k), stack(scrolls[i]));
				}

			if(rand.nextFloat() < adjustProbability(0.07F)) {
				List<Enchantment> enchantments = new ArrayList<>(ForgeRegistries.ENCHANTMENTS.getValues());
				Enchantment enchantment = enchantments.get(rand.nextInt(enchantments.size()));
				int i1 = Mth.nextInt(rand, enchantment.getMinLevel(), enchantment.getMaxLevel());
				ItemStack itemstack = EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, i1));
				k = 2 + rand.nextInt(5 + i1 * 10) + 3 * i1;
				list.add(offer(new ItemStack(Items.BOOK), coins(k), itemstack));
			}

			break;
		case 2:
			addCoinTrade(list, Items.ENDER_EYE, rand, adjustProbability(0.3F));
			addCoinTrade(list, Items.EXPERIENCE_BOTTLE, rand, adjustProbability(0.2F));
			addCoinTrade(list, Items.REDSTONE, rand, adjustProbability(0.4F));
			addCoinTrade(list, Items.GLOWSTONE, rand, adjustProbability(0.3F));
			addItemTrade(list, Items.ROTTEN_FLESH, rand, adjustProbability(0.7F));
			addItemTrade(list, ACItems.coralium_plagued_flesh.get(), rand, adjustProbability(0.7F));
			addItemTrade(list, ACItems.dread_fragment.get(), rand, adjustProbability(0.7F));
			addItemTrade(list, ACItems.omothol_flesh.get(), rand, adjustProbability(0.7F));
			addItemTrade(list, ACItems.rotten_anti_flesh.get(), rand, adjustProbability(0.3F));
			addCoinTrade(list, ACItems.ritual_charm.get(), rand, adjustProbability(0.4F));
			for(RegistryObject<Item> charm : List.of(ACItems.cthulhu_charm, ACItems.hastur_charm, ACItems.jzahar_charm, ACItems.azathoth_charm,
					ACItems.nyarlathotep_charm, ACItems.yog_sothoth_charm, ACItems.shub_niggurath_charm))
				addCoinTrade(list, ACItems.elder_engraved_coin.get(), 8, charm.get(), 1);
			if(ACItems.staff_of_rending.isPresent())
				addCoinTrade(list, ACItems.staff_of_rending.get(), rand, adjustProbability(0.1F));
			if(rand.nextFloat() < adjustProbability(0.1F))
				addIfPresent(list, stack(ACItems.greater_scroll), new ItemStack(ACItems.liquid_antimatter_bucket.get()), stack(ACItems.antimatter_scroll));
			if(rand.nextFloat() < adjustProbability(0.1F))
				addIfPresent(list, stack(ACItems.greater_scroll), stack(ACItems.oblivion_catalyst), stack(ACItems.oblivion_scroll));
			// enchants an Ethaxium item
			for(RegistryObject<Item> item : List.of(ACItems.ethaxium_sword, ACItems.ethaxium_chestplate, ACItems.ethaxium_axe, ACItems.ethaxium_pickaxe,
					ACItems.ethaxium_shovel))
				if(rand.nextFloat() < adjustProbability(0.05F))
					list.add(offer(new ItemStack(item.get()), coins(2 + rand.nextInt(3)),
							EnchantmentHelper.enchantItem(rand, new ItemStack(item.get()), 5 + rand.nextInt(15), true)));
			break;
		case 3:
			addItemTrade(list, Items.COAL, rand, adjustProbability(0.7F));
			addItemTrade(list, ACItems.abyssalnite_ingot.get(), rand, adjustProbability(0.5F));
			addItemTrade(list, ACItems.refined_coralium_ingot.get(), rand, adjustProbability(0.5F));
			addItemTrade(list, ACItems.dreadium_ingot.get(), rand, adjustProbability(0.5F));
			addItemTrade(list, ACItems.ethaxium_ingot.get(), rand, adjustProbability(0.3F));
			addCoinTrade(list, ACItems.ethaxium_sword.get(), rand, adjustProbability(0.5F));
			addCoinTrade(list, ACItems.ethaxium_axe.get(), rand, adjustProbability(0.3F));
			addCoinTrade(list, ACItems.ethaxium_pickaxe.get(), rand, adjustProbability(0.5F));
			addCoinTrade(list, ACItems.ethaxium_shovel.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.ethaxium_hoe.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.ethaxium_boots.get(), rand, adjustProbability(0.1F));
			addCoinTrade(list, ACItems.ethaxium_helmet.get(), rand, adjustProbability(0.1F));
			addCoinTrade(list, ACItems.ethaxium_chestplate.get(), rand, adjustProbability(0.1F));
			addCoinTrade(list, ACItems.ethaxium_leggings.get(), rand, adjustProbability(0.1F));
			addCoinTrade(list, ACItems.blank_engraving.get(), rand, adjustProbability(0.2F));
			addConfiguratorShards(list, rand);
			break;
		case 4:
			addItemTrade(list, Items.COAL, rand, adjustProbability(0.7F));
			addItemTrade(list, Items.PORKCHOP, rand, adjustProbability(0.5F));
			addItemTrade(list, Items.BEEF, rand, adjustProbability(0.5F));
			addItemTrade(list, Items.CHICKEN, rand, adjustProbability(0.5F));
			if(ACConfig.foodstuff) {
				addCoinTrade(list, ACItems.washcloth.get(), rand, adjustProbability(0.4F));
				addCoinTrade(list, ACItems.mre.get(), rand, adjustProbability(0.1F));
				addCoinTrade(list, ACItems.pork_on_a_plate.get(), rand, adjustProbability(0.3F));
				addCoinTrade(list, ACItems.beef_on_a_plate.get(), rand, adjustProbability(0.3F));
				addCoinTrade(list, ACItems.chicken_on_a_plate.get(), rand, adjustProbability(0.3F));
			} else {
				addCoinTrade(list, Items.COOKIE, rand, adjustProbability(0.4F));
				addCoinTrade(list, Items.GOLDEN_CARROT, rand, adjustProbability(0.1F));
				addCoinTrade(list, Items.PUMPKIN_PIE, rand, adjustProbability(0.3F));
				addCoinTrade(list, Items.RABBIT_STEW, rand, adjustProbability(0.3F));
				addCoinTrade(list, Items.CAKE, rand, adjustProbability(0.3F));
			}
			break;
		case 5:
			// coin exchange: 8/16/24 Elder coins for 1/2/3 deity coins
			for(int amount = 1; amount <= 3; amount++)
				for(RegistryObject<Item> coin : List.of(ACItems.cthulhu_engraved_coin, ACItems.hastur_engraved_coin, ACItems.jzahar_engraved_coin,
						ACItems.azathoth_engraved_coin, ACItems.nyarlathotep_engraved_coin, ACItems.yog_sothoth_engraved_coin, ACItems.shub_niggurath_engraved_coin))
					addCoinTrade(list, ACItems.elder_engraved_coin.get(), 8 * amount, coin.get(), amount);
			break;
		case 6:
			addItemTrade(list, Items.COAL, rand, adjustProbability(0.7F));
			addItemTrade(list, ACItems.abyssalnite_ingot.get(), rand, adjustProbability(0.5F));
			addItemTrade(list, ACItems.refined_coralium_ingot.get(), rand, adjustProbability(0.5F));
			addItemTrade(list, ACItems.dreadium_ingot.get(), rand, adjustProbability(0.5F));
			addItemTrade(list, ACItems.ethaxium_ingot.get(), rand, adjustProbability(0.3F));
			addCoinTrade(list, ACItems.ethaxium_sword.get(), rand, adjustProbability(0.5F));
			addCoinTrade(list, ACItems.ethaxium_axe.get(), rand, adjustProbability(0.3F));
			addCoinTrade(list, ACItems.ethaxium_pickaxe.get(), rand, adjustProbability(0.5F));
			addCoinTrade(list, ACItems.ethaxium_shovel.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.ethaxium_hoe.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.plated_coralium_boots.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.dreadium_samurai_boots.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.plated_coralium_helmet.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.dreadium_samurai_helmet.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.plated_coralium_chestplate.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.dreadium_samurai_chestplate.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.plated_coralium_leggings.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.dreadium_samurai_leggings.get(), rand, adjustProbability(0.2F));
			addCoinTrade(list, ACItems.ethaxium_boots.get(), rand, adjustProbability(0.1F));
			addCoinTrade(list, ACItems.ethaxium_helmet.get(), rand, adjustProbability(0.1F));
			addCoinTrade(list, ACItems.ethaxium_chestplate.get(), rand, adjustProbability(0.1F));
			addCoinTrade(list, ACItems.ethaxium_leggings.get(), rand, adjustProbability(0.1F));
			for(RegistryObject<Item> engraving : List.of(ACItems.cthulhu_engraving, ACItems.hastur_engraving, ACItems.jzahar_engraving,
					ACItems.azathoth_engraving, ACItems.nyarlathotep_engraving, ACItems.yog_sothoth_engraving, ACItems.shub_niggurath_engraving))
				addCoinTrade(list, engraving.get(), rand, adjustProbability(0.1F));
			addConfiguratorShards(list, rand);
		}

		if(list.isEmpty())
			addItemTrade(list, Items.GOLD_INGOT, rand, 1.0F);

		Collections.shuffle(list, new Random(rand.nextLong()));

		if(offers == null)
			offers = new MerchantOffers();

		for(int l = 0; l < par1 && l < list.size(); ++l)
			addToListWithCheck(offers, list.get(l));
	}

	private void addConfiguratorShards(List<MerchantOffer> list, RandomSource rand) {
		for(RegistryObject<Item> shard : List.of(ACItems.configurator_shard_0, ACItems.configurator_shard_1, ACItems.configurator_shard_2,
				ACItems.configurator_shard_3))
			addCoinTrade(list, new ItemStack(shard.get()), rand, adjustProbability(0.2F));
	}

	@SuppressWarnings("unchecked")
	private static RegistryObject<Item>[] scrolls() {
		return new RegistryObject[] {ACItems.basic_scroll, ACItems.lesser_scroll, ACItems.moderate_scroll};
	}

	private static boolean isConfiguratorShard(ItemStack stack) {
		return Stream.of(ACItems.configurator_shard_0, ACItems.configurator_shard_1, ACItems.configurator_shard_2, ACItems.configurator_shard_3)
				.anyMatch(shard -> stack.is(shard.get()));
	}

	@Override
	public void notifyTrade(MerchantOffer offer) {
		offer.increaseUses();
		// 1.12.2 ItemTool (pickaxes, axes, shovels) and armor sell out after two trades
		Item sold = offer.getResult().getItem();
		if(sold instanceof DiggerItem && !(sold instanceof HoeItem) || sold instanceof ArmorItem) {
			offer.increaseUses();
			offer.increaseUses();
			offer.increaseUses();
		}
		if(isConfiguratorShard(offer.getResult()))
			offer.increaseUses();
		// 1.12.2 also meant to sell Necronomicons and Staffs of Rending only once, but checked the price instead of the
		// sold item, so that never applied; left out
		ambientSoundTime = -getAmbientSoundInterval();
		playSound(ACSounds.remnant_yes.get(), getSoundVolume(), getVoicePitch());

		if(hasSameIDsAs(offer, offers.get(offers.size() - 1))) {
			timeUntilReset = 40;
			needsInitilization = true;
		}

		if(APIUtils.isCoin(offer.getBaseCostA()))
			wealth += offer.getBaseCostA().getCount();
	}

	@Override
	public void notifyTradeUpdated(ItemStack stack) {
		if(!level().isClientSide && ambientSoundTime > -getAmbientSoundInterval() + 20) {
			ambientSoundTime = -getAmbientSoundInterval();
			playSound(stack.isEmpty() ? ACSounds.remnant_no.get() : ACSounds.remnant_yes.get(), getSoundVolume(), getVoicePitch());
		}
	}

	/****************** START OF VANILLA CODE FROM 1.7.10 ******************/

	private void addToListWithCheck(MerchantOffers list, MerchantOffer recipe) {
		if(recipe.getResult().isEmpty()) return;
		for(int i = 0; i < list.size(); ++i) {
			MerchantOffer merchantrecipe1 = list.get(i);

			if(hasSameIDsAs(recipe, merchantrecipe1)) {
				if(hasSameItemsAs(recipe, merchantrecipe1))
					list.set(i, recipe);

				return;
			}
		}

		list.add(recipe);
	}

	private boolean hasSameIDsAs(MerchantOffer r1, MerchantOffer r2) {
		return r1.getBaseCostA().getItem() == r2.getBaseCostA().getItem() && r1.getResult().getItem() == r2.getResult().getItem() ?
				r1.getCostB().isEmpty() && r2.getCostB().isEmpty() || !r1.getCostB().isEmpty() && !r2.getCostB().isEmpty()
				&& r1.getCostB().getItem() == r2.getCostB().getItem() : false;
	}

	private boolean hasSameItemsAs(MerchantOffer r1, MerchantOffer r2) {
		return hasSameIDsAs(r1, r2) && (r1.getBaseCostA().getCount() < r2.getBaseCostA().getCount() ||
				!r1.getCostB().isEmpty() && r1.getCostB().getCount() < r2.getCostB().getCount());
	}

	/****************** END OF VANILLA CODE FROM 1.7.10 ******************/

	private static MerchantOffer offer(ItemStack buy1, ItemStack buy2, ItemStack sell) {
		return new MerchantOffer(buy1, buy2, sell, MAX_USES, 0, 0.0F);
	}

	private static ItemStack coins(int count) {
		return new ItemStack(ACItems.elder_engraved_coin.get(), count);
	}

	private static ItemStack stack(RegistryObject<Item> item) {
		return item.isPresent() ? new ItemStack(item.get()) : ItemStack.EMPTY;
	}

	/** Adds the offer if all its items exist (some are ported later) */
	private static void addIfPresent(List<MerchantOffer> list, ItemStack buy1, ItemStack buy2, ItemStack sell) {
		if(!buy1.isEmpty() && !buy2.isEmpty() && !sell.isEmpty())
			list.add(offer(buy1, buy2, sell));
	}

	/** Remnant buys a number of an item for an Elder Engraved Coin */
	public static void addItemTrade(List<MerchantOffer> list, Item item, RandomSource rand, float probability) {
		if(rand.nextFloat() < probability)
			list.add(offer(new ItemStack(item, getQuantity(item, rand)), ItemStack.EMPTY, coins(1)));
	}

	private static int getQuantity(Item item, RandomSource rand) {
		return range(itemSellingList.get(item), rand);
	}

	public static void addCoinTrade(List<MerchantOffer> list, Item item, RandomSource rand, float probability) {
		addCoinTrade(list, new ItemStack(item), rand, probability);
	}

	/**
	 * Remnant sells an item for coins, or a number of it for a coin when its rarity is negative.<br>
	 * (1.12.2 set the stack size to the negative number there, which made those offers empty and dropped them; the
	 * 1.7.10 villager code it was based on used -rarity)
	 */
	public static void addCoinTrade(List<MerchantOffer> list, ItemStack stack, RandomSource rand, float probability) {
		if(rand.nextFloat() < probability) {
			int i = getRarity(stack.getItem(), rand);
			ItemStack price;
			ItemStack sold = stack.copy();

			if(i < 0) {
				price = coins(1);
				sold.setCount(-i);
			} else {
				price = coins(i);
				sold.setCount(1);
			}

			if(isConfiguratorShard(stack))
				list.add(new MerchantOffer(price, ItemStack.EMPTY, sold, 0, 1, 0, 0.0F));
			else
				list.add(offer(price, ItemStack.EMPTY, sold));
		}
	}

	private static int getRarity(Item item, RandomSource rand) {
		return range(coinSellingList.get(item), rand);
	}

	/** 1.12.2: min if min >= max, else min + nextInt(max - min) (the max is never reached) */
	private static int range(@Nullable int[] range, RandomSource rand) {
		return range == null ? 1 : range[0] >= range[1] ? range[0] : range[0] + rand.nextInt(range[1] - range[0]);
	}

	public void addCoinTrade(List<MerchantOffer> list, Item buy, int q1, Item sell, int q2) {
		list.add(offer(new ItemStack(buy, q1), ItemStack.EMPTY, new ItemStack(sell, q2)));
	}

	@Nullable
	@Override
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
		data = super.finalizeSpawn(level, difficulty, reason, data, tag);
		applyRandomTrade(random);

		if(getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
			LocalDate date = LocalDate.now();

			if(date.getMonthValue() == 10 && date.getDayOfMonth() == 31 && random.nextFloat() < 0.25F) {
				setItemSlot(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
				setDropChance(EquipmentSlot.HEAD, 0.0F);
			}
		}

		return data;
	}

	@Override
	protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.9F;
	}

	public void applyRandomTrade(RandomSource rand) {
		setProfession(rand.nextInt(7));
	}

	/** Registers the trade amounts; the items must exist (common setup) */
	public static void registerTradeAmounts() {
		itemSellingList.put(Items.COAL, new int[] {16, 24});
		itemSellingList.put(ACItems.abyssalnite_ingot.get(), new int[] {8, 10});
		itemSellingList.put(ACItems.refined_coralium_ingot.get(), new int[] {8, 10});
		itemSellingList.put(ACItems.dreadium_ingot.get(), new int[] {8, 10});
		itemSellingList.put(ACItems.ethaxium_ingot.get(), new int[] {4, 6});
		itemSellingList.put(Items.PAPER, new int[] {24, 36});
		itemSellingList.put(Items.BOOK, new int[] {11, 13});
		itemSellingList.put(Items.WRITTEN_BOOK, new int[] {1, 1});
		itemSellingList.put(Items.ENDER_PEARL, new int[] {3, 4});
		itemSellingList.put(Items.ENDER_EYE, new int[] {2, 3});
		itemSellingList.put(Items.PORKCHOP, new int[] {14, 18});
		itemSellingList.put(Items.BEEF, new int[] {14, 18});
		itemSellingList.put(Items.CHICKEN, new int[] {14, 18});
		itemSellingList.put(Items.COOKED_COD, new int[] {9, 13});
		itemSellingList.put(Items.WHEAT_SEEDS, new int[] {34, 48});
		itemSellingList.put(Items.MELON_SEEDS, new int[] {30, 38});
		itemSellingList.put(Items.PUMPKIN_SEEDS, new int[] {30, 38});
		itemSellingList.put(Items.WHEAT, new int[] {18, 22});
		itemSellingList.put(Items.WHITE_WOOL, new int[] {14, 22});
		itemSellingList.put(Items.ROTTEN_FLESH, new int[] {16, 28});
		itemSellingList.put(ACItems.coralium_plagued_flesh.get(), new int[] {16, 28});
		itemSellingList.put(ACItems.dread_fragment.get(), new int[] {16, 28});
		itemSellingList.put(ACItems.omothol_flesh.get(), new int[] {32, 60});
		itemSellingList.put(ACItems.rotten_anti_flesh.get(), new int[] {8, 14});
		coinSellingList.put(Items.FLINT_AND_STEEL, new int[] {3, 4});
		coinSellingList.put(Items.SHEARS, new int[] {3, 4});
		coinSellingList.put(ACItems.ethaxium_sword.get(), new int[] {12, 14});
		coinSellingList.put(ACItems.ethaxium_axe.get(), new int[] {9, 12});
		coinSellingList.put(ACItems.ethaxium_pickaxe.get(), new int[] {10, 12});
		coinSellingList.put(ACItems.ethaxium_shovel.get(), new int[] {7, 8});
		coinSellingList.put(ACItems.ethaxium_hoe.get(), new int[] {7, 8});
		coinSellingList.put(ACItems.ethaxium_boots.get(), new int[] {5, 7});
		coinSellingList.put(ACItems.ethaxium_helmet.get(), new int[] {5, 7});
		coinSellingList.put(ACItems.ethaxium_chestplate.get(), new int[] {11, 15});
		coinSellingList.put(ACItems.ethaxium_leggings.get(), new int[] {9, 11});
		coinSellingList.put(Items.BREAD, new int[] {-4, -2});
		coinSellingList.put(Items.MELON_SLICE, new int[] {-8, -4});
		coinSellingList.put(Items.APPLE, new int[] {-8, -4});
		coinSellingList.put(Items.COOKIE, new int[] {-10, -7});
		coinSellingList.put(Items.GLASS, new int[] {-5, -3});
		coinSellingList.put(Items.BOOKSHELF, new int[] {3, 4});
		coinSellingList.put(ACItems.washcloth.get(), new int[] {2, 4});
		coinSellingList.put(ACItems.mre.get(), new int[] {6, 8});
		coinSellingList.put(Items.EXPERIENCE_BOTTLE, new int[] {-4, -1});
		coinSellingList.put(Items.REDSTONE, new int[] {-4, -1});
		coinSellingList.put(Items.COMPASS, new int[] {10, 12});
		coinSellingList.put(Items.CLOCK, new int[] {10, 12});
		coinSellingList.put(ACItems.necronomicon.get(), new int[] {10, 12});
		coinSellingList.put(ACItems.abyssal_wasteland_necronomicon.get(), new int[] {10, 12});
		coinSellingList.put(ACItems.dreadlands_necronomicon.get(), new int[] {10, 12});
		coinSellingList.put(Items.GLOWSTONE, new int[] {-3, -1});
		coinSellingList.put(ACItems.pork_on_a_plate.get(), new int[] {-7, -5});
		coinSellingList.put(ACItems.beef_on_a_plate.get(), new int[] {-7, -5});
		coinSellingList.put(ACItems.chicken_on_a_plate.get(), new int[] {-7, -5});
		coinSellingList.put(ACItems.fish_on_a_plate.get(), new int[] {-7, -5});
		coinSellingList.put(Items.COOKED_CHICKEN, new int[] {-8, -6});
		coinSellingList.put(Items.ENDER_EYE, new int[] {7, 11});
		coinSellingList.put(Items.ARROW, new int[] {-12, -8});
		coinSellingList.put(ACItems.plated_coralium_boots.get(), new int[] {4, 6});
		coinSellingList.put(ACItems.dreadium_samurai_boots.get(), new int[] {7, 8});
		coinSellingList.put(ACItems.plated_coralium_helmet.get(), new int[] {4, 6});
		coinSellingList.put(ACItems.dreadium_samurai_helmet.get(), new int[] {7, 8});
		coinSellingList.put(ACItems.plated_coralium_chestplate.get(), new int[] {10, 14});
		coinSellingList.put(ACItems.dreadium_samurai_chestplate.get(), new int[] {16, 19});
		coinSellingList.put(ACItems.plated_coralium_leggings.get(), new int[] {8, 10});
		coinSellingList.put(ACItems.dreadium_samurai_leggings.get(), new int[] {11, 14});
		if(ACItems.staff_of_rending.isPresent())
			coinSellingList.put(ACItems.staff_of_rending.get(), new int[] {20, 25});
		for(RegistryObject<Item> shard : List.of(ACItems.configurator_shard_0, ACItems.configurator_shard_1, ACItems.configurator_shard_2,
				ACItems.configurator_shard_3))
			coinSellingList.put(shard.get(), new int[] {64, 64});
	}

	/** Angry Remnants hunt the nearest mob of the kind that made them angry */
	private class AngerTargetGoal extends NearestAttackableTargetGoal<LivingEntity> {

		AngerTargetGoal() {
			super(EntityRemnant.this, LivingEntity.class, true);
		}

		@Override
		public boolean canUse() {
			return isAngry() && angerTargetClass != null && super.canUse();
		}

		@Override
		protected void findTarget() {
			target = mob.level().getNearestEntity(mob.level().getEntitiesOfClass(angerTargetClass, getTargetSearchArea(getFollowDistance()), e -> true),
					targetConditions, mob, mob.getX(), mob.getEyeY(), mob.getZ());
		}
	}
}
