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

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.IdentityHashMap;
import java.util.concurrent.TimeUnit;

/**
 * The jmh-benchmark-class for benchmarking.
 *
 * @author lunarydess, FlorianMichael/EnZaXD
 * @version 1.0.0-release
 */
@SuppressWarnings("unused")
@State(value = Scope.Benchmark)
@OutputTimeUnit(value = TimeUnit.NANOSECONDS)
@Warmup(iterations = 4, time = 5)
@Measurement(iterations = 4, time = 5)
public class BenchmarkCaller implements BenchmarkListener {
	private static final int ITERATIONS = 100_000;
	private static final TinyEvents EVENTS = new TinyEvents(IdentityHashMap::new, Throwable::printStackTrace);

	@Setup
	public void setup() {
		EVENTS.register(BenchmarkEvent.class, this);
	}

	@Benchmark
	@BenchmarkMode(value = Mode.AverageTime)
	@Fork(value = 1, warmups = 1)
	public void callBenchmarkListener(Blackhole blackhole) {
		for (int i = 0 ; i < ITERATIONS ; i++) {
			EVENTS.call(new BenchmarkListener.BenchmarkEvent(blackhole));
		}
	}

	@Override
	public void onBenchmark(Blackhole blackhole) {
		blackhole.consume(Integer.bitCount(Integer.parseInt("123")));
	}
}
