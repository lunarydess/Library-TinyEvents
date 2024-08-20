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

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * The dummy-class for our effective-static-subclasses.
 *
 * @author lunarydess
 * @version 1.0.0-release
 */
@SuppressWarnings("unused")
public final class EventHandlers {
	/**
	 * This is an abstraction layer for a consumer-handlers to implement our own handle-logic for events.
	 *
	 * @param <E> The event-type of our handler.
	 */
	@FunctionalInterface
	public interface IHandler<E extends AbstractEvent> extends Consumer<E>, Comparable<IHandler<E>> {
		/**
		 * Handles our incoming events when {@link TinyEvents#call(AbstractEvent)} gets called.
		 *
		 * @param event The event we want to implement the logic for.
		 */
		void handle(final E event);

		default @Override void accept(final E e) {
			this.handle(e);
		}

		/**
		 * Defines how important/prioritized our event is.<br>
		 * Higher values allow prioritized calling.<br>
		 * Highest is {@link Short#MAX_VALUE} and lowest is {@link Short#MIN_VALUE}
		 *
		 * @return The priority of our event.
		 */
		default short priority() {
			return 0;
		}

		default @Override int compareTo(@NotNull EventHandlers.IHandler<E> handler) {
			return Short.compare(handler.priority(), this.priority());
		}
	}

	/**
	 * This is an abstraction layer for class-handlers to implement our own handle-logic for events.
	 *
	 * @param <E> The event-type of our handler.
	 */
	public abstract static class AbstractHandler<E extends AbstractEvent> implements IHandler<E> {
		/**
		 * Handles our incoming events when {@link TinyEvents#call(AbstractEvent)} gets called.
		 *
		 * @param event The event we want to implement the logic for.
		 */
		public abstract @Override void handle(final E event);

		/**
		 * A wrapper method that calls {@link AbstractHandler#handle(E)}
		 *
		 * @param event The event we want to implement the logic for.
		 */
		public final @Override void accept(final E event) {
			this.handle(event);
		}
	}
}
