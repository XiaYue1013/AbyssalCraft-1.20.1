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
 *     1.20.1 port - server ticker runs the ritual, the client ticker only advances its timer for the particles
 *     (started by RitualStartMessage); the ritual starts on the next server tick like the 1.12.2 Scheduler did
 ******************************************************************************/
package com.shinoow.abyssalcraft.common.blocks.tile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.shinoow.abyssalcraft.api.AbyssalCraftAPI;
import com.shinoow.abyssalcraft.api.block.IRitualAltar;
import com.shinoow.abyssalcraft.api.block.IRitualPedestal;
import com.shinoow.abyssalcraft.api.energy.EnergyEnum.DeityType;
import com.shinoow.abyssalcraft.api.energy.IEnergyTransporterItem;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionEntry;
import com.shinoow.abyssalcraft.api.energy.disruption.DisruptionHandler;
import com.shinoow.abyssalcraft.api.entity.EntityUtil;
import com.shinoow.abyssalcraft.api.event.ACEvents.DisruptionEvent;
import com.shinoow.abyssalcraft.api.event.ACEvents.RitualEvent;
import com.shinoow.abyssalcraft.api.necronomicon.condition.caps.NecroDataCapability;
import com.shinoow.abyssalcraft.api.ritual.EnumRitualParticle;
import com.shinoow.abyssalcraft.api.ritual.NecronomiconRitual;
import com.shinoow.abyssalcraft.api.ritual.RitualRegistry;
import com.shinoow.abyssalcraft.common.items.ItemNecronomicon;
import com.shinoow.abyssalcraft.common.network.PacketDispatcher;
import com.shinoow.abyssalcraft.common.network.client.RitualMessage;
import com.shinoow.abyssalcraft.common.network.client.RitualStartMessage;
import com.shinoow.abyssalcraft.init.ACBlockEntities;
import com.shinoow.abyssalcraft.lib.ACConfig;
import com.shinoow.abyssalcraft.lib.ACSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;

/**
 * Ritual Altar: takes the offerings from its eight pedestals, collects the ritual's PE from the performer over
 * 10 seconds and then completes the ritual (or causes a disruption when it was interrupted)
 */
public class TileEntityRitualAltar extends BlockEntity implements IRitualAltar {

	private int ritualTimer;
	@Nullable
	private NecronomiconRitual ritual;
	private ItemStack item = ItemStack.EMPTY;
	@Nullable
	private Player user;
	private float consumedEnergy;
	@Nullable
	private Mob sacrifice;
	private List<IRitualPedestal> pedestals = new ArrayList<>();

	public TileEntityRitualAltar(BlockPos pos, BlockState state) {
		super(ACBlockEntities.RITUAL_ALTAR.get(), pos, state);
	}

	@Override
	public void load(CompoundTag nbttagcompound)
	{
		super.load(nbttagcompound);
		item = ItemStack.of(nbttagcompound.getCompound("Item"));
		ritualTimer = nbttagcompound.getInt("Cooldown");
	}

	@Override
	protected void saveAdditional(CompoundTag nbttagcompound)
	{
		super.saveAdditional(nbttagcompound);
		CompoundTag nbtItem = new CompoundTag();
		if(!item.isEmpty())
			item.save(nbtItem);
		nbttagcompound.put("Item", nbtItem);
		nbttagcompound.putInt("Cooldown", ritualTimer);
	}

	@Override
	public CompoundTag getUpdateTag() {
		return saveWithoutMetadata();
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	private void markForUpdate() {
		setChanged();
		if(level != null && !level.isClientSide)
			level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, TileEntityRitualAltar tile) {
		if(tile.isPerformingRitual()) {
			tile.ritualTimer++;
			level.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 0,0,0);
		}
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityRitualAltar tile) {
		if(!tile.isPerformingRitual()) return;
		if(tile.user != null && tile.user.isRemoved())
			tile.user = null;

		if(tile.ritualTimer == 1){
			SoundEvent chant = tile.getRandomChant();
			for(int i = 0; i < 3; i++)
				level.playSound(null, pos, chant, SoundSource.PLAYERS, 1, 1);
			if(tile.ritual != null && tile.sacrifice != null && tile.sacrifice.isAlive())
				tile.sacrifice.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, false, false));
		}
		tile.ritualTimer++;
		if(tile.ritual != null){
			if(tile.ritualTimer % 20 == 0)
				if(tile.user != null)
					tile.collectPEFromPlayer();
				else tile.user = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 5, true);
			if(tile.ritualTimer == 200)
				if(tile.user != null){
					if(!MinecraftForge.EVENT_BUS.post(new RitualEvent.Post(tile.user, tile.ritual, level, pos))){
						//Fixes any rounding errors from uneven numbers, but likely breaks rituals with decimal numbers for required energy. I can live with that.
						tile.consumedEnergy = BigDecimal.valueOf(tile.consumedEnergy).setScale(1, RoundingMode.HALF_UP).floatValue();
						if(tile.consumedEnergy == tile.ritual.getReqEnergy() && (tile.sacrifice == null || !tile.sacrifice.isAlive()))
							tile.ritual.completeRitual(level, pos, tile.user);
						else
							tile.triggerDisruption();
					}
					tile.reset();
				} else {
					tile.triggerDisruption();
					tile.reset();
				}
		} else tile.reset();
	}

	private void reset() {
		ritualTimer = 0;
		user = null;
		ritual = null;
		consumedEnergy = 0;
		sacrifice = null;
		markForUpdate();
	}

	private void collectPEFromPlayer() {
		ItemStack stack = user.getItemInHand(InteractionHand.OFF_HAND);
		if(stack.getItem() instanceof IEnergyTransporterItem transporter && transporter.canTransferPEExternally(stack) && transporter.getContainedEnergy(stack) > 0)
			consumedEnergy += transporter.consumeEnergy(stack, ritual.getReqEnergy()/10);
		else
			for(ItemStack stack1 : user.getInventory().items)
				if(stack1.getItem() instanceof IEnergyTransporterItem transporter && transporter.canTransferPEExternally(stack1) && transporter.getContainedEnergy(stack1) > 0){
					consumedEnergy += transporter.consumeEnergy(stack1, ritual.getReqEnergy()/10);
					break;
				}
	}

	private void triggerDisruption() {
		if(!(level instanceof ServerLevel serverLevel)) return;
		LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
		if(bolt != null) {
			bolt.moveTo(worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ());
			bolt.setVisualOnly(true);
			serverLevel.addFreshEntity(bolt);
		}
		DeityType deity = DeityType.values()[level.random.nextInt(DeityType.values().length)];
		List<Player> players = level.getEntitiesOfClass(Player.class, new AABB(worldPosition).inflate(16, 16, 16));
		if(user != null && ritual != null) {
			RitualEvent.Failed event = new RitualEvent.Failed(user, ritual, DisruptionHandler.instance().getRandomDisruption(deity, level), level, worldPosition);
			if(!MinecraftForge.EVENT_BUS.post(event) && !ACConfig.no_disruptions) {
				DisruptionEntry disruption = event.getDisruption();
				if(disruption == null) return;
				PacketDispatcher.sendToAllAround(new RitualMessage(ritual.getId(), worldPosition, true, disruption.getUnlocalizedName()), level.dimension(), worldPosition, 5);
				if(!MinecraftForge.EVENT_BUS.post(new DisruptionEvent(deity, level, worldPosition, players, disruption)))
					disruption.disrupt(level, worldPosition, players);
				AbyssalCraftAPI.getInternalMethodHandler().sendDisruption(deity, disruption.getUnlocalizedName().substring("ac.disruption.".length()), worldPosition, level);
			} else if(!ACConfig.no_disruptions) DisruptionHandler.instance().generateDisruption(deity, level, worldPosition, players);
		} else if(!ACConfig.no_disruptions) DisruptionHandler.instance().generateDisruption(deity, level, worldPosition, players);
	}

	@Override
	public boolean canPerform(){
		if(pedestals.isEmpty())
			for(IRitualPedestal ped : findPedestals())
				ped.setAltar(worldPosition);
		pedestals.removeIf(p -> p instanceof BlockEntity be && be.isRemoved());
		return pedestals.size() == 8 && pedestals.stream().anyMatch(p -> !p.getItem().isEmpty());
	}

	private List<IRitualPedestal> findPedestals(){
		List<IRitualPedestal> list = new ArrayList<>();
		for(BlockPos offset : com.shinoow.abyssalcraft.lib.util.RitualUtil.PEDESTAL_POSITIONS)
			if(level.getBlockEntity(worldPosition.offset(offset)) instanceof IRitualPedestal pedestal)
				list.add(pedestal);
			else return new ArrayList<>();
		return list;
	}

	@Override
	public void performRitual(Level world, BlockPos pos, Player player){
		if(world.isClientSide || isPerformingRitual()) return;
		ItemStack stack = player.getMainHandItem();
		if(stack.getItem() instanceof ItemNecronomicon book)
			if(RitualRegistry.instance().canPerformAction(world.dimension(), book.getBookType()))
				if(canPerform()){
					ritual = RitualRegistry.instance().getRitual(world.dimension(), book.getBookType(), pedestals.stream().map(IRitualPedestal::getItem).toArray(ItemStack[]::new), item);
					if(ritual != null && NecroDataCapability.getCap(player).isUnlocked(ritual.getUnlockCondition(), player))
						if(ritual.requiresSacrifice()){
							List<Mob> mobs = world.getEntitiesOfClass(Mob.class, new AABB(pos).inflate(4, 4, 4));
							if(!mobs.isEmpty()) {
								for(Mob mob : mobs)
									if(canBeSacrificed(mob) && ritual.canCompleteRitual(world, pos, player)
											&& !MinecraftForge.EVENT_BUS.post(new RitualEvent.Pre(player, ritual, world, pos))){
										start(player, mob);
										return;
									}
							} else player.displayClientMessage(Component.translatable("message.ritual.missingsacrifice"), true);
						} else if(ritual.canCompleteRitual(world, pos, player))
							if(!MinecraftForge.EVENT_BUS.post(new RitualEvent.Pre(player, ritual, world, pos))){
								start(player, null);
								return;
							}
					ritual = null;
				}
	}

	/** Takes the offerings and starts the ritual on the next tick */
	private void start(Player player, @Nullable Mob mob) {
		NecronomiconRitual started = ritual;
		pedestals.forEach(IRitualPedestal::consumeItem);
		level.getServer().execute(() -> {
			ritual = started;
			sacrifice = mob;
			ritualTimer = 1;
			user = player;
			consumedEnergy = 0;
			markForUpdate();
			PacketDispatcher.sendToAllAround(new RitualStartMessage(worldPosition, started.getId(), mob != null ? mob.getId() : 0),
					level.dimension(), worldPosition, 30);
		});
	}

	/**
	 * Checks if a certain Entity can be sacrificed
	 * @param entity Entity to potentially sacrifice
	 * @return True if the Entity can be sacrificed, otherwise false
	 */
	private boolean canBeSacrificed(Mob entity){
		return (EntityUtil.isShoggothFood(entity) || entity instanceof Villager) &&
				entity.getMobType() != MobType.UNDEAD &&
				entity.isAlive() && !entity.isBaby();
	}

	public SoundEvent getRandomChant(){
		SoundEvent[] chants = {ACSounds.cthulhu_chant.get(), ACSounds.yog_sothoth_chant_1.get(), ACSounds.yog_sothoth_chant_2.get(),
				ACSounds.hastur_chant_1.get(), ACSounds.hastur_chant_2.get(), ACSounds.sleeping_chant.get(), ACSounds.cthugha_chant.get()};
		return chants[level.random.nextInt(chants.length)];
	}

	@Override
	public int getRitualCooldown(){
		return ritualTimer;
	}

	@Override
	public boolean isPerformingRitual(){
		return ritualTimer < 200 && ritualTimer > 0;
	}

	@Override
	public ItemStack getItem(){
		return item;
	}

	@Override
	public void setItem(ItemStack item){
		this.item = item;
		markForUpdate();
	}

	@Override
	public void setRitualFields(@Nullable NecronomiconRitual ritual, @Nullable Mob sacrifice) {
		this.ritual = ritual;
		this.sacrifice = sacrifice;
		ritualTimer = 1;
	}

	@Override
	public void addPedestal(IRitualPedestal pedestal) {
		if(!pedestals.contains(pedestal))
			pedestals.add(pedestal);
	}

	@Override
	public List<IRitualPedestal> getPedestals() {
		return pedestals;
	}

	@Override
	public EnumRitualParticle getRitualParticle() {
		return ritual != null ? ritual.getRitualParticle() : EnumRitualParticle.NONE;
	}
}
