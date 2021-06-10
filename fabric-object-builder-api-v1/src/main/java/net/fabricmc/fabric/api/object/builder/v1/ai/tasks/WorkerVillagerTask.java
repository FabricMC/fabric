package net.fabricmc.fabric.api.object.builder.v1.ai.tasks;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
//import org.jetbrains.annotations.Contract;
//import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A framework for creating simple world-affecting {@code VillagerTask}s, including pre-made functions for scanning for targets, etc.
 * @apiNote Tasks may also extend {@code Task<VillagerEntity>}.
 * @see FarmerVillagerTask FarmerVillagerTask
 * @see net.minecraft.entity.ai.brain.task.BoneMealTask BoneMealTask
 * @see Task
 * @see VillagerTaskProviderRegistry
 */
public abstract class WorkerVillagerTask extends Task<VillagerEntity> {
    /* @Nullable */
    protected BlockPos currentTarget;
    protected long nextResponseTime;
    protected int ticksRan;
    
    protected List<BlockPos> targetPositions = Lists.newArrayList();
    
    public WorkerVillagerTask() {
        super(ImmutableMap.of(
                MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT,
                MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
        LogManager.getLogger().log(Level.INFO, "Task initialized");
    }
    
    @SuppressWarnings("unused")
    public WorkerVillagerTask(ImmutableMap<MemoryModuleType<?>, MemoryModuleState> memoryMap)
    {
        super(memoryMap);
    }
    
    /**
     * Checks if the block at this position satisfies interaction requirements.<p>
     * 
     * Example: the Farmer {@link VillagerProfession} checks for one of two things:
     * <ol>
     *     <li>For harvesting: if the block is a crop and is mature,</li>
     *      <li>For planting: if the targeted block is air and the block below it is farmland.</li>
     * </ol>
     * Thus, the Farmer's implementation of this method would be as follows:
     * <blockquote><pre>protected boolean isSuitableTarget(BlockPos pos, ServerWorld world) {
     *      BlockState blockState = world.getBlockState(pos);
     *      Block block = blockState.getBlock();
     *      Block block2 = world.getBlockState(pos.down()).getBlock();
     *      return (block instanceof CropBlock) &amp;&amp; ((CropBlock)block).isMature(blockState)
     *             || (blockState.isAir() &amp;&amp; block2 instanceof FarmlandBlock);
     * }</pre></blockquote>
     * 
     * @param pos The position of the block in the world.
     * @param world The serverworld in which the block exists.
     * @return {@code true} if the block at that position satisfies the requirements to be interacted with.
     */
    /* @Contract(pure=true) */
    protected abstract boolean isSuitableTarget(BlockPos pos, ServerWorld world);

    /**
     * The conditions for this task running, e.g. daylight level, villager age, profession, etc.<p>
     * 
     * Example:
     * <pre>protected boolean checkRunConditions(ServerWorld serverWorld, VillagerEntity villagerEntity) {
     *      return villagerEntity.getVillagerData().getProfession() == VillagerProfession.FARMER;
     * }</pre>
     * 
     * @param serverWorld the current server world.
     * @param villagerEntity the {@link VillagerEntity} currently attempting to execute this task.
     * @return {@code true} if this task should run with the given environment.
     */
    /* @Contract(pure=true) */
    protected abstract boolean checkRunConditions(ServerWorld serverWorld, VillagerEntity villagerEntity);
    
    /**
     * If this task should run only if the {@code doMobGriefing} game-rule is enabled.<p>
     * Generally necessary for any tasks that break blocks, but not required if they only modify {@code BlockState}s.<p>
     * 
     * Example: {@link FarmerVillagerTask} needs to break crops to harvest them, so its implementation would return {@code true}.<p>
     * @return {@code true} or {@code false}.
     */
    /* @Contract(pure=true) */
    protected abstract boolean doesMobGriefing();
    
    
    /**
     * The world action to be performed on the currently-targeted block position.
     * <p>
     * @param currentTarget The {@link BlockPos} of the currently-targeted block.
     * @param serverWorld The {@link ServerWorld} the block is located in.
     * @param villagerEntity The {@link VillagerEntity} acting upon this block.
     * @param startTick The game tick that the task began.
     */
    /* @Contract(mutates="this, param2, param3") */
    protected abstract void doWorldActions(BlockPos currentTarget, ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick);
    
    
    /**
	 * @return The duration, in game ticks, that the task should run for.
	 */
    /* @Contract(pure=true) */
    protected abstract int getDuration();
    
    
    /**
     * Checks if this task should be executed by the villager who randomly chose it.
     * @apiNote Should avoid overriding this method, instead using {@link WorkerVillagerTask#checkRunConditions}.
     * @see Task#shouldRun
     * @param serverWorld The {@code ServerWorld} this task is being executed in.
     * @param villagerEntity The {@code VillagerEntity} attempting to execute the task.
     * @return {@code true} if this task should run.
     * @implNote Implementations of this method should also set the current target via side-effect.
     */
    /* @Contract(mutates="this, param1") */
    @Override
    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if ((doesMobGriefing() && serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING))
                || !checkRunConditions(serverWorld, villagerEntity))
            return false;
        else {
            setCurrentTarget(serverWorld, villagerEntity);
            return this.currentTarget != null;
        }
    }
    
    /**
	 * Clears all potential targets stored, scans for new potential targets, then selects one randomly as the current target.
     * @apiNote Programs should not override this method, instead overriding {@link WorkerVillagerTask#chooseRandomTarget}.
     * @param serverWorld The {@code ServerWorld} this task is being executed in.
     * @param villagerEntity The {@code VillagerEntity} currently executing this task.
     */
    /* @Contract(mutates="this") */
    protected void setCurrentTarget(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        this.targetPositions.clear();
        
        this.getTargetsInRange(serverWorld, villagerEntity);
        
        this.currentTarget = this.chooseRandomTarget(serverWorld);
    }
    
    protected void getTargetsInRange(ServerWorld serverWorld, VillagerEntity villagerEntity)
    {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) 
                {
                    mutable.set(villagerEntity.getBlockPos(), i, j, k);
                    
                    if (this.isSuitableTarget(mutable, serverWorld)) 
                        addPotentialTarget(mutable.toImmutable());
                    
                }
            }
        }
    }
    
    /**
     * The mechanism for saving potential {@link BlockPos} target positions.
     * @implNote The only reason to override this method is to use an alternative storage method, as with {@link net.minecraft.entity.ai.brain.task.BoneMealTask BoneMealTask}.
     * @param pos The potential target position.
     */
    protected void addPotentialTarget(BlockPos pos) {
        this.targetPositions.add(pos);
    }
    
    /**
     * 
     * @param world The server world being scanned.
     * @return The {@link BlockPos} that should be chosen as a target.
     */
    /* @Contract(pure=true) */
    /* @Nullable */
    protected BlockPos chooseRandomTarget(ServerWorld world) {
        if (this.targetPositions.isEmpty())
            return null;
        else
            return this.targetPositions.get(world.getRandom().nextInt(this.targetPositions.size()));
    }
    
    /**
     * Called to execute the main task.
     * @param serverWorld The world this task is being executed in.
     * @param villagerEntity The {@link VillagerEntity} executing this task.
     * @param startTick The game tick this task began on.
     */
    /* @Contract(mutates="this, param1, param2") */
    @Override
    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick) {
        if (startTick > this.nextResponseTime && this.currentTarget != null) {
            addLookWalkTarget(villagerEntity, this.currentTarget);
        }
    }
    
    /**
     * Called to finish up the current task.
     * @param serverWorld The world this task is being executed in.
	 * @param villagerEntity The {@link VillagerEntity} executing this task.
	 * @param startTick The game tick this task began on.
     */
    /* @Contract(mutates="this, param1, param2") */
    @Override
    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick) {
        forgetLookWalkTarget(villagerEntity);
        this.ticksRan = 0;
        this.nextResponseTime = startTick + getEndDelay();
    }
    
    /**
     * Adds look/walk target to the {@link VillagerEntity}'s {@link net.minecraft.entity.ai.brain.Brain Brain}.
     * @param villagerEntity The {@code VillagerEntity} to add the look/walk target to.
     * @param target The target {@code BlockPos}.
     */
    /* @Contract(mutates="param1") */
    protected void addLookWalkTarget(final VillagerEntity villagerEntity, final BlockPos target)
    {
        final BlockPosLookTarget lookTarget = new BlockPosLookTarget(target);
        villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, (new WalkTarget(lookTarget, 0.5F, 1)));
        villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, (lookTarget));
    }
    
    /**
     * Makes the {@code VillagerEntity} forget its Look Target and Walk Target.
     * @param villagerEntity The {@code VillagerEntity} that will forget its look/walk target.
     */
    /* @Contract(mutates="param") */
    protected void forgetLookWalkTarget(VillagerEntity villagerEntity)
    {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
    }
	
	
	/**
     * Called to perform the task's main action.
     * @implNote {@code currentTarget} should be set in {@link WorkerVillagerTask#shouldRun}, and implementations of this method should assume {@code currentTarget} has already been set. This is to prevent the task running when no suitable target is within range.
     * @param serverWorld The world that this task is being executed in.
     * @param villagerEntity The {@code VillagerEntity} executing this task.
     * @param startTick The game tick the task was started on.
     */
	/* @Contract(mutates="this, param1, param2") */
    @Override
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long startTick) {
        if (this.currentTarget == null || this.currentTarget.isWithinDistance(villagerEntity.getPos(), 1.0D)) {
            if (this.currentTarget != null && startTick > this.nextResponseTime) {
                doWorldActions(currentTarget, serverWorld, villagerEntity, startTick);
            }

            ++this.ticksRan;
        }
    }
    
    /**
     * 
     * @return the number of game ticks before the next random target should be chosen.
     */
    /* @Contract(pure=true) */
    protected long getEndDelay()
    {
        return 40L;
    }
    
    
    
    
    /* @Contract(pure=true) */
    @Override
    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        return this.ticksRan < getDuration();
    }
    
    
    
    
}

