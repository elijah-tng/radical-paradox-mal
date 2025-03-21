/*
 * Copyright (C) 2020 ActiveJ LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tripleo.elijah_prolific.comp_signals.io_activej_trigger;

import io.activej.common.builder.AbstractBuilder;
import io.activej.common.initializer.Initializer;
import io.activej.inject.Injector;
import io.activej.inject.Key;
import io.activej.inject.annotation.Provides;
import io.activej.inject.annotation.ProvidesIntoSet;
import io.activej.inject.binding.Binding;
import io.activej.inject.binding.BindingType;
import io.activej.inject.binding.OptionalDependency;
import io.activej.inject.module.AbstractModule;
import io.activej.launcher.LauncherService;
import io.activej.worker.WorkerPool;
import io.activej.worker.WorkerPools;
import tripleo.elijah_prolific.comp_signals.io_activej_trigger.util.KeyWithWorkerData;
import tripleo.elijah_prolific.comp_signals.io_activej_trigger.util.Utils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.activej.trigger.util.Utils.prettyPrintSimpleKeyName;
import static java.util.concurrent.CompletableFuture.completedFuture;

@SuppressWarnings("unused")
public final class TriggersModule extends AbstractModule {
	private final Map<Class<?>, Set<TriggerConfig<?>>> classSettings = new LinkedHashMap<>();
	private final Map<Key<?>, Set<TriggerConfig<?>>>   keySettings   = new LinkedHashMap<>();
	private Function<Key<?>, String> keyToString = Utils::prettyPrintSimpleKeyName;

	//<editor-fold desc="Description">
	private TriggersModule() {
	}

	public static TriggersModule create() {
		return builder().build();
	}

	public static Builder builder() {
		return new TriggersModule().new Builder();
	}

	@Provides
	Triggers triggers() {
		return Triggers.create();
	}
	//</editor-fold>

	//<editor-fold desc="Description">
	@ProvidesIntoSet
	LauncherService service(final Injector injector, final Triggers triggers, final OptionalDependency<Set<Initializer<TriggersModuleSettings>>> initializers) {
		final Builder builder = new Builder();
		for (final Initializer<TriggersModuleSettings> initializer : initializers.orElse(Set.of())) {
			initializer.initialize(builder);
		}
		return new LauncherService() {
			@Override
			public CompletableFuture<?> start() {
				doStart(injector, triggers);
				return completedFuture(null);
			}

			@Override
			public CompletableFuture<?> stop() {
				return completedFuture(null);
			}
		};
	}
	//</editor-fold>

	@SuppressWarnings("unchecked")
	private void doStart(final Injector injector, final Triggers triggers) {
		final Map<KeyWithWorkerData, List<TriggerRegistryRecord>> triggersMap = new LinkedHashMap<>();

		// register singletons
		for (final Map.Entry<Key<?>, Object> entry : injector.peekInstances().entrySet()) {
			final Key<Object> key      = (Key<Object>) entry.getKey();
			final Object      instance = entry.getValue();
			if (instance == null) {
				continue;
			}
			scanSingleton(injector, triggersMap, key, instance);
		}

		// register workers
		final WorkerPools workerPools = injector.peekInstance(WorkerPools.class);
		if (workerPools != null) {
			for (final WorkerPool workerPool : workerPools.getWorkerPools()) {
				if (workerPool.getSize() == 0) {
					continue;
				}

				for (final Map.Entry<Key<?>, WorkerPool.Instances<?>> entry : workerPool.peekInstances().entrySet()) {
					final Key<?>  key       = entry.getKey();
					final List<?> instances = entry.getValue().getList();

					scanWorkers(workerPool, triggersMap, key, instances);
				}
			}
		}

		for (final KeyWithWorkerData keyWithWorkerData : triggersMap.keySet()) {
			for (final TriggerRegistryRecord registryRecord : triggersMap.getOrDefault(keyWithWorkerData, List.of())) {
				triggers.addTrigger(registryRecord.severity, prettyPrintSimpleKeyName(keyWithWorkerData.getKey()), registryRecord.name, registryRecord.triggerFunction);
			}
		}
	}

	//<editor-fold desc="Description">
	private void scanSingleton(final Injector injector, final Map<KeyWithWorkerData, List<TriggerRegistryRecord>> triggersMap, Key<?> key, Object instance) {
		if (key.getRawType() == OptionalDependency.class) {
			final OptionalDependency<?> optional = (OptionalDependency<?>) instance;
			if (!optional.isPresent()) {
				return;
			}

			final Binding<?> binding = injector.getBinding(key);
			if (binding == null || binding.getType() == BindingType.SYNTHETIC) {
				return;
			}

			instance = optional.get();
			key      = key.getTypeParameter(0).qualified(key.getQualifier());
		}

		final KeyWithWorkerData internalKey = new KeyWithWorkerData(key);
		scanHasTriggers(triggersMap, internalKey, instance);
		scanClassSettings(triggersMap, internalKey, instance);
		scanKeySettings(triggersMap, internalKey, instance);
	}
	//</editor-fold>

	//<editor-fold desc="Description">
	private void scanWorkers(final WorkerPool workerPool, final Map<KeyWithWorkerData, List<TriggerRegistryRecord>> triggersMap, Key<?> key, List<?> workerInstances) {
		final Injector injector = workerPool.getScopeInjectors()[0];

		if (key.getRawType() == OptionalDependency.class) {
			final Binding<?> binding = injector.getBinding(key);
			if (binding == null || binding.getType() == BindingType.SYNTHETIC) {
				return;
			}
			final List<Object> instances = new ArrayList<>(workerInstances.size());
			for (final Object workerInstance : workerInstances) {
				instances.add(((OptionalDependency<?>) workerInstance).orElse(null));
			}
			key             = key.getTypeParameter(0).qualified(key.getQualifier());
			workerInstances = instances;
		}

		for (int i = 0; i < workerInstances.size(); i++) {
			final Object instance = workerInstances.get(i);
			if (instance == null) {
				continue;
			}

			final KeyWithWorkerData internalKey = new KeyWithWorkerData(key, workerPool, i);
			scanHasTriggers(triggersMap, internalKey, instance);
			scanClassSettings(triggersMap, internalKey, instance);
			scanKeySettings(triggersMap, internalKey, instance);
		}
	}

	private void scanHasTriggers(final Map<KeyWithWorkerData, List<TriggerRegistryRecord>> triggers, final KeyWithWorkerData internalKey, final Object instance) {
		if (instance instanceof HasTriggers) {
			((HasTriggers) instance).registerTriggers(new TriggerRegistry() {
				@Override
				public Key<?> getComponentKey() {
					return internalKey.getKey();
				}

				@Override
				public String getComponentName() {
					return keyToString.apply(internalKey.getKey());
				}

				@Override
				public void add(final Severity severity, final String name, final Supplier<TriggerResult> triggerFunction) {
					triggers.computeIfAbsent(internalKey, $ -> new ArrayList<>()).add(new TriggerRegistryRecord(severity, name, triggerFunction));
				}
			});
		}
	}

	@SuppressWarnings({"unchecked", "RedundantCast"})
	private void scanClassSettings(final Map<KeyWithWorkerData, List<TriggerRegistryRecord>> triggers, final KeyWithWorkerData internalKey, final Object instance) {
		for (final Map.Entry<Class<?>, Set<TriggerConfig<?>>> entry : classSettings.entrySet()) {
			for (final TriggerConfig<?> config : entry.getValue()) {
				if (entry.getKey().isAssignableFrom(instance.getClass())) {
					triggers.computeIfAbsent(internalKey, $ -> new ArrayList<>())
							.add(new TriggerRegistryRecord(config.severity, config.name, () ->
									((TriggerConfig<Object>) config).triggerFunction.apply(instance)));
				}
			}
		}
	}

	@SuppressWarnings({"unchecked", "RedundantCast"})
	private void scanKeySettings(final Map<KeyWithWorkerData, List<TriggerRegistryRecord>> triggers, final KeyWithWorkerData internalKey, final Object instance) {
		final Key<Object> key = (Key<Object>) internalKey.getKey();
		for (final TriggerConfig<?> config : keySettings.getOrDefault(key, Set.of())) {
			triggers.computeIfAbsent(internalKey, $ -> new ArrayList<>())
					.add(new TriggerRegistryRecord(config.severity, config.name, () ->
							((TriggerConfig<Object>) config).triggerFunction.apply(instance)));
		}
	}

	public record TriggerConfig<T>(Severity severity, String name, Function<T, TriggerResult> triggerFunction) {

		@Override
		public boolean equals(final Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			final TriggerConfig<?> that = (TriggerConfig<?>) o;
			return
					severity == that.severity &&
							Objects.equals(name, that.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(severity, name);
		}
	}

	public record TriggerRegistryRecord(Severity severity, String name, Supplier<TriggerResult> triggerFunction) {
	}

	public final class Builder extends AbstractBuilder<Builder, TriggersModule> implements TriggersModuleSettings {
		private Builder() {
		}

		@Override
		public Builder withNaming(final Function<Key<?>, String> keyToString) {
			checkNotBuilt(this);
			TriggersModule.this.keyToString = keyToString;
			return this;
		}

		@Override
		public <T> Builder with(final Class<T> type, final Severity severity, final String name, final Function<T, TriggerResult> triggerFunction) {
			checkNotBuilt(this);
			final Set<TriggerConfig<?>> triggerConfigs = classSettings.computeIfAbsent(type, $ -> new LinkedHashSet<>());

			if (!triggerConfigs.add(new TriggerConfig<>(severity, name, triggerFunction))) {
				throw new IllegalArgumentException("Cannot assign duplicate trigger");
			}

			return this;
		}

		@Override
		public <T> Builder with(final Key<T> key, final Severity severity, final String name, final Function<T, TriggerResult> triggerFunction) {
			checkNotBuilt(this);
			final Set<TriggerConfig<?>> triggerConfigs = keySettings.computeIfAbsent(key, $ -> new LinkedHashSet<>());

			if (!triggerConfigs.add(new TriggerConfig<>(severity, name, triggerFunction))) {
				throw new IllegalArgumentException("Cannot assign duplicate trigger");
			}

			return this;
		}

		@Override
		protected TriggersModule doBuild() {
			return TriggersModule.this;
		}
	}
	//</editor-fold>

}
