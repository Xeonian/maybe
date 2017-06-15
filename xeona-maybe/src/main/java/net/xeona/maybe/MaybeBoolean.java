package net.xeona.maybe;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import net.xeona.function.BooleanConsumer;
import net.xeona.function.BooleanFunction;
import net.xeona.function.BooleanProvider;
import net.xeona.function.BooleanUnaryOperator;
import net.xeona.function.Provider;

public abstract class MaybeBoolean implements Collection<Boolean>, Serializable {

	private static final long serialVersionUID = 1L;

	private MaybeBoolean() {}

	public abstract boolean isPresent();

	public abstract boolean get();

	public abstract boolean orElse(boolean other);

	public abstract <X extends Throwable> boolean orElseGet(BooleanProvider<? extends X> provider) throws X;

	public abstract <X extends Throwable> boolean orElseThrow(Provider<? extends X, RuntimeException> provider)
			throws X;

	public abstract <X extends Throwable> void ifPresent(BooleanConsumer<? extends X> consumer) throws X;

	public abstract <X extends Throwable> MaybeBoolean filter(BooleanUnaryOperator<? extends X> predicate) throws X;

	public abstract <X extends Throwable> MaybeBoolean mapToBoolean(BooleanUnaryOperator<? extends X> function)
			throws X;

	public abstract <T, X extends Throwable> Maybe<T> map(BooleanFunction<? extends T, ? extends X> function) throws X;

	@Override
	public boolean add(Boolean element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends Boolean> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object object) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		throw new UnsupportedOperationException();
	}

	public static MaybeBoolean just(boolean value) {
		return value ? Just.TRUE_INSTANCE : Just.FALSE_INSTANCE;
	}

	public static MaybeBoolean nothing() {
		return Nothing.INSTANCE;
	}

	public static class Just extends MaybeBoolean {

		private static final long serialVersionUID = 1L;

		private static final Just TRUE_INSTANCE = new Just(true);
		private static final Just FALSE_INSTANCE = new Just(false);

		private final boolean value;

		private Just(boolean value) {
			this.value = value;
		}

		@Override
		public boolean get() {
			return value;
		}

		@Override
		public boolean isPresent() {
			return true;
		}

		@Override
		public <X extends Throwable> void ifPresent(BooleanConsumer<? extends X> consumer) throws X {
			consumer.consume(value);
		}

		@Override
		public <X extends Throwable> MaybeBoolean filter(BooleanUnaryOperator<? extends X> predicate) throws X {
			return predicate.apply(value) ? this : Nothing.INSTANCE;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(BooleanUnaryOperator<? extends X> function) throws X {
			return MaybeBoolean.just(requireNonNull(function, "Function must not be null").apply(value));
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(BooleanFunction<? extends T, ? extends X> function) throws X {
			return Maybe.maybe(function.apply(value));
		}

		@Override
		public boolean orElse(boolean other) {
			return value;
		}

		@Override
		public <X extends Throwable> boolean orElseGet(BooleanProvider<? extends X> provider) throws X {
			return value;
		}

		@Override
		public <X extends Throwable> boolean orElseThrow(Provider<? extends X, RuntimeException> provider) throws X {
			return value;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public boolean contains(Object o) {
			return o instanceof Boolean && ((Boolean) o).booleanValue() == value;
		}

		@Override
		public boolean containsAll(Collection<?> collection) {
			return collection.stream().allMatch(element -> contains(element));
		}

		@Override
		public Object[] toArray() {
			Object[] arr = new Object[1];
			arr[0] = value;
			return arr;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T[] toArray(T[] a) {
			Object[] valueArr = new Object[] { value };

			T[] returnArr;
			if (a.length == 0) {
				returnArr = (T[]) Arrays.copyOf(valueArr, 1, a.getClass());
			} else {
				System.arraycopy(valueArr, 0, a, 0, 1);
				if (a.length > 1) {
					a[1] = null;
				}
				returnArr = a;
			}

			return returnArr;
		}

		@Override
		public Iterator<Boolean> iterator() {
			return new Iterator<Boolean>() {

				private boolean hasNext = true;

				@Override
				public boolean hasNext() {
					return hasNext;
				}

				@Override
				public Boolean next() {
					if (hasNext) {
						hasNext = false;
						return value;
					} else {
						throw new NoSuchElementException();
					}
				}

			};
		}

		@Override
		public String toString() {
			return "Just [" + value + "]";
		}

		@Override
		public int hashCode() {
			return Boolean.hashCode(value);
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Just && ((Just) other).value == value;
		}

	}

	private static class Nothing extends MaybeBoolean {

		private static final long serialVersionUID = 1L;

		private static final Nothing INSTANCE = new Nothing();

		private static final Iterator<Boolean> ITERATOR = new Iterator<Boolean>() {

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public Boolean next() {
				throw new NoSuchElementException();
			}
		};

		@Override
		public boolean get() {
			throw new NoSuchElementException();
		}

		@Override
		public boolean isPresent() {
			return false;
		}

		@Override
		public <X extends Throwable> void ifPresent(BooleanConsumer<? extends X> consumer) {}

		@Override
		public <X extends Throwable> MaybeBoolean filter(BooleanUnaryOperator<? extends X> predicate) {
			return INSTANCE;
		}

		@Override
		public <X extends Throwable> MaybeBoolean mapToBoolean(BooleanUnaryOperator<? extends X> function) {
			return INSTANCE;
		}

		@Override
		public <T, X extends Throwable> Maybe<T> map(BooleanFunction<? extends T, ? extends X> function) throws X {
			return Maybe.nothing();
		}

		@Override
		public boolean orElse(boolean other) {
			return other;
		}

		@Override
		public <X extends Throwable> boolean orElseGet(BooleanProvider<? extends X> provider) throws X {
			return provider.get();
		}

		@Override
		public <X extends Throwable> boolean orElseThrow(Provider<? extends X, RuntimeException> provider) throws X {
			throw provider.get();
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public boolean contains(Object o) {
			return false;
		}

		@Override
		public Iterator<Boolean> iterator() {
			return ITERATOR;
		}

		@Override
		public Object[] toArray() {
			return new Object[0];
		}

		@Override
		public <T> T[] toArray(T[] a) {
			if (a.length > 0) {
				a[0] = null;
			}
			return a;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return c.isEmpty();
		}

		@Override
		public String toString() {
			return "Nothing []";
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object other) {
			return other instanceof Nothing;
		}

	}

}
