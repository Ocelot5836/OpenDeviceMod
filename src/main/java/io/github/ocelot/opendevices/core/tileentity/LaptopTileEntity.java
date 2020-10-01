package io.github.ocelot.opendevices.core.tileentity;

import io.github.ocelot.opendevices.api.computer.Computer;
import io.github.ocelot.opendevices.api.device.Device;
import io.github.ocelot.opendevices.api.device.DeviceTileEntity;
import io.github.ocelot.opendevices.api.device.InteractableDevice;
import io.github.ocelot.opendevices.api.device.process.ProcessManager;
import io.github.ocelot.opendevices.core.init.DeviceBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class LaptopTileEntity extends DeviceTileEntity implements ITickableTileEntity, Computer, InteractableDevice
{
    public static final float LAPTOP_OPENED_ANGLE = 102;

    private final ProcessManager<Computer> processManager;

    @Nullable
    private UUID user;

    private boolean open;
    private float lastRotation;
    private float rotation;

    public LaptopTileEntity()
    {
        super(DeviceBlocks.LAPTOP_TILE_ENTITY.get());
        this.processManager = ProcessManager.create(this);
    }

    @Override
    public void tick()
    {
        if (this.world == null)
            return;

        if (!this.world.isRemote())
        {
            this.processManager.tick();

            ServerPlayerEntity user = (ServerPlayerEntity) this.getUser();
            if (user != null && !this.canInteract(user))
            {
                this.stopInteracting(user);
            }
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        super.write(nbt);
        nbt.putBoolean("open", this.open);
        nbt.put("ProcessManager", this.processManager.serializeNBT());
        return nbt;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        this.open = nbt.getBoolean("open");
        this.processManager.deserializeNBT(nbt.getCompound("ProcessManager"));
    }

    public void toggleOpen(PlayerEntity player)
    {
        if (this.canInteract(this.getUser()) || !this.canInteract(player))
            return;
        this.open = !this.open;
        this.markDirty();
        if (this.world != null)
            this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.DEFAULT);
    }

    @Override
    public boolean startInteracting(PlayerEntity player)
    {
        if (this.canInteract(this.getUser()) || !this.open)
            return false;
        if (this.canInteract(player))
        {
            this.user = player.getUniqueID();
            return true;
        }
        return false;
    }

    @Override
    public void stopInteracting(ServerPlayerEntity player)
    {
        if (this.user == null)
            return;
        if (this.user.equals(player.getUniqueID()))
        {
            this.user = null;
            player.closeScreen();
        }
    }

    @Override
    public boolean canInteract(@Nullable PlayerEntity player)
    {
        if (player == null)
            return false;
        return player.getDistanceSq(this.pos.getX(), this.pos.getY(), this.pos.getZ()) <= 64D;
    }

    @Override
    public PlayerEntity getUser()
    {
        if (this.user == null || this.world == null)
            return null;
        return this.world.getPlayerByUuid(this.user);
    }

    public boolean isOpen()
    {
        return open;
    }

    @OnlyIn(Dist.CLIENT)
    public float getScreenAngle(float partialTicks)
    {
        return LAPTOP_OPENED_ANGLE * MathHelper.lerp(partialTicks, this.lastRotation, this.rotation);
    }

    @Override
    public int getScreenWidth()
    {
        return 364;
    }

    @Override
    public int getScreenHeight()
    {
        return 216;
    }

    @Override
    public Optional<ProcessManager<? extends Device>> getProcessManager()
    {
        return Optional.of(this.processManager);
    }
}
