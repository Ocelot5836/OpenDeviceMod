package com.ocelot.opendevices.api.task;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * <p>A Task is simple implementation that allows you to make calls to the
 * server to process actions, store or retrieve data, etc. Useful for any
 * client-server like applications, e.g. Emails, Instant Messaging, etc</p>
 *
 * <p>Any global variables that are initialized in this class, wont be on the server side.
 * To initialize them, first store the data in the NBT tag provided in {@link #prepareRequest(CompoundNBT)},
 * then once your Task gets to the server, use {@link #processRequest(CompoundNBT, World, PlayerEntity)} to
 * get the data from the NBT tag parameter. Initialize the variables as normal.
 *
 * <p>Please check out the example applications to get a better understanding
 * how this could be useful to your application.</p>
 *
 * @author MrCrayfish, Ocelot
 * @see TaskManager
 */
public abstract class Task
{
    private boolean success = false;

    /**
     * Sets that this Task was successful. Should be called
     * if your Task produced the correct results, preferably in
     * {@link #processRequest(CompoundNBT, World, PlayerEntity)}
     */
    public final void setSuccessful()
    {
        this.success = true;
    }

    /**
     * Gets if this Task produced the correct results.
     *
     * @return if this task was successful
     */
    public final boolean isSucessful()
    {
        return this.success;
    }

    /**
     * Called before the request is sent off to the server.
     * You should store the data you want to sendTask into the NBT Tag
     *
     * @param nbt The NBT to be sent to the server
     */
    public abstract void prepareRequest(CompoundNBT nbt);

    /**
     * Called when the request arrives to the server. Here you can perform actions
     * with your request. Data attached to the NBT from {@link Task#prepareRequest(CompoundNBT nbt)}
     * can be accessed from the NBT tag parameter.
     *
     * @param nbt The NBT Tag received from the client
     */
    public abstract void processRequest(CompoundNBT nbt, World world, PlayerEntity player);

    /**
     * Called before the response is sent back to the client.
     * You should store the data you want to sendTask back into the NBT Tag
     *
     * @param nbt The NBT to be sent back to the client
     */
    public abstract void prepareResponse(CompoundNBT nbt);

    /**
     * Called when the response arrives to the client. Here you can update data
     * on the client side. If you want to update any UI component, you should set
     * a Callback before you sendTask the request.
     *
     * @param nbt The NBT Tag received from the server
     */
    public abstract void processResponse(CompoundNBT nbt, World world, PlayerEntity player);

    /**
     * Registers a new type of task that can be used in the {@link TaskManager}.
     *
     * @author Ocelot
     * @see Task
     */
    @Target(ElementType.TYPE)
    public @interface Register
    {
        /**
         * @return The name of this task. Should be in the format of <code>modid:taskName</code>
         */
        String value();
    }
}
