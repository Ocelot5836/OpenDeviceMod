package com.ocelot.opendevices.tileentity;

import com.ocelot.opendevices.api.device.DeviceTileEntity;
import com.ocelot.opendevices.init.DeviceBlocks;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

public class LaptopTileEntity extends DeviceTileEntity implements ITickableTileEntity
{
    public static final int OPENED_ANGLE = 102;

    private UUID user;
    private boolean open;

    @OnlyIn(Dist.CLIENT)
    private int rotation;
    @OnlyIn(Dist.CLIENT)
    private int prevRotation;

    public LaptopTileEntity()
    {
        super(DeviceBlocks.TE_LAPTOP);
        this.user = null;
        this.open = false;
    }

    @Override
    public void tick()
    {
        if (this.world != null && this.world.isRemote) {
            this.prevRotation = this.rotation;
            if (!open) {
                if (rotation > 0) {
                    rotation -= 10F;
                }
            }
            else {
                if (rotation < OPENED_ANGLE) {
                    rotation += 10F;
                }
            }
        }
    }

    @Override
    public void save(CompoundNBT nbt)
    {
    }

    @Override
    public void load(CompoundNBT nbt)
    {
    }

    public boolean toggleOpen(PlayerEntity player)
    {
        if (this.hasUser())
            return false;
        this.open = !this.open;
        return this.canInteract(player);
    }

    public boolean view(PlayerEntity player)
    {
        if (this.hasUser())
            return false;
        if (this.canInteract(player)) {
            this.user = player.getUniqueID();
            return true;
        }
        return false;
    }

    public void stopView(PlayerEntity player)
    {
        if (this.user == null)
            return;
        if (this.user.equals(player.getUniqueID()))
            this.user = null;
    }

    @Nullable
    public UUID getUser()
    {
        return user;
    }

    @Nullable
    public PlayerEntity getUserPlayer()
    {
        if (this.user == null || this.world == null)
            return null;
        return this.world.getPlayerByUuid(this.user);
    }

    public boolean hasUser()
    {
        PlayerEntity player = this.getUserPlayer();
        return player != null && this.canInteract(player);
    }

    public boolean canInteract(@Nullable PlayerEntity player)
    {
        if (player == null)
            return false;
        return player.getDistanceSq(this.pos.getX(), this.pos.getY(), this.pos.getZ()) <= 64D;
    }

    @OnlyIn(Dist.CLIENT)
    public float getScreenAngle(float partialTicks)
    {
        return -OPENED_ANGLE * ((this.prevRotation + (this.rotation - this.prevRotation) * partialTicks) / OPENED_ANGLE); //TODO optimize
    }
}