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

import org.openjdk.jmh.infra.Blackhole;
import zip.luzey.tinyevents.BenchmarkListener.BenchmarkEvent;
import zip.luzey.tinyevents.EventHandlers.IHandler;

import java.util.Objects;

/**
 * The listener-class for the {@link BenchmarkCaller benchmark caller}.
 *
 * @author lunarydess, FlorianMichael/EnZaXD
 * @version 1.0.0-release
 */
public interface BenchmarkListener extends IHandler<BenchmarkEvent> {
	void onBenchmark(final Blackhole blackhole);

	default @Override void handle(BenchmarkEvent event) {
		onBenchmark(event.blackhole);
	}

	class BenchmarkEvent extends AbstractEvent {
		@SuppressWarnings("unused")
		public static final int ID = 0;

		private final Blackhole blackhole;

		public BenchmarkEvent(final Blackhole blackhole) {
			this.blackhole = blackhole;
		}

		public Blackhole getBlackhole() {
			return blackhole;
		}

		public @Override int hashCode() {
			return Objects.hash(blackhole);
		}

		public @Override boolean equals(Object object) {
			return object instanceof BenchmarkEvent event && this.equals(event);
		}

		public <E extends BenchmarkEvent> boolean equals(E event) {
			return this.hashCode() == event.hashCode() || Objects.equals(this.blackhole, event.getBlackhole());
		}

		public @Override String toString() {
			return "BenchmarkEvent{" +
			       "blackhole=" + blackhole +
			       '}';
		}
	}
}
