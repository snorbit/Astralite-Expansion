package com.snorbitzz.astralskies.aircraft;

import com.snorbitzz.astralskies.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * AircraftEntity — the invisible rideable vehicle that represents an assembled aircraft.
 *
 * Flight model:
 *   - WASD        → horizontal movement (relative to player look direction)
 *   - Space       → ascend (positive Y velocity)
 *   - Shift       → descend (negative Y velocity, limited to fall speed cap)
 *   - No input    → hovering with gravity decay (slowly loses altitude unless engine is running)
 *
 * The entity is invisible and non-collidable; the aircraft appearance is the
 * placed blocks themselves. The entity is anchored 1 block above the control panel.
 *
 * Fuel: the engine burns 1 Astralite Scrap per 60 seconds of flight.
 * If fuel runs out the aircraft enters a slow descent mode.
 */
public class AircraftEntity extends Entity {

    // ─── Data ─────────────────────────────────────────────────────────────────

    private static final EntityDataAccessor<Boolean> POWERED =
            SynchedEntityData.defineId(AircraftEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FUEL_TICKS =
            SynchedEntityData.defineId(AircraftEntity.class, EntityDataSerializers.INT);

    /** The control panel block position this aircraft is linked to. */
    private BlockPos panelPos = BlockPos.ZERO;

    // ─── Flight constants ─────────────────────────────────────────────────────

    private static final float SPEED         = 0.35f;   // blocks/tick horizontal
    private static final float ASCEND_SPEED  = 0.25f;   // blocks/tick
    private static final float DESCEND_SPEED = 0.20f;
    private static final float HOVER_GRAVITY = 0.02f;   // slow passive descent
    private static final int   FUEL_PER_SCRAP = 20 * 60; // 20 ticks/s × 60s

    // ─── Constructor ─────────────────────────────────────────────────────────

    public AircraftEntity(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    // ─── SynchedEntityData ────────────────────────────────────────────────────

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(POWERED, false);
        builder.define(FUEL_TICKS, FUEL_PER_SCRAP * 3); // Start with 3 min of fuel
    }

    // ─── NBT ──────────────────────────────────────────────────────────────────

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        panelPos = new BlockPos(tag.getInt("PanelX"), tag.getInt("PanelY"), tag.getInt("PanelZ"));
        entityData.set(FUEL_TICKS, tag.getInt("FuelTicks"));
        entityData.set(POWERED, tag.getBoolean("Powered"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("PanelX", panelPos.getX());
        tag.putInt("PanelY", panelPos.getY());
        tag.putInt("PanelZ", panelPos.getZ());
        tag.putInt("FuelTicks", entityData.get(FUEL_TICKS));
        tag.putBoolean("Powered", entityData.get(POWERED));
    }

    // ─── Tick ─────────────────────────────────────────────────────────────────

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) return;

        // Consume fuel while powered
        if (entityData.get(POWERED)) {
            int fuel = entityData.get(FUEL_TICKS);
            if (fuel > 0) {
                entityData.set(FUEL_TICKS, fuel - 1);
            } else {
                entityData.set(POWERED, false);
            }
        }

        // If aircraft has a rider, apply movement
        if (!getPassengers().isEmpty() && getPassengers().get(0) instanceof Player pilot) {
            applyFlightInput(pilot);
        } else {
            // No rider — slow gravity descent
            Vec3 vel = getDeltaMovement();
            setDeltaMovement(vel.x * 0.8, Math.max(vel.y - HOVER_GRAVITY, -0.3), vel.z * 0.8);
            move(MoverType.SELF, getDeltaMovement());
        }
    }

    private void applyFlightInput(Player pilot) {
        // Read player input flags
        float forward   = pilot.zza;     // positive = forward
        float strafe    = pilot.xxa;     // positive = right
        boolean ascend  = pilot.jumping;
        boolean descend = pilot.isShiftKeyDown();

        // Horizontal movement relative to pilot look
        float yaw = pilot.getYRot() * ((float) Math.PI / 180f);
        double moveX = -(Math.sin(yaw) * forward + Math.cos(yaw) * strafe) * SPEED;
        double moveZ =  (Math.cos(yaw) * forward - Math.sin(yaw) * strafe) * SPEED;

        // Vertical input
        double moveY = 0;
        if (ascend)  moveY =  ASCEND_SPEED;
        if (descend) moveY = -DESCEND_SPEED;
        if (!ascend && !descend) moveY = -HOVER_GRAVITY; // gentle always-on gravity

        setDeltaMovement(moveX, moveY, moveZ);
        move(MoverType.SELF, getDeltaMovement());

        // Keep entity aligned above panel while stationary (drift prevention)
        if (Math.abs(moveX) < 0.001 && Math.abs(moveZ) < 0.001) {
            double cx = panelPos.getX() + 0.5;
            double cz = panelPos.getZ() + 0.5;
            double snapX = cx + (getX() - cx) * 0.7;
            double snapZ = cz + (getZ() - cz) * 0.7;
            setPos(snapX, getY(), snapZ);
        }
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    public BlockPos getPanelPos() { return panelPos; }
    public void setPanelPos(BlockPos pos) { this.panelPos = pos; }

    public boolean isPowered() { return entityData.get(POWERED); }
    public void setPowered(boolean powered) { entityData.set(POWERED, powered); }

    public int getFuelTicks() { return entityData.get(FUEL_TICKS); }
    public void addFuel(int ticks) {
        entityData.set(FUEL_TICKS, entityData.get(FUEL_TICKS) + ticks);
    }

    // ─── Rider config ─────────────────────────────────────────────────────────

    @Override
    public boolean canBeCollidedWith() { return false; }

    @Override
    public boolean isPickable() { return !isRemoved(); }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return getPassengers().isEmpty() && passenger instanceof Player;
    }

    @Override
    protected net.minecraft.world.phys.Vec3 getPassengerRidingPosition(Entity passenger) {
        return super.getPassengerRidingPosition(passenger).add(0, 0.5, 0);
    }
}
