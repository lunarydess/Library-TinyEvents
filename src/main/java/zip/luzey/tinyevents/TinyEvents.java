/**
 * This file is part of <a href="https://github.com/lunarydess/Library-TinyEvents">TinyEvents</a>
 * Copyright (C) 2024 lunarydess (inbox@luzey.zip)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package zip.luzey.tinyevents;

import zip.luzey.tinyevents.EventHandlers.IHandler;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The main-class of this project.
 *
 * @author lunarydess
 * @version 1.0.0-release
 */
@SuppressWarnings("unused")
public final class TinyEvents {
	/**
	 * The default on-error when none gets passed.
	 */
	public static final Consumer<Throwable> DEFAULT_ON_ERROR = Throwable::printStackTrace;
	private final Consumer<Throwable> onError;

	private final Map<Class<? extends AbstractEvent>, IHandler<? extends AbstractEvent>[]> handlers;
	private final Object2IntMap<IHandler<? extends AbstractEvent>> handlersIndices = new Object2IntMap<>();


	/**
	 * Creates a new event-manager with a default {@link IdentityHashMap map} and {@link TinyEvents#DEFAULT_ON_ERROR error-handler}.
	 */
	public TinyEvents() {
		this(IdentityHashMap::new, DEFAULT_ON_ERROR);
	}

	/**
	 * Creates a new event-manager with a custom {@link Supplier<Map> map} and default {@link TinyEvents#DEFAULT_ON_ERROR error-handler}.
	 *
	 * @param factory The custom map we want to provide.
	 */
	public TinyEvents(
		 final Supplier<Map<Class<? extends AbstractEvent>,
			  IHandler<? extends AbstractEvent>[]
			  >> factory
	) {
		this(factory, DEFAULT_ON_ERROR);
	}

	/**
	 * Creates a new event-manager with a custom {@link Supplier<Map> map} and {@link Consumer<Throwable> error-handler}.
	 *
	 * @param factory The custom map we want to provide.
	 * @param onError The custom error-handler we want to provide.
	 */
	public TinyEvents(
		 final Supplier<Map<Class<? extends AbstractEvent>, IHandler<? extends AbstractEvent>[]>> factory,
		 final Consumer<Throwable> onError
	) {
		this.handlers = factory.get();
		this.onError = onError;
	}

	/**
	 * @param clazz   The class-group of our handlers.
	 * @param handler The handler we want to add.
	 * @param <H>     The type of the handler.
	 * @param <E>     The type of the event for our handler.
	 */
	public <H extends EventHandlers.IHandler<E>, E extends AbstractEvent> void register(
		 final Class<E> clazz,
		 final H handler
	) {
		try {
			final IHandler<? extends AbstractEvent>[] current = this.handlers.getOrDefault(clazz, new IHandler<?>[0]);
			final IHandler<? extends AbstractEvent>[] updated = Arrays.copyOf(current, current.length + 1);
			updated[updated.length - 1] = handler;

			final Comparator<IHandler<? extends AbstractEvent>> sort = Comparator.comparingInt(wrapper1 -> handler.priority());
			Arrays.sort(updated, sort);
			this.handlers.put(clazz, updated);
			this.handlersIndices.put(handler, Arrays.binarySearch(updated, handler, sort));
		} catch (final Throwable throwable) {
			onError.accept(throwable);
		}
	}

	/**
	 * @param clazz   The class-group of our handlers.
	 * @param handler The handler we want to remove.
	 * @param <H>     The type of the handler.
	 * @param <E>     The type of the event for our handler.
	 */
	public <H extends EventHandlers.IHandler<E>, E extends AbstractEvent> void unregister(
		 final Class<E> clazz,
		 final H handler
	) {
		final IHandler<? extends AbstractEvent>[] current = this.handlers.getOrDefault(clazz, new IHandler<?>[0]);

		if (current.length == 0) {
			this.handlers.remove(clazz);
			return;
		}

		int index = this.handlersIndices.get(handler);
		if (index < 0 || index > current.length - 1) {
			this.onError.accept(new NoSuchFieldError(String.format(
				 "The handler %s doesn't exist.",
				 handler.toString()
			)));
			return;
		}

		final IHandler<? extends AbstractEvent>[] updated = new IHandler<?>[current.length - 1];
		if (updated.length > 0) {
			System.arraycopy(current, 0, updated, 0, index);
			System.arraycopy(current, index + 1, updated, index, current.length - index - 1);
			this.handlers.put(clazz, updated);
		} else this.handlers.remove(clazz);
		this.handlersIndices.remove(handler);
	}

	/**
	 * @param event The event we want to call.
	 * @param <E>   The type of our event.
	 */
	@SuppressWarnings("unchecked")
	public <E extends AbstractEvent> void call(final E event) {
		final IHandler<E>[] handlers = (IHandler<E>[]) this.handlers.get(event.getClass());
		if (handlers == null) return;
		for (final IHandler<E> handler : handlers) {
			try {
				handler.accept(event);
			} catch (final Throwable throwable) {
				onError.accept(throwable);
			}
		}
	}

	/**
	 * @return the internal error-handler
	 *
	 * @see TinyEvents#call(AbstractEvent)
	 * @see TinyEvents#register(Class, IHandler)
	 * @see TinyEvents#unregister(Class, IHandler)
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public Consumer<Throwable> getOnError() {
		return this.onError;
	}

	/**
	 * @return the internal handlers-map
	 *
	 * @see TinyEvents#call(AbstractEvent)
	 * @see TinyEvents#register(Class, IHandler)
	 * @see TinyEvents#unregister(Class, IHandler)
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public Map<Class<? extends AbstractEvent>, IHandler<? extends AbstractEvent>[]> getHandlers() {
		return this.handlers;
	}

	/**
	 * @return the internal handlers-indices-map
	 *
	 * @see TinyEvents#call(AbstractEvent)
	 * @see TinyEvents#register(Class, IHandler)
	 * @see TinyEvents#unregister(Class, IHandler)
	 */
	@Deprecated
	@SuppressWarnings("DeprecatedIsStillUsed")
	public Object2IntMap<IHandler<? extends AbstractEvent>> getHandlersIndices() {
		return this.handlersIndices;
	}


	/**
	 * @param <K> The object-type we want to use.
	 */
	@SuppressWarnings("unchecked")
	public static class Object2IntMap<K> {
		private static final int INITIAL_CAPACITY = 16;
		private static final float LOAD_FACTOR = 0.75f;

		private LinkedList<Entry<K>>[] table;
		private int size;

		/**
		 * Creates a {@link Object2IntMap<K> map} with a {@link Object2IntMap#INITIAL_CAPACITY initial capacity}.
		 */
		public Object2IntMap() {
			this(INITIAL_CAPACITY);
		}

		/**
		 * Creates a {@link Object2IntMap<K> map} with a given capacity.
		 *
		 * @param capacity The capacity we want to start with.
		 */
		public Object2IntMap(final int capacity) {
			this.table = new LinkedList[capacity];
			this.size = 0;
		}

		/**
		 * @param key   The key of our int-value we want to add
		 * @param value The int-value we want to add.
		 */
		public void put(
			 final K key,
			 final int value
		) {
			if (size >= table.length * LOAD_FACTOR) {
				int newCapacity = table.length * 2;
				LinkedList<Entry<K>>[] newTable = new LinkedList[newCapacity];
				for (LinkedList<Entry<K>> entries : table) {
					if (entries == null) continue;
					for (Entry<K> entry : entries) {
						int index = hash(entry.key) & (newCapacity - 1);
						(newTable[index] == null ? (newTable[index] = new LinkedList<>()) : newTable[index]).add(entry);
					}
				}
				table = newTable;
			}

			int index = hash(key) & (table.length - 1);
			if (table[index] == null)
				table[index] = new LinkedList<>();

			for (Entry<K> entry : table[index]) {
				if (Objects.equals(entry.key, key)) {
					entry.value = value; // Update existing value
					return;
				}
			}

			table[index].add(new Entry<>(key, value));
			size++;
		}

		/**
		 * Gets the int-value of the given key.
		 *
		 * @param key The key we want our int-value from.
		 *
		 * @return The int-value or -1 if null or not found.
		 */
		public int get(K key) {
			int index = hash(key) & (table.length - 1);
			if (table[index] == null) return -1;

			for (Entry<K> entry : table[index]) {
				if (!Objects.equals(entry.key, key)) continue;
				return entry.value;
			}

			return -1;
		}

		/**
		 * Removes the int-value of the given key.
		 *
		 * @param key The key of our int-value we want to remove
		 */
		public void remove(K key) {
			int index = hash(key) & (table.length - 1);
			if (table[index] != null) {
				table[index].removeIf(entry -> Objects.equals(entry.key, key));
				size--;
			}
		}

		/**
		 * @param key The key we want the hash-value from.
		 *
		 * @return A hash-value from our key.
		 */
		private int hash(Object key) {
			return key == null ? 0 : key.hashCode();
		}

		/**
		 * An entry we can store our key and value in.
		 *
		 * @param <K> The type of our key.
		 */
		private static class Entry<K> {
			private final K key;
			private int value;

			/**
			 * Creates a new {@link Entry<K> entry}.
			 *
			 * @param key   The key of our entry.
			 * @param value The value of our entry.
			 */
			Entry(
				 final K key,
				 final int value
			) {
				this.key = key;
				this.value = value;
			}

			/**
			 * @return The key of our entry.
			 */
			public K getKey() {
				return key;
			}

			/**
			 * @return The int-value of our entry.
			 */
			public int getValue() {
				return value;
			}

			/**
			 * Sets a new int-value.
			 *
			 * @param value The new value we want to set.
			 */
			public void setValue(final int value) {
				this.value = value;
			}
		}
	}
}
