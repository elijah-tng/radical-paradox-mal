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

import io.activej.jmx.stats.ExceptionStats;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static io.activej.common.Checks.checkState;
import static io.activej.jmx.stats.MBeanFormat.formatExceptionMultiline;

public final class TriggerResult {
	private static final TriggerResult NONE = new TriggerResult(0, null, null);

	private final long timestamp;
	private final Throwable throwable;
	private final Object value;
	private final int count;

	private TriggerResult(final long timestamp, @Nullable final Throwable e, @Nullable final Object value, final int count) {
		this.timestamp = timestamp;
		throwable      = e;
		this.value     = value;
		this.count     = count;
	}

	private TriggerResult(final long timestamp, @Nullable final Throwable e, @Nullable final Object context) {
		this(timestamp, e, context, 1);
	}

	public static TriggerResult none() {
		return NONE;
	}

	public static TriggerResult create() {
		return new TriggerResult(0L, null, null);
	}

	public static TriggerResult create(final long timestamp, final Throwable e, final int count) {
		return new TriggerResult(timestamp, e, null, count);
	}

	public static TriggerResult create(final long timestamp, @Nullable final Throwable e, @Nullable final Object value) {
		return new TriggerResult(timestamp, e, value);
	}

	public static TriggerResult create(final long timestamp, final Throwable e, final Object value, final int count) {
		return new TriggerResult(timestamp, e, value, count);
	}

	public static TriggerResult create(final Instant instant, @Nullable final Throwable e, @Nullable final Object value) {
		return create(instant.toEpochMilli(), e, value);
	}

	public static TriggerResult create(final Instant instant, final Throwable e, final Object value, final int count) {
		return create(instant.toEpochMilli(), e, value, count);
	}

	public static TriggerResult ofTimestamp(final long timestamp) {
		return timestamp != 0L ?
				new TriggerResult(timestamp, null, null) : NONE;
	}

	public static TriggerResult ofTimestamp(final long timestamp, final boolean condition) {
		return timestamp != 0L && condition ?
				new TriggerResult(timestamp, null, null) : NONE;
	}

	public static TriggerResult ofInstant(@Nullable final Instant instant) {
		return instant != null ?
				create(instant, null, null) : NONE;
	}

	public static TriggerResult ofInstant(final Instant instant, final boolean condition) {
		return instant != null && condition ?
				create(instant, null, null) : NONE;
	}

	public static TriggerResult ofError(final Throwable e) {
		return e != null ?
				new TriggerResult(0L, e, null) : NONE;
	}

	public static TriggerResult ofError(final Throwable e, final long timestamp) {
		return e != null ?
				new TriggerResult(timestamp, e, null) : NONE;
	}

	public static TriggerResult ofError(final Throwable e, final Instant instant) {
		return e != null ?
				create(instant.toEpochMilli(), e, null) : NONE;
	}

	public static TriggerResult ofError(final ExceptionStats exceptionStats) {
		final Throwable lastException = exceptionStats.getLastException();
		return lastException != null ?
				create(exceptionStats.getLastTime() != null ?
						       exceptionStats.getLastTime().toEpochMilli() :
						       0,
				       lastException, exceptionStats.getTotal()
				) :
				NONE;
	}

	public static TriggerResult ofValue(final Object value) {
		return value != null ?
				new TriggerResult(0L, null, value) : NONE;
	}

	public static <T> TriggerResult ofValue(final T value, final Predicate<T> predicate) {
		return value != null && predicate.test(value) ?
				new TriggerResult(0L, null, value) : NONE;
	}

	public static <T> TriggerResult ofValue(final T value, final boolean condition) {
		return value != null && condition ?
				new TriggerResult(0L, null, value) : NONE;
	}

	public static <T> TriggerResult ofValue(final Supplier<T> supplier, final boolean condition) {
		return condition ? ofValue(supplier.get()) : NONE;
	}

	public TriggerResult withValue(final Object value) {
		return isPresent() ? new TriggerResult(timestamp, throwable, value) : NONE;
	}

	public TriggerResult withValue(final Supplier<?> value) {
		return isPresent() ? new TriggerResult(timestamp, throwable, value.get()) : NONE;
	}

	public TriggerResult withCount(final int count) {
		return isPresent() ? new TriggerResult(timestamp, throwable, value, count) : NONE;
	}

	public TriggerResult withCount(final IntSupplier count) {
		return isPresent() ? new TriggerResult(timestamp, throwable, value, count.getAsInt()) : NONE;
	}

	public TriggerResult when(final boolean condition) {
		return isPresent() && condition ? this : NONE;
	}

	public TriggerResult when(final BooleanSupplier conditionSupplier) {
		return isPresent() && conditionSupplier.getAsBoolean() ? this : NONE;
	}

	@SuppressWarnings("unchecked")
	public <T> TriggerResult whenValue(final Predicate<T> valuePredicate) {
		return isPresent() && hasValue() && valuePredicate.test((T) value) ? this : NONE;
	}

	public boolean isPresent() {
		return this != NONE;
	}

	public boolean hasTimestamp() {
		return timestamp != 0L;
	}

	public boolean hasThrowable() {
		return throwable != null;
	}

	public boolean hasValue() {
		return value != null;
	}

	public long getTimestamp() {
		checkState(isPresent(), "Trigger is not present");
		return timestamp;
	}

	public @Nullable Instant getInstant() {
		checkState(isPresent(), "Trigger is not present");
		return hasTimestamp() ? Instant.ofEpochMilli(timestamp) : null;
	}

	public @Nullable Throwable getThrowable() {
		checkState(isPresent(), "Trigger is not present");
		return throwable;
	}

	public @Nullable Object getValue() {
		checkState(isPresent(), "Trigger is not present");
		return value;
	}

	public int getCount() {
		checkState(isPresent(), "Trigger is not present");
		return count;
	}

	@Override
	public String toString() {
		return
				"@" + Instant.ofEpochMilli(timestamp) +
						(count != 1 ? " #" + count : "") +
						(value != null ? " : " + value : "") +
						(throwable != null ? "\n" + formatExceptionMultiline(throwable) : "");
	}
}
