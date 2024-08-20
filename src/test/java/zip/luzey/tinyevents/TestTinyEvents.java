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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import zip.luzey.tinyevents.AbstractEvent.Cancellable;
import zip.luzey.tinyevents.EventHandlers.IHandler;

import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.StringJoiner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The only junit-test-class of this project.
 *
 * @author lunarydess
 * @version 1.0.0-release
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("TinyEvents.java")
@SuppressWarnings("deprecated")
class TestTinyEvents {
	/**
	 * Tests all functions of the event-system.<br>
	 * Perfect example of all use-cases.
	 */
	@Test
	@DisplayName("all")
	@SuppressWarnings("deprecation")
	void test() {
		TinyEvents events = new TinyEvents(IdentityHashMap::new, Throwable::printStackTrace);

		IHandler<DummyEvent1> handler1 = event -> {
			event.setString1("how are y'all doing");
			event.setString2("hope y'all keep going :)");
		};
		events.register(DummyEvent1.class, handler1);
		assertEquals(1, events.getHandlers().size());

		IHandler<DummyEvent2> handler2 = event -> {
			event.setNum1(9090);
			event.setNum2(1337);
		};
		events.register(DummyEvent2.class, handler2);
		assertEquals(2, events.getHandlers().size());

		IHandler<DummyEvent3> handler3 = event -> {
			if (event.cancelled()) return;
			String string1 = event.getString1(), string2 = event.getString2();

			event.setString1(string2);
			event.setString2(string1);
			event.cancel();
		};
		events.register(DummyEvent3.class, handler3);
		assertEquals(3, events.getHandlers().size());

		IHandler<DummyEvent4> handler4 = event -> {
			if (event.cancelled()) return;
			int num1 = event.getNum1(), num2 = event.getNum2();

			event.setNum1(num2);
			event.setNum2(num1);
			event.cancel();
		};
		events.register(DummyEvent4.class, handler4);
		assertEquals(4, events.getHandlers().size());

		DummyEvent1 event1 = new DummyEvent1(
			 "waow",
			 "hellow there"
		);
		assertEquals(
			 "waow",
			 event1.getString1()
		);
		assertEquals(
			 "hellow there",
			 event1.getString2()
		);
		handler1.accept(event1);
		assertEquals(
			 "how are y'all doing",
			 event1.getString1()
		);
		assertEquals(
			 "hope y'all keep going :)",
			 event1.getString2()
		);

		DummyEvent2 event2 = new DummyEvent2(1337, 9090);
		assertEquals(1337, event2.getNum1());
		assertEquals(9090, event2.getNum2());
		handler2.accept(event2);
		assertEquals(9090, event2.getNum1());
		assertEquals(1337, event2.getNum2());

		DummyEvent3 event3 = new DummyEvent3(
			 "i'm doing fine curr",
			 "just wishing it stays like this ;w;"
		);
		assertEquals(
			 "i'm doing fine curr",
			 event3.getString1()
		);
		assertEquals(
			 "just wishing it stays like this ;w;",
			 event3.getString2()
		);
		assertFalse(event3.cancelled());
		handler3.accept(event3);
		assertEquals(
			 "just wishing it stays like this ;w;",
			 event3.getString1()
		);
		assertEquals(
			 "i'm doing fine curr",
			 event3.getString2()
		);
		assertTrue(event3.cancelled());
		handler3.accept(event3);
		assertEquals(
			 "just wishing it stays like this ;w;",
			 event3.getString1()
		);
		assertEquals(
			 "i'm doing fine curr",
			 event3.getString2()
		);

		DummyEvent4 event4 = new DummyEvent4(9090, 1337);
		assertEquals(9090, event4.getNum1());
		assertEquals(1337, event4.getNum2());
		assertFalse(event4.cancelled());
		handler4.accept(event4);
		assertEquals(1337, event4.getNum1());
		assertEquals(9090, event4.getNum2());
		assertTrue(event4.cancelled());
		handler4.accept(event4);
		assertEquals(1337, event4.getNum1());
		assertEquals(9090, event4.getNum2());

		assertEquals(1, events.getHandlers().get(DummyEvent4.class).length);
		events.unregister(DummyEvent4.class, handler4);
		assertNull(events.getHandlers().get(DummyEvent4.class));

		assertEquals(1, events.getHandlers().get(DummyEvent3.class).length);
		events.unregister(DummyEvent3.class, handler3);
		assertNull(events.getHandlers().get(DummyEvent3.class));

		assertEquals(1, events.getHandlers().get(DummyEvent2.class).length);
		events.unregister(DummyEvent2.class, handler2);
		assertNull(events.getHandlers().get(DummyEvent2.class));

		assertEquals(1, events.getHandlers().get(DummyEvent1.class).length);
		events.unregister(DummyEvent1.class, handler1);
		assertNull(events.getHandlers().get(DummyEvent1.class));
	}

	static final class DummyEvent1 extends AbstractEvent {
		private String string1, string2;

		DummyEvent1(String string1, String string2) {
			this.string1 = string1;
			this.string2 = string2;
		}

		public String getString1() {
			return string1;
		}

		public void setString1(String string1) {
			this.string1 = string1;
		}

		public String getString2() {
			return string2;
		}

		public void setString2(String string2) {
			this.string2 = string2;
		}

		public @Override int hashCode() {
			return Objects.hash(
				 this.string1,
				 this.string2
			);
		}

		public @Override boolean equals(Object object) {
			return object instanceof DummyEvent1 event && this.equals(event);
		}

		public <E extends DummyEvent1> boolean equals(E event) {
			return Objects.equals(this.hashCode(), event.hashCode()) || (
				 (Objects.equals(this.getString1(), event.getString1())) &&
				 (Objects.equals(this.getString2(), event.getString2()))
			);
		}

		public @Override String toString() {
			return new StringJoiner(", ", DummyEvent1.class.getSimpleName() + "[", "]")
				 .add("string1='" + this.string1 + "'")
				 .add("string2='" + this.string2 + "'")
				 .toString();
		}
	}

	static final class DummyEvent2 extends AbstractEvent {
		private int num1, num2;

		DummyEvent2(int num1, int num2) {
			this.num1 = num1;
			this.num2 = num2;
		}

		public int getNum1() {
			return num1;
		}

		public void setNum1(int num) {
			this.num1 = num;
		}

		public int getNum2() {
			return this.num2;
		}

		public void setNum2(int num) {
			this.num2 = num;
		}

		public @Override int hashCode() {
			return Objects.hash(
				 this.num1,
				 this.num2
			);
		}

		public @Override boolean equals(Object object) {
			return object instanceof DummyEvent2 event && this.equals(event);
		}

		public <E extends DummyEvent2> boolean equals(E event) {
			return Objects.equals(this.hashCode(), event.hashCode()) || (
				 (Objects.equals(this.getNum1(), event.getNum1())) &&
				 (Objects.equals(this.getNum2(), event.getNum2()))
			);
		}

		public @Override String toString() {
			return new StringJoiner(", ", DummyEvent2.class.getSimpleName() + "[", "]")
				 .add("num1='" + this.num1 + "'")
				 .add("num2='" + this.num2 + "'")
				 .toString();
		}
	}

	static final class DummyEvent3 extends AbstractEvent implements Cancellable {
		private String string1, string2;
		private boolean cancelled = false;

		DummyEvent3(String string1, String string2) {
			this.string1 = string1;
			this.string2 = string2;
		}

		public String getString1() {
			return string1;
		}

		public void setString1(String string1) {
			this.string1 = string1;
		}

		public String getString2() {
			return string2;
		}

		public void setString2(String string2) {
			this.string2 = string2;
		}

		public @Override int hashCode() {
			return Objects.hash(
				 this.string1,
				 this.string2,
				 this.cancelled
			);
		}

		public @Override boolean equals(Object object) {
			return object instanceof DummyEvent3 event && this.equals(event);
		}

		public <E extends DummyEvent3> boolean equals(E event) {
			return Objects.equals(this.hashCode(), event.hashCode()) || (
				 (Objects.equals(this.getString1(), event.getString1())) &&
				 (Objects.equals(this.getString2(), event.getString2()))
			);
		}

		public @Override String toString() {
			return new StringJoiner(", ", DummyEvent3.class.getSimpleName() + "[", "]")
				 .add("string1='" + this.string1 + "'")
				 .add("string2='" + this.string2 + "'")
				 .add("cancelled='" + this.cancelled + "'")
				 .toString();
		}

		@Override
		public void cancel(boolean state) {
			this.cancelled = state;
		}

		@Override
		public boolean cancelled() {
			return this.cancelled;
		}
	}

	static final class DummyEvent4 extends AbstractEvent implements Cancellable {
		private int num1, num2;
		private boolean cancelled = false;

		DummyEvent4(int num1, int num2) {
			this.num1 = num1;
			this.num2 = num2;
		}

		public int getNum1() {
			return num1;
		}

		public void setNum1(int num) {
			this.num1 = num;
		}

		public int getNum2() {
			return this.num2;
		}

		public void setNum2(int num) {
			this.num2 = num;
		}

		public @Override int hashCode() {
			return Objects.hash(
				 this.num1,
				 this.num2,
				 this.cancelled
			);
		}

		public @Override boolean equals(Object object) {
			return object instanceof DummyEvent4 event && this.equals(event);
		}

		public <E extends DummyEvent4> boolean equals(E event) {
			return Objects.equals(this.hashCode(), event.hashCode()) || (
				 (Objects.equals(this.getNum1(), event.getNum1())) &&
				 (Objects.equals(this.getNum2(), event.getNum2()))
			);
		}

		public @Override String toString() {
			return new StringJoiner(", ", DummyEvent4.class.getSimpleName() + "[", "]")
				 .add("num1='" + this.num1 + "'")
				 .add("num2='" + this.num2 + "'")
				 .add("cancelled='" + this.cancelled + "'")
				 .toString();
		}

		@Override
		public void cancel(boolean state) {
			this.cancelled = state;
		}

		@Override
		public boolean cancelled() {
			return this.cancelled;
		}
	}
}
