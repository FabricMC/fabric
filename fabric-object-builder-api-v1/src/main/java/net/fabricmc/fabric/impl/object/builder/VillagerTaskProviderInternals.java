package net.fabricmc.fabric.impl.object.builder;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.object.builder.v1.ai.tasks.*;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
//import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.fabricmc.fabric.api.object.builder.v1.ai.tasks.VillagerTaskProvider.TaskType.PLAY;


public class VillagerTaskProviderInternals {
	
	private static final Map<VillagerProfession, VillagerTaskProvider> villagerTaskProviderMap;
	
	private static final VillagerTaskProvider defaultTaskProvider = new VillagerTaskProvider();
	
	private static VillagerTaskProvider getTaskProvider(VillagerProfession profession)
	{
		assert(profession != null);
		
		if (profession == VillagerProfession.NONE)
			return defaultTaskProvider;
		
		final VillagerTaskProvider vtp = villagerTaskProviderMap.get(profession);
		if (vtp == null)
			return defaultTaskProvider;
		else
			return vtp;
	}
	
	static {
		villagerTaskProviderMap = VillagerTaskProviderRegistry.getCompletedMap();
	}
	
	
	
	public static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getConstantTasks(VillagerTaskProvider.TaskType taskType, /* @Nullable */ VillagerProfession profession, float f)
	{
		assert(!(profession == null && taskType != PLAY)); // The only hardcoding in this whole thing, because PLAY is an outlier.
		
		ImmutableList.Builder<Pair<Integer, ? extends Task<? super VillagerEntity>>> out = ImmutableList.builder();
		
		out.addAll(VillagerTaskProvider.getBaseConstantTasks(taskType, profession, f));
		
		if (profession != VillagerProfession.NONE && taskType != PLAY)
			out.addAll(getTaskProvider(profession).getConstantTasks(taskType, profession, f));
		
		return out.build();
	}
	
//	private static ImmutableList<Pair<Integer, ? extends Task<? super VillagerEntity>>> getVanillaTasks(VillagerTaskProvider.TaskType taskType, VillagerProfession p, float f)
//	{
//		switch (taskType)
//		{
//			case CORE:
//				return VillagerTaskListProvider.createCoreTasks(p, f);
//			case WORK:
//				return VillagerTaskListProvider.createWorkTasks(p, f);
//			case PLAY:
//				return VillagerTaskListProvider.createPlayTasks(f);
//			case MEET:
//				return VillagerTaskListProvider.createMeetTasks(p, f);
//			case IDLE:
//				return VillagerTaskListProvider.createIdleTasks(p, f);
//			case PANIC:
//				return VillagerTaskListProvider.createPanicTasks(p, f);
//			case PRERAID:
//				return VillagerTaskListProvider.createPreRaidTasks(p, f);
//			case RAID:
//				return VillagerTaskListProvider.createRaidTasks(p, f);
//			case HIDE:
//				return VillagerTaskListProvider.createHideTasks(p, f);
//			case REST:
//				return VillagerTaskListProvider.createRestTasks(p, f);
//			default:
//				throw new AssertionError("Invalid VillagerTaskProvider.TaskType provided: " + taskType);
//		}
//	}
	
	
	
	// These are injected directly into the random task list, so there's no need to worry about switch cases.
	public static ImmutableList<Pair<Task<? super VillagerEntity>, Integer>> getRandomTasks(VillagerTaskProvider.TaskType taskType, /* @Nullable */ VillagerProfession p, float f)
	{
		ImmutableList.Builder<Pair<Task<? super VillagerEntity>, Integer>> out = ImmutableList.builder();
		out.addAll(VillagerTaskProvider.getBaseRandomTasks(taskType, p, f));
		
		if (taskType != PLAY) 
			out.addAll(getTaskProvider(p).getRandomTasks(taskType, p, f));
		
		return out.build();
	}
	
	
	
	
	public static boolean hasCustomRandomTasks(VillagerTaskProvider.TaskType taskType, VillagerProfession villagerProfession)
	{
		return getTaskProvider(villagerProfession).hasRandomTasks(taskType) || VillagerTaskProvider.hasBaseRandomTasks(taskType);
	}
	
	public static boolean hasCustomRandomTasks(VillagerTaskProvider.TaskType taskType)
	{
		return VillagerTaskProvider.hasBaseRandomTasks(taskType);
	}
	
//	public static boolean baseHasRandomTasks(VillagerTaskProvider.TaskType taskType)
//	{
//		return VillagerTaskProvider.hasBaseRandomTasks(taskType);
//	}
}
