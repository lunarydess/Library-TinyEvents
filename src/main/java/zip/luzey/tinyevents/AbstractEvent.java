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

/**
 * The abstraction layer for all events.
 *
 * @author lunarydess
 * @version 1.0.0-release
 */
public abstract class AbstractEvent {
	/**
	 * @return a valid hashcode from the objects in your event.
	 *
	 * @see java.util.Objects#hash(Object...)
	 */
	public abstract @Override int hashCode();

	/**
	 * @return comparison of hashcode and fields.
	 */
	public abstract @Override boolean equals(final Object object);

	public abstract @Override String toString();

	/**
	 * Used to implement your own cancellation-logic.
	 */
	public interface Cancellable {
		/**
		 * Switches the cancel state to the opposite.
		 */
		default void cancel() {
			cancel(!cancelled());
		}

		/**
		 * Sets the cancel state given by param.
		 *
		 * @param state The state we want to change our cancel-state to.
		 */
		void cancel(boolean state);

		/**
		 * @return The current cancel state.
		 */
		boolean cancelled();
	}
}
