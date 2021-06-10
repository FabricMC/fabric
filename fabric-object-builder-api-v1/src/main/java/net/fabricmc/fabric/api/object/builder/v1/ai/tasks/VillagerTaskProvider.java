package net.fabricmc.fabric.api.object.builder.v1.ai.tasks;

import com.google.common.collect.*;
import com.mojang.datafixers.util.*;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.passive.*;
import net.minecraft.village.*;
//import org.jetbrains.annotations.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Provides task lists for villager professions. Called when initializing a {@code VillagerEntity}'s {@link net.minecraft.entity.ai.brain.Brain Brain}. Must be registered with {@link VillagerTaskProviderRegistry#register(VillagerProfession, VillagerTaskProvider)} before it can be called.
 * @see VillagerTaskListProvider Vanilla Source
 * @see Task Task&lt;VillagerEntity&gt;
 */
@SuppressWarnings({"unused"})
public final class VillagerTaskProvider {
	public VillagerTaskProvider()
	{
		constantTaskMap = new EnumMap<>(TaskType.class);
		randomTaskMap = new EnumMap<>(TaskType.class);
	}
	
	private final EnumMap<TaskType, List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>>> constantTaskMap;
	private final EnumMap<TaskType, List<BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>>> randomTaskMap;
	
	
	/**
	 * Adds a <b>Constant</b> task under the specified {@link TaskType} for any {@link VillagerProfession VillagerProfession} using this {@code VillagerTaskProvider}.
	 * <p><i>Constant</i> tasks are priorities given to a {@code LivingEntity} which are constantly assessed, whereas <i>Random</i> tasks are selected during the {@code Entity's} lifetime.
	 * <p>Example Usage: <blockquote><pre>addConstantTask(TaskType.CORE, (profession, f) -&gt; Pair.of(1, new VillagerTask(profession, f)));</pre></blockquote>
	 * @see #addRandomTask(TaskType, BiFunction) addRandomTask
	 * @see #addBaseConstantTask(TaskType, BiFunction) addBaseConstantTask
	 * @apiNote {@code PLAY} tasks may only be modified in {@code base} tasks. All calls to this method with {@code TaskType.PLAY} will be redirected to {@link #addBaseConstantTask(TaskType, BiFunction) addBaseConstantTask}.
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * In addition, {@code PLAY} tasks must expect the {@code VillagerProfession} argument to be {@code NULL} when calculated.
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTask {@code BiFunction}, taking the {@code VillagerProfession} of the {@link VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	public VillagerTaskProvider addConstantTask(TaskType taskType, BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
	{
		if (taskType == TaskType.PLAY) {
			addBaseConstantTask(taskType, dynamicTask);
			
			return this;
		}
		
		List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> list = constantTaskMap.get(taskType);
		if (list == null)
			list = new ArrayList<>();
		
		list.add(dynamicTask);
		
		constantTaskMap.put(taskType, list);
		return this;
	}
	
	/**
	 * Adds a <b>Constant</b> task under the specified {@link TaskType} for any {@link VillagerProfession} using this {@code VillagerTaskProvider}.
	 * <p><i>Constant</i> tasks are priorities given to a {@code LivingEntity} which are constantly assessed, whereas <i>Random</i> tasks are selected during the {@code Entity's} lifetime.
	 * <p>Example Usage: <blockquote><pre>addConstantTasks(TaskType.CORE, (profession, f) -&gt; Pair.of(1, new VillagerTask(profession, f)));</pre></blockquote>
	 * @see #addRandomTasks(TaskType, BiFunction[]) addRandomTasks
	 * @see #addBaseConstantTasks(TaskType, BiFunction[]) addBaseConstantTasks
	 * @apiNote {@code PLAY} tasks may only be modified in {@code base} tasks. All calls to this method with {@code TaskType.PLAY} will be redirected to {@link #addBaseConstantTasks(TaskType, BiFunction[]) addBaseConstantTasks}.
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * In addition, {@code PLAY} tasks must expect the {@code VillagerProfession} argument to be {@code NULL} when calculated.
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTasks {@code BiFunctions}, taking the {@code VillagerProfession} of the {@link VillagerEntity VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	@SafeVarargs
	public final VillagerTaskProvider addConstantTasks(TaskType taskType, BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>... dynamicTasks)
	{
		return addConstantTasks(taskType, toListCheckNull(dynamicTasks));
	}
	
	/**
	 * Adds a <b>Constant</b> task under the specified {@link TaskType} for any {@link VillagerProfession} using this {@code VillagerTaskProvider}.
	 * <p><i>Constant</i> tasks are priorities given to a {@code LivingEntity} which are constantly assessed, whereas <i>Random</i> tasks are selected during the {@code Entity's} lifetime.
	 * <p>Example Usage: 
	 * <blockquote><pre>addConstantTasks(TaskType.CORE, (profession, f) -&gt; Pair.of(1, new VillagerTask(profession, f)));</pre></blockquote>
	 * @see #addRandomTasks(TaskType, BiFunction[]) addRandomTasks
	 * @see #addBaseConstantTasks(TaskType, BiFunction[]) addBaseConstantTasks
	 * @apiNote {@code PLAY} tasks may only be modified in {@code base} tasks. All calls to this method with {@code TaskType.PLAY} will be redirected to {@link #addBaseConstantTasks(TaskType, BiFunction[]) addBaseConstantTasks}.
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * In addition, {@code PLAY} tasks must expect the {@code VillagerProfession} argument to be {@code NULL} when calculated.
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTasks A {@code List} of {@code BiFunctions}, taking the {@code VillagerProfession} of the {@link VillagerEntity VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	public final VillagerTaskProvider addConstantTasks(TaskType taskType, List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> dynamicTasks)
	{
		if (taskType == TaskType.PLAY) {
			addBaseConstantTasks(taskType, dynamicTasks);
			
			return this;
		}
		
		List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> list = constantTaskMap.get(taskType);
		if (list == null)
			list = new ArrayList<>();
		
		list.addAll(dynamicTasks);
		
		constantTaskMap.put(taskType, list);
		return this;
	}
	
	
	/**
	 * Adds a <b>Random</b> task under the specified {@link TaskType} for any {@link VillagerProfession VillagerProfession} using this {@code VillagerTaskProvider}.
	 * <p><i>Random</i> tasks are randomly selected and executed during the Villager's lifetime, whereas <i>Constant</i> tasks are priorities that are always active.
	 * <p>Example Usage:
	 * <blockquote><pre>addRandomTask(TaskType.CORE, (profession, f) -&gt; Pair.of(new VillagerTask(profession, f), 1));</pre></blockquote>
	 * @apiNote {@code PLAY} tasks may only be modified in {@code base} tasks. All calls to this method with {@code TaskType.PLAY} will be redirected to {@link #addBaseConstantTask(TaskType, BiFunction) addBaseConstantTask}.
	 * @implNote {@code Task}s are added to a single {@code List} passed to the {@link RandomTask RandomTask} constructor.  If the {@code TaskType} does not include a {@code RandomTask} by default, one will be added.
	 * @see #addConstantTask(TaskType, BiFunction) addConstantTask
	 * @see #addBaseRandomTask(TaskType, BiFunction) addBaseRandomTask
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * In addition, {@code PLAY} tasks must expect the {@code VillagerProfession} argument to be {@code NULL} when calculated.
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTask {@code BiFunction}, taking the {@code VillagerProfession} of the {@link VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	public final VillagerTaskProvider addRandomTask(TaskType taskType, BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>> dynamicTask)
	{
		if (taskType == TaskType.PLAY) {
			addBaseRandomTask(taskType, dynamicTask);
			return this;
		}
		List<BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>> list = randomTaskMap.get(taskType);
		if (list == null)
			list = new ArrayList<>();
		
		list.add(dynamicTask);
		
		randomTaskMap.put(taskType, list);
		
		return this;
	}
	
	/**
	 * Adds a <b>Random</b> task under the specified {@link TaskType} for any {@link VillagerProfession VillagerProfession} using this {@code VillagerTaskProvider}.
	 * <p><i>Random</i> tasks are randomly selected and executed during the Villager's lifetime, whereas <i>Constant</i> tasks are priorities that are always active.
	 * <p>Example Usage:
	 * <blockquote><pre>addRandomTasks(TaskType.CORE, (profession, f) -&gt; Pair.of(new VillagerTask(profession, f), 1));</pre></blockquote>
	 * @apiNote {@code PLAY} tasks may only be modified in {@code base} tasks. All calls to this method with {@code TaskType.PLAY} will be redirected to {@link #addBaseRandomTasks(TaskType, BiFunction[]) addBaseRandomTasks}.
	 * @implNote {@code Task}s are added to a single {@code List} passed to the {@link RandomTask RandomTask} constructor.  If the {@code TaskType} does not include a {@code RandomTask} by default, one will be added.
	 * @see #addConstantTasks(TaskType, BiFunction[]) addConstantTasks
	 * @see #addBaseRandomTasks(TaskType, BiFunction[]) addBaseRandomTasks
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * In addition, {@code PLAY} tasks must expect the {@code VillagerProfession} argument to be {@code NULL} when calculated.
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTasks {@code BiFunctions}, taking the {@code VillagerProfession} of the {@link VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	@SafeVarargs
	public final VillagerTaskProvider addRandomTasks(TaskType taskType, BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>... dynamicTasks)
	{
		return addRandomTasks(taskType, toListCheckNull(dynamicTasks));
	}
	
	/**
	 * Adds a <b>Random</b> task under the specified {@link TaskType} for any {@link VillagerProfession VillagerProfession} using this {@code VillagerTaskProvider}.
	 * <p><i>Random</i> tasks are randomly selected and executed during the Villager's lifetime, whereas <i>Constant</i> tasks are priorities that are always active.
	 * <p>Example Usage:
	 * <blockquote><pre>addRandomTasks(TaskType.CORE, (profession, f) -&gt; Pair.of(new VillagerTask(profession, f), 1));</pre></blockquote>
	 * @apiNote {@code PLAY} tasks may only be modified in {@code base} tasks. All calls to this method with {@code TaskType.PLAY} will be redirected to {@link #addBaseRandomTasks(TaskType, BiFunction[]) addBaseRandomTasks}.
	 * @implNote {@code Task}s are added to a single {@code List} passed to the {@link RandomTask RandomTask} constructor.  If the {@code TaskType} does not include a {@code RandomTask} by default, one will be added.
	 * @see #addConstantTasks(TaskType, List) addConstantTasks
	 * @see #addBaseRandomTasks(TaskType, List) addBaseRandomTasks
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * In addition, {@code PLAY} tasks must expect the {@code VillagerProfession} argument to be {@code NULL} when calculated.
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTasks A {@code List} of {@code BiFunctions}, taking the {@code VillagerProfession} of the {@link VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	public final VillagerTaskProvider addRandomTasks(TaskType taskType, List<BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>> dynamicTasks)
	{
		if (taskType == TaskType.PLAY) {
			addBaseRandomTasks(taskType, dynamicTasks);
			
			return this;
		}
		
		List<BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>> list = randomTaskMap.get(taskType);
		if (list == null)
			list = new ArrayList<>();
		
		list.addAll(dynamicTasks);
		
		randomTaskMap.put(taskType, list);
		return this;
	}
	
	
	
	
	
	
	public List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getConstantTasks(TaskType taskType, VillagerProfession villagerProfession, float f)
	{
		return applyToTasks(constantTaskMap.get(taskType), villagerProfession, f);
	}
	
	public List<Pair<Task<? super VillagerEntity>, Integer>> getRandomTasks(TaskType type, VillagerProfession profession, float f)
	{
		return applyToTasks(randomTaskMap.get(type), profession, f);
	}
	
	
	
	
	
	
	
	
	
	
	
	private static final EnumMap<TaskType, List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>>> baseConstantTaskMap;
	private static final EnumMap<TaskType, List<BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>>> baseRandomTaskMap;
	
	
	/**
	 * Adds a <b>Constant</b> task under the specified {@link TaskType} for <i>all</i> {@link VillagerEntity VillagerEntities}, regardless of {@link VillagerProfession}.
	 * <p><i>Constant</i> tasks are priorities given to a {@code LivingEntity} which are constantly assessed, whereas <i>Random</i> tasks are selected during the {@code Entity's} lifetime.
	 * <p>Example Usage: <blockquote><pre>addBaseConstantTask(TaskType.CORE, (profession, f) -&gt; Pair.of(1, new VillagerTask(profession, f)));</pre></blockquote>
	 * @see #addBaseRandomTask(TaskType, BiFunction) addBaseRandomTask
	 * @see #addConstantTask(TaskType, BiFunction) addConstantTask
	 * @apiNote {@code PLAY} tasks may only be modified here.
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * In addition, {@code PLAY} tasks must expect the {@code VillagerProfession} argument to be {@code NULL} when calculated.
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTask {@code BiFunction}, taking the {@code VillagerProfession} of the {@link VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	public static void addBaseConstantTask(TaskType taskType, BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>> dynamicTask)
	{
		List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> list = baseConstantTaskMap.get(taskType);
		if (list == null)
			list = new ArrayList<>();
		
		list.add(dynamicTask);
		
		baseConstantTaskMap.put(taskType, list);
	}
	
	
	
	
	
	/**
	 * Adds a <b>Constant</b> task under the specified {@link TaskType} for <i>all</i> {@link VillagerEntity VillagerEntities}, regardless of {@link VillagerProfession}.
	 * <p><i>Constant</i> tasks are priorities given to a {@code LivingEntity} which are constantly assessed, whereas <i>Random</i> tasks are selected during the {@code Entity's} lifetime.
	 * <p>Example Usage: <blockquote><pre>addBaseConstantTasks(TaskType.CORE, (profession, f) -&gt; Pair.of(1, new VillagerTask(profession, f)));</pre></blockquote>
	 * @see #addBaseRandomTasks(TaskType, BiFunction[]) addBaseRandomTasks
	 * @see #addConstantTasks(TaskType, BiFunction[]) addConstantTasks
	 * @apiNote {@code PLAY} tasks may only be modified here.
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * In addition, {@code PLAY} tasks must expect the {@code VillagerProfession} argument to be {@code NULL} when calculated.
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTasks {@code BiFunctions}, taking the {@code VillagerProfession} of the {@link VillagerEntity VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	@SafeVarargs
	public static void addBaseConstantTasks(TaskType taskType, BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>... dynamicTasks)
	{
		addBaseConstantTasks(taskType, toListCheckNull(dynamicTasks));
	}
	
	/**
	 * Adds a <b>Constant</b> task under the specified {@link TaskType} for <i>all</i> {@link VillagerEntity VillagerEntities}, regardless of {@link VillagerProfession}.
	 * <p><i>Constant</i> tasks are priorities given to a {@code LivingEntity} which are constantly assessed, whereas <i>Random</i> tasks are selected during the {@code Entity's} lifetime.
	 * <p>Example Usage: <blockquote><pre>addBaseConstantTask(TaskType.CORE, (profession, f) -&gt; Pair.of(1, new VillagerTask(profession, f)));</pre></blockquote>
	 * @see #addBaseRandomTasks(TaskType, List) addBaseRandomTasks
	 * @see #addConstantTasks(TaskType, List) addConstantTasks
	 * @apiNote {@code PLAY} tasks may only be modified here.
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * In addition, {@code PLAY} tasks must expect the {@code VillagerProfession} argument to be {@code NULL} when calculated.
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTasks {@code BiFunction}, taking the {@code VillagerProfession} of the {@link VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	public static void addBaseConstantTasks(TaskType taskType, List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> dynamicTasks)
	{
		List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> list = baseConstantTaskMap.get(taskType);
		if (list == null)
			list = new ArrayList<>();
		
		list.addAll(dynamicTasks);
		
		baseConstantTaskMap.put(taskType, list);
	}
	
	
	/**
	 * Adds a <b>Random</b> task under the specified {@link TaskType} for <i>all</i> {@link VillagerEntity VillagerEntities}, regardless of {@link VillagerProfession}.
	 * <p><i>Random</i> tasks are randomly selected and executed during the Villager's lifetime, whereas <i>Constant</i> tasks are priorities that are always active.
	 * <p>Example Usage:
	 * <blockquote><pre>addBaseRandomTask(TaskType.CORE, (profession, f) -&gt; Pair.of(new VillagerTask(profession, f), 1));</pre></blockquote>
	 * @apiNote {@code PLAY} tasks may only be modified here.
	 * @implNote {@code Task}s are added to a single {@code List} passed to the {@link RandomTask RandomTask} constructor.  If the {@code TaskType} does not include a {@code RandomTask} by default, one will be added.
	 * @see #addBaseConstantTask(TaskType, BiFunction) addBaseConstantTask
	 * @see #addRandomTask(TaskType, BiFunction) addRandomTask
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * In addition, {@code PLAY} tasks must expect the {@code VillagerProfession} argument to be {@code NULL} when calculated.
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTask {@code BiFunction}, taking the {@code VillagerProfession} of the {@link VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	public static void addBaseRandomTask(TaskType taskType, BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>> dynamicTask)
	{
		List<BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>> list = baseRandomTaskMap.get(taskType);
		if (list == null)
			list = new ArrayList<>();
		
		list.add(dynamicTask);
		
		baseRandomTaskMap.put(taskType, list);
	}
	
	
	
	
	
	
	
	/**
	 * Adds a <b>Random</b> task under the specified {@link TaskType} for <i>all</i> {@link VillagerEntity VillagerEntities}, regardless of {@link VillagerProfession}.
	 * <p><i>Random</i> tasks are randomly selected and executed during the Villager's lifetime, whereas <i>Constant</i> tasks are priorities that are always active.
	 * <p>Example Usage:
	 * <blockquote><pre>addBaseRandomTasks(TaskType.CORE, (profession, f) -&gt; Pair.of(new VillagerTask(profession, f), 1));</pre></blockquote>
	 * @apiNote {@code PLAY} tasks may only be modified here.
	 * @implNote {@code Task}s are added to a single {@code List} passed to the {@link RandomTask RandomTask} constructor.  If the {@code TaskType} does not include a {@code RandomTask} by default, one will be added.
	 * @see #addBaseConstantTasks(TaskType, BiFunction[]) addBaseConstantTasks
	 * @see #addRandomTasks(TaskType, BiFunction[]) addRandomTask
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTasks {@code BiFunctions}, taking the {@code VillagerProfession} of the {@link VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	@SafeVarargs
	public static void addBaseRandomTasks(TaskType taskType, BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>... dynamicTasks)
	{
		addBaseRandomTasks(taskType, toListCheckNull(dynamicTasks));
	}
	
	/**
	 * Adds a <b>Random</b> task under the specified {@link TaskType} for <i>all</i> {@link VillagerEntity VillagerEntities}, regardless of {@link VillagerProfession}.
	 * <p><i>Random</i> tasks are randomly selected and executed during the Villager's lifetime, whereas <i>Constant</i> tasks are priorities that are always active.
	 * <p>Example Usage:
	 * <blockquote><pre>addBaseRandomTasks(TaskType.CORE, (profession, f) -&gt; Pair.of(new VillagerTask(profession, f), 1));</pre></blockquote>
	 * @apiNote {@code PLAY} tasks may only be modified here.
	 * @implNote {@code Task}s are added to a single {@code List} passed to the {@link RandomTask RandomTask} constructor.  If the {@code TaskType} does not include a {@code RandomTask} by default, one will be added.
	 * @see #addBaseConstantTasks(TaskType, BiFunction[]) addBaseConstantTasks
	 * @see #addRandomTasks(TaskType, BiFunction[]) addRandomTask
	 * @implSpec {@code BiFunction}s must return a {@link Pair} containing the <b>Task</b> and its <b>Weight</b> (as used in a {@link net.minecraft.util.collection.WeightedList WeightedList}).
	 * @param taskType The {@code TaskType} of the specified task.
	 * @param dynamicTasks {@code List} of {@code BiFunctions}, taking the {@code VillagerProfession} of the {@link VillagerEntity} executing the task and the <b>walk speed</b> of the villager as arguments.
	 */
	public static void addBaseRandomTasks(TaskType taskType, List<BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>> dynamicTasks)
	{
		List<BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>> list = baseRandomTaskMap.get(taskType);
		if (list == null)
			list = new ArrayList<>();
		
		list.addAll(dynamicTasks);
		
		baseRandomTaskMap.put(taskType, list);
	}
	
	
	static {
		baseConstantTaskMap = new EnumMap<>(TaskType.class);
		baseRandomTaskMap = new EnumMap<>(TaskType.class);
	}
	
	
	
	
	
	
	
	public static List<Pair<Task<? super VillagerEntity>, Integer>> getBaseRandomTasks(TaskType taskType, /* @Nullable */ VillagerProfession profession, float f)
	{
		return applyToTasks(baseRandomTaskMap.get(taskType), profession, f);
	}
	
	
	public static List<Pair<Integer, ? extends Task<? super VillagerEntity>>> getBaseConstantTasks(TaskType taskType, /* @Nullable */ VillagerProfession profession, float f)
	{
		return applyToTasks(baseConstantTaskMap.get(taskType), profession, f);
	}
	
	
	
	
	
	
	public boolean hasRandomTasks(/* @NotNull */ TaskType taskType)
	{
		return randomTaskMap.containsKey(taskType);
	}
	
	public boolean hasConstantTasks(/* @NotNull */ TaskType taskType)
	{
		return constantTaskMap.containsKey(taskType);
	}
	
	public static boolean hasBaseConstantTasks(/* @NotNull */ TaskType taskType)
	{
		return baseConstantTaskMap.containsKey(taskType);
	}
	
	public static boolean hasBaseRandomTasks(/* @NotNull */ TaskType taskType)
	{
		return baseRandomTaskMap.containsKey(taskType);
	}
	
	public List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> getRawConstantTasks(TaskType taskType)
	{
		return constantTaskMap.get(taskType);
	}
	
	public List<BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>> getRawRandomTasks(TaskType taskType)
	{
		return randomTaskMap.get(taskType);
	}
	
	public static List<BiFunction<VillagerProfession, Float, Pair<Integer, ? extends Task<? super VillagerEntity>>>> getRawBaseConstantTasks(TaskType taskType)
	{
		return baseConstantTaskMap.get(taskType);
	}
	
	public static List<BiFunction<VillagerProfession, Float, Pair<Task<? super VillagerEntity>, Integer>>> getRawBaseRandomTasks(TaskType taskType)
	{
		return baseRandomTaskMap.get(taskType);
	}
	
	
	
	
	
	
	
	
	/*
	 * Helper functions
	 */
	/* @NotNull */
	private static <T> List<T> toListCheckNull(T[] arr)
	{
		if (arr == null)
			return ImmutableList.of();
		
		return Arrays.asList(arr);
	}
	
	private static <T> List<T> applyToTasks(List<BiFunction<VillagerProfession, Float, T>> l, final VillagerProfession p, final float f)
	{
		if (l == null)
			return ImmutableList.of();
		
		return l.stream().map(el -> el.apply(p, f)).collect(Collectors.toList());
	}
	
	private static <T> List<BiFunction<VillagerProfession, Float, T>> mapToBiFunction(final List<T> in)
	{
		final List<BiFunction<VillagerProfession, Float, T>> out = new ArrayList<>();
		
		for (final T t : in) {
			if (t == null) {
				continue;
			}
			
			out.add((p, f) -> t);
		}
		
		return out;
	}
	
	private static <T> List<BiFunction<VillagerProfession, Float, T>> mapToBiFunction(final T[] in)
	{
		return mapToBiFunction(toListCheckNull(in));
	}
	
	
	/**
	 * The type of {@code Task} being added.
	 * @see TaskType#CORE
	 * @see TaskType#WORK
	 * @see TaskType#PLAY
	 * @see TaskType#REST
	 * @see TaskType#MEET
	 * @see TaskType#IDLE
	 * @see TaskType#PANIC
	 * @see TaskType#PRERAID
	 * @see TaskType#RAID
	 * @see TaskType#HIDE
	 */
	public enum TaskType {
		/**
		 * {@code CORE} tasks are priorities given to the {@code VillagerEntity}.
		 * <p>Includes tasks such as:
		 * <ul>
		 *     <li>{@link StayAboveWaterTask Stay above water},</li>
		 *     <li>{@link OpenDoorsTask Open doors}, and</li>
		 *     <li>{@link LookAroundTask Look around}.</li>
		 * </ul>
		 * @see VillagerTaskListProvider#createCoreTasks(VillagerProfession, float) Vanilla Code Source
		 */
		CORE,
		
		/**
		 * {@code WORK} tasks are executed when a {@code VillagerProfession} is near its secondary worksite.
		 * <p>Includes tasks such as:
		 * <ul>
		 *     <li>{@link FarmerVillagerTask Farmer breaking crops},</li>
		 *     <li>{@link HoldTradeOffersTask Hold trade offers}, and</li>
		 *     <li>{@link GoToNearbyPositionTask Go to jobsite}.</li>
		 * </ul>
		 * @see WorkerVillagerTask Implementation Framework (optional)
		 * @see VillagerTaskListProvider#createWorkTasks(VillagerProfession, float) Vanilla Code Source
		 */
		WORK,
		
		/**
		 * {@code REST} tasks are executed as nighttime begins.
		 * <p>Includes tasks such as:
		 * <ul>
		 *     <li>{@link VillagerWalkTowardsTask Go home},</li>
		 *     <li>{@link SleepTask Sleep}, and</li>
		 *     <li>{@link WanderIndoorsTask Wander indoors}.</li>
		 * </ul>
		 * @see VillagerTaskListProvider#createRestTasks(VillagerProfession, float) Vanilla Code Source
		 */
		REST,
		
		/**
		 * {@code MEET} tasks are executed at midday, when all villagers gather in the center of their village.
		 * <p>Includes tasks such as:
		 * <ul>
		 *     <li>{@link FindInteractionTargetTask Interact with player},</li>
		 *     <li>{@link GatherItemsVillagerTask Acquire food from {@code Farmer} Villagers}, and</li>
		 *     <li>{@link VillagerWalkTowardsTask Walk to meeting point}.</li>
		 * </ul>
		 * @see VillagerTaskListProvider#createMeetTasks(VillagerProfession, float) Vanilla Code Source
		 */
		MEET,
		
		/**
		 * {@code IDLE} tasks are executed to make Villagers seek out additional tasks.
		 * <p>Includes tasks such as:
		 * <ul>
		 *     <li>{@link FindEntityTask Find entities nearby},</li>
		 *     <li>{@link FindInteractionTargetTask Find interaction target}, and</li>
		 *     <li>{@link JumpInBedTask Babies jumping in bed}.</li>
		 * </ul>
		 * @see VillagerTaskListProvider#createIdleTasks(VillagerProfession, float) Vanilla Code Source
		 */
		IDLE,
		
		/**
		 * {@code PANIC} tasks are executed when a {@code VillagerEntity} is hurt.
		 * <p>Includes tasks such as:
		 * <ul>
		 *     <li>{@code Escape from nearest hostile}, and</li>
		 *     <li>{@code Escape from attacker}.</li>
		 * </ul>
		 * @see VillagerTaskListProvider#createPanicTasks(VillagerProfession, float) Vanilla Code Source
		 */
		PANIC,
		
		/**
		 * {@code PRERAID} tasks are executed immediately before an {@link net.minecraft.entity.mob.IllagerEntity Illager} {@link net.minecraft.village.raid.Raid Raid}.
		 * <p>Includes tasks such as:
		 * <ul>
		 *     <li>{@link RingBellTask Ring bell}, and</li>
		 *     <li>{@link FindWalkTargetTask Run around}.</li>
		 * </ul>
		 * @see VillagerTaskListProvider#createPreRaidTasks(VillagerProfession, float) Vanilla Code Source
		 */
		PRERAID,
		
		/**
		 * {@code Raid} tasks are generated during an {@link net.minecraft.entity.mob.IllagerEntity Illager} {@link net.minecraft.village.raid.Raid Raid}.
		 * <p>Includes tasks such as:
		 * <ul>
		 *     <li>{@link HideInHomeDuringRaidTask Stay in home during raid},</li>
		 *     <li>{@link SeekSkyAfterRaidWinTask Seek sky}, executed when {@code Raid} is complete, and</li>
		 *     <li>{@link CelebrateRaidWinTask Celebrate raid win}, executed when {@code Raid} is complete.</li>
		 * </ul>
		 * @see VillagerTaskListProvider#createRaidTasks(VillagerProfession, float) Vanilla Code Source
		 */
		RAID,
		
		/**
		 * {@code HIDE} tasks are executed immediately prior to and during an {@link net.minecraft.entity.mob.IllagerEntity Illager} {@link net.minecraft.village.raid.Raid Raid}.
		 * <p>Includes tasks such as:
		 * <ul>
		 *     <li>{@link ForgetBellRingTask Forget ring bell task}, and</li>
		 *     <li>{@link HideInHomeTask Hide inside home}.</li>
		 * </ul>
		 * @see VillagerTaskListProvider#createHideTasks(VillagerProfession, float) Vanilla Code Source
		 */
		HIDE,
		
		
		/**
		 * {@code PLAY} tasks are executed by "baby" Villagers.
		 * <p>Includes tasks such as:
		 * <ul>
		 *     <li>{@link JumpInBedTask Jump in bed},</li>
		 *     <li>{@link FindEntityTask Follow villagers and cats}, and</li>
		 *     <li>{@link PlayWithVillagerBabiesTask Play with other "baby" Villagers}.</li>
		 * </ul>
		 * @see VillagerTaskListProvider#createPlayTasks(float) Vanilla Code Source
		 * @apiNote {@code PLAY} tasks may only be added in the base tasks, due to the lack of a {@link VillagerProfession} to map to.
		 */
		PLAY
	}
}
