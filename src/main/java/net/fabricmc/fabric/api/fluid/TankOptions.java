package net.fabricmc.fabric.api.fluid;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.ObjLongConsumer;

import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.Direction;

public interface TankOptions {
    /**
     * Methods common to all tank variants.
     */
    interface TankBase {

        /**
         * Unit of measure the tank is optimized for. See {@link #capacity()}.
         * Otherwise mostly descriptive and does not limit behavior.
         * A HUD info mod may want to use this unit for displaying tank contents.
         */
        default VolumeUnit baseUnit() {
            return VolumeUnitRegistry.BUCKET;
        }

        /**
         * Total tank capacity in the given unit, rounded down. No rounding will
         * occur if unit == {@link #baseUnit()}.
         * 
         * Tank capacity <em>must</em> be a multiple of {@link #baseUnit()} and must 
         * not be zero, but it <em>can</em> be less than one bucket.
         * For information only, do not use to forecast transaction results.
         * 
         * Will return {@link Long#MAX_VALUE} if result is larger than can be
         * expressed in the given unit.
         */
        long capacity(VolumeUnit unit);

        /**
         * How full the tank is, rounded down.
         * A zero result does not necessarily mean the tank is empty.
         * For information only, do not use to forecast transaction results.
         * 
         * Will return {@link Long#MAX_VALUE} if result is larger than can be
         * expressed in the given unit.
         */
        long usedCapacity(VolumeUnit unit);

        /**
         * Empty space in the tank, rounded down.
         * For information only, do not use to forecast transaction results.
         * 
         * Will return {@link Long#MAX_VALUE} if result is larger than can be
         * expressed in the given unit.
         */
        long availableCapacity(VolumeUnit unit);

        /**
         * For tanks that somehow support variable capacity, allows changing it.
         * Units must equal {@link #baseUnit()} or the tank must allow that
         * value also to change, in which case the unit parameter sets the new base unit for the tank.
         * 
         * Returns false if tank does not allow changing capacity or if the
         * request would result in an invalid state (tank too full, for example).
         */
        default boolean setCapacity(long capacity, VolumeUnit unit){
            return false;
        }

        /**
         * Adds fluid to this tank, returning the amount added (or a forecast of same if requested). 
         * 
         * @param fluidType Fluid to add - must equal {@link #fluid()} unless tank is empty.
         * @param amount  How much
         * @param unit  Units of amount
         * @param simulate It true, forecasts result without changing tank state.
         * @param allowPartial If false, nothing will happen unless full requested amount can be added.
         * @return Amount actual added, in units given.
         */
        long add(FabricFluidType fluidType, long amount, VolumeUnit unit, boolean simulate, boolean allowPartial);

        /**
         * Convenience for allowPartial = true, which is the common case.
         */
        default long add(FabricFluidType fluidType, long amount, VolumeUnit unit, boolean simulate) {
            return add(fluidType, amount, unit, simulate, true);
        }

        /**
         * Removes fluid from this tank, returning the amount removed (or a forecast of same if requested). 
         * 
         * @param fluidType Fluid to remove - must equal {@link #fluid()} or nothing will happen.
         * @param amount  How much
         * @param unit  Units of amount
         * @param simulate It true, forecasts result without changing tank state.
         * @param allowPartial If false, nothing will happen unless full requested amount can be removed.
         * @return Amount actual removed, in units given.
         */
        long remove(FabricFluidType fluidType, long amount, VolumeUnit unit, boolean simulate, boolean allowPartial);

        /**
         * Convenience for allowPartial = true, which is the common case.
         */
        default long remove(FabricFluidType fluidType, long amount, VolumeUnit unit, boolean simulate) {
            return remove(fluidType, amount, unit, simulate, true);
        }

        /**
         * Sets the current fluid amount, discarding any existing contents.
         * If the tank implementation supports multiple fluids (OptionB) then
         * this affects only the fluid specified and has no effect on other contents.
         * 
         * Returns false if tank does not allow changing capacity or if the
         * request would result in an invalid state (tank too full, for example).
         */
        boolean set(FabricFluidType fluidType, long amount, VolumeUnit unit);

        /**
         * True if tank has nothing in it.
         */
        boolean  isEmpty();

        /** 
         * Serializes contents to Nbt.
         */
        Tag toNbt();

        /** 
         * Deserializes contents from Nbt.
         */
        void fromNbt(Tag tag);

        /** 
         * Serializes contents to network packet buffer.
         */
        void toPacket(PacketByteBuf packetBuff);

        /** 
         * Deserializes contents from network packet buffer.
         */
        void fromPacket(PacketByteBuf packetBuff);
    }

    /**
     * Tanks can hold only one thing and there are no standards for getting access to a tank.
     * 
     * Public methods of the tank should only be called from the main thread.
     * However, nothing prevents any tank implementation from allowing concurrent access
     * for mod-specific features external to this API. Tanks the do expect/support
     * concurrency will need to synchronize appropriately.
     * 
     * A consequence of this "probably on the main thread but maybe not" expectation
     * is that simulation results of {@link Tank#add(FabricFluidType, long, VolumeUnit, boolean)} 
     * and {@link Tank#remove(FabricFluidType, long, VolumeUnit, boolean)} are not reliable.
     * Transport mods must therefore allow for the possibility that a tank that earlier said it
     * could accept four buckets is now only willing to accept three and a bucket in transit
     * needs a place to go.
     * 
     * In all instances, tanks may have predicates that limit what
     * fluids can be stored in them.  To know if a tank can store a fluid, 
     * you have to simulate putting the fluid in the tank.
     * These rules aren't directly exposed in the interface because they 
     * may not be always be simple and you end up simulating anyway.
     */
    interface OptionA {
        interface Tank extends TankBase {
            /**
             * Current fluid in the tank, or null if the tank is completely empty.
             */
            FabricFluidType fluid();
        }
    }

    /**
     * A tank can store more than one fluid, if it wants to, with 
     * a common capacity limit. TCon is a well-known use case. 
     */
    interface OptionB {
        interface Tank extends TankBase {
            /**
             * Provide quantities of all fluids in the tank in the given unit, rounded down.
             *
             * Amounts will be {@link Long#MAX_VALUE} if larger than can be expressed in the given unit.
             */
            void forEachAmount(VolumeUnit unit, ObjLongConsumer<FabricFluidType> consumer);
            
            /**
             * Removes all fluid(s) from the tank.
             */
            void clear();
        }
    }

    /**
     * Introduces concept of a "device" that can hold more than one tank.
     * Tanks gain some descriptive information to distinguish them from
     * other tanks in the same device.
     */
    interface OptionC {
        /*
         * A device provides/controls access to the tanks contained in it.
         * Devices are typically blocks or items, but an entity or any other 
         * game construct can implement this interface.
         */
        interface TankDevice {
            /**
             * True if tank access varies based on cardinal direction. Generally
             * applies for in-world blocks, but nothing stops a "cow" entity from
             * containing a TankDevice that expects certain fluids to enter/exit only from  
             * from the front/back, for example.
             */
            boolean sided();

            /**
             * Produces tanks accessible from the given side.
             * 
             * When a device is sided, the tank instance(s) returned may
             * limit input or output based on the side by which is was retrieved.
             * 
             * For example, the tank instance retrieved from TOP could have the same
             * id and description as the tank instance retrieved from BOTTOM, 
             * and yet the TOP instance could only allow input and the BOTTOM
             * instance could only allow output.  This behavior is entirely
             * up to the tank / device implementation.
             * 
             * If the tank is sided and side parameter is null, will return only 
             * those tanks available from all sides with the same access.
             * 
             * If the tank is not sided the side parameter is ignored.
             */
            void forEachTank(Consumer<Tank> consumer, Direction side);

            /**
             * Convenience for non-sided devices or when you only want tanks
             * available from all sides.
             */
            default void forEachTank(Consumer<Tank> consumer) {
                forEachTank(consumer, null);
            }
        }

        interface Tank extends OptionB.Tank {
            /** 
             * Name-spaced id for the tank. Each unique tank in a device must have a different id.
             * 
             * If id is equal for two tank instances returned from the same device, that
             * does imply the <em>contents</em> of both instances are the same - they point
             * to the same fluid storage internally.  However, tank instances with the same id
             * are not guaranteed to be the same (==) object instance and could impose different 
             * access restrictions when the tank is sided.
             * 
             * See {@link TankDevice#forEachTank(Consumer, Direction)}.
             * 
             * Community standards for tank ids are welcome but not part of the Fabric API.
             */
            Identifier id();

            /**
             * Key for user-friendly label of the tank within the device.
             */
            String getTranslationKey();
        }
    }

    /**
     * Tanks can publish concurrency guarantees for input and output operations
     * and a specialized execution service allows most tank operations
     * to run outside the main thread without explicit synchronization.
     * 
     * This can improve game experience on dedicated servers running complex pipe
     * networks that might normally suffer TPS lag by moving some of the load to 
     * a dedicated service thread. (Offers no help on the client side where
     * where it either isn't needed (because the server does it) or other
     * cores are busy with rendering tasks (true for integrated server).
     * 
     * This can also make multi-tank fluid transactions simpler and more reliable
     * when all involved tanks support the appropriate guarantees because
     * simulation results will always be reliable within the same execution task.
     * 
     * Service thread processing is paused during world save.
     * 
     * Note that the service task queue is not persisted.  Mods using it must not
     * update persistent state except as a result of task completion.
     */
    interface OptionD {
        /**
         * Dedicated, single-thread execution service for tank operations
         * with a specialized prioritization mechanism to allow urgent
         * requests that block the main server thread to execute immediately.
         * 
         * All tasks run on the same thread but tasks submitted with immediate = true 
         * will always start before tasks submitted with immediate = false.
         * Otherwise tasks start in the order of submission.
         * 
         * Immediate tasks should only be used for handling interactive or otherwise
         * urgent requests from the server main thread or network handlers. Normal
         * tank/pipe processing that doesn't interact with or depend directly on world 
         * or player state should be scheduled to run asynchronously with immediate = false.
         */
        interface TankService {
            /** would point to implementation */
            TankService INSTANCE = null;
            
            /**
             * True if the current thread is the execution thread for this service.
             */
            boolean isRunningOn();
            
            /**
             * Schedules the task for later execution on the service thread.
             * 
             * @param task  Can (and should) embody multiple tank operations when the
             * intent is to ensure that simulation results are reliable. 
             * 
             * @param immediate If true, the task will run immediately after the
             * currently running task and after any other immediate tasks already queued.
             */
            void execute(Runnable task, boolean immediate);
            
            public <T> Future<T> submit(Callable<T> task, boolean immediate);

            public <T> Future<T> submit(Runnable task, T result, boolean immediate);

            public Future<?> submit(Runnable task, boolean immediate);
            
            /**
             * Convenience for tanks implementing service guarantees.  If the call is already
             * on the service thread this simply returns result.  If not, submits to service thread
             * queue for immediate execution and blocks caller until operation is complete.
             * 
             * Do not use to orchestrate transport operations across more than one tank.
             * For those use the submit or execute methods and bundle all operations into a
             * single task.  Also note that automated transport should be designed to run
             * asynchronously whenever possible - reserve immediate tasks for world and player interactions.
             */
            default long runReliably(LongSupplier task) throws InterruptedException, ExecutionException {
                return isRunningOn() ? task.getAsLong() : submit(task::getAsLong, true).get();
            }
        }
       
        interface Tank extends OptionC.Tank {
            /**
             * If true, the tank guarantees any operation that results in a 
             * net increase of fluid in the tank will run on the TankService thread.
             * This guarantee includes calls to {@link #set(FabricFluidType, long, VolumeUnit)}
             * if the set amount is higher than the current amount.
             * 
             * This in turn ensures simulation results reported by {@link #add(FabricFluidType, long, VolumeUnit, boolean)}
             * will always be honored on the tank service thread within the same task.  
             * This guarantee can be extremely useful for transport networks that do not buffer fluid in transit, 
             * and may enable optimizations in other networks depending on how they operate.
             * 
             * The simplest way to implement the reliability guarantee will be via {@link TankService#runReliably(LongSupplier)}
             * 
             * It is unnecessary and potentially inefficient to offer this guarantee if the
             * tank is a machine output buffer, or in other situations where the tank device
             * will never extract from the tank AND there is no external access for tank input.
             * 
             * Such a device can input to the tank on any thread (typically during server tick) and still 
             * offer the {@link #removeIsReliable()} guarantee because the amount of fluid reported 
             * during a {@link #remove(FabricFluidType, long, VolumeUnit, boolean)} simulation call will
             * never decrease before a non-simulation call happens. (Fluid can only increase off the service thread.)
             * 
             * However, such tanks <em>should</em> implement their own internal synchronization scheme for
             * tank operations if concurrent access is possible.
             */
            boolean addIsReliable();
            
            /**
             * Like {@link #addIsReliable()} but applies to decreases in tank fluid.
             * 
             * As with {@link #addIsReliable()}, this guarantee is generally unnecessary for machine input buffers.
             */
            boolean removeIsReliable();
        }
    }

    /**
     * Tanks can subscribe to a TankManager, forming a Tank Domain.
     * Typically a domain will represent a storage and transport network,
     * but could include a single tank or multiple tanks within the same device.
     * 
     * The TankManager includes an event bus for tanks within the domain and consumes
     * posted events to track domain inventory and tank status efficiently. 
     * Provides performant services for querying, monitoring and transaction management.
     */
    interface OptionE {
        //TODO
    }
    
    /**
     * Devices now have specific connection metadata to give transport mods some love.
     * For example, a pipe mod can test to see if it will be 
     * compatible with a connector on a given block side and if not,
     * avoid rendering the connection. 
     * 
     * The same internal tank may allow different connection types depending on the 
     * side from which it is accessed.
     * 
     * Connections are not explicitly stateful but could be stateful in implementations that want it.
     * Stateful connections could prevent re-testing connection compatibility before each transport 
     * event and simplify tracking of transport network topology.
     * Stateful connections probably make sense for block-type devices. Less so for items, etc.
     */
    interface OptionF {
       // TODO
    }
    
    /**
     * Adds services for routing and transport management within a domain. 
     * Implementations have flexibility in defining constraints and behaviors
     * to create distinct game-play and optimize for different use cases.
     */
    interface OptionG {
        // TODO
     }
    
    /**
     * The system is generalized to address Item and Power storage and transport.
     * Power would support multiple power units / types / constraints, and there
     * are other necessary differences but many of the same interfaces and patterns
     * apply universally.
     */
    interface OptionH {
        // TODO
     }
}
