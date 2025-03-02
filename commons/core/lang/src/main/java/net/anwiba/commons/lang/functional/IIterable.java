/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas Bartels
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

package net.anwiba.commons.lang.functional;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;

import net.anwiba.commons.lang.counter.IntCounter;

public interface IIterable<O, E extends Exception> {

  IIterator<O, E> iterator();

  default IIterable<O, E> iterable(final IAcceptor<O> acceptor) {

    return new IIterable<O, E>() {

      @Override
      public IIterator<O, E> iterator() {
        return iterator(acceptor);
      }
    };
  }

  default Spliterator<O> spliterator() {
    IIterator<O, E> iterator = iterator();
    return Spliterators.spliteratorUnknownSize(new Iterator<O>() {

      O value;

      @Override
      public boolean hasNext() {
        if (this.value != null) {
          return true;
        }
        try {
          if (iterator.hasNext()) {
            this.value = iterator.next();
            return true;
          }
          return false;
        } catch (Exception exception) {
          return false;
        }
      }

      @Override
      public O next() {
        try {
          if (hasNext()) {
            return this.value;
          } else {
            throw new NoSuchElementException();
          }
        } finally {
          this.value = null;
        }
      }
    }, 0);
  }

  default IIterator<O, E> iterator(final IAcceptor<O> acceptor) {
    return new IIterator<O, E>() {

      private final IIterator<O, E> iterator = iterator();
      private O item = null;

      @Override
      public boolean hasNext() throws E {
        while (this.iterator.hasNext()) {
          final O i = this.iterator.next();
          if (acceptor.accept(i) && (this.item = i) != null) {
            return true;
          }
        }
        return false;
      }

      @Override
      public O next() throws E {
        try {
          if (this.item != null || hasNext()) {
            return this.item;
          }
          throw new NoSuchElementException();
        } finally {
          this.item = null;
        }
      }
    };
  }

  default O first(final IAcceptor<O> acceptor) throws E {
    final IIterator<O, E> iterator = this.iterator();
    while (iterator.hasNext()) {
      final O value = iterator.next();
      if (acceptor.accept(value)) {
        return value;
      }
    }
    return null;
  }

  default void foreach(final IConsumer<O, E> consumer) throws E {
    final IIterator<O, E> iterator = this.iterator();
    while (iterator.hasNext()) {
      final O value = iterator.next();
      consumer.consume(value);
    }
  }

  default void foreach(final int initial, final IIntAssimilator<O, E> assimilator) throws E {
    final IIterator<O, E> iterator = this.iterator();
    final IntCounter counter = new IntCounter(initial - 1);
    while (iterator.hasNext()) {
      final O value = iterator.next();
      assimilator.assimilate(counter.next(), value);
    }
  }

  default void foreach(final IAssimilator<Integer, O, E> assimilator) throws E {
    final IIterator<O, E> iterator = this.iterator();
    final IntCounter counter = new IntCounter(-1);
    while (iterator.hasNext()) {
      final O value = iterator.next();
      assimilator.assimilate(Integer.valueOf(counter.next()), value);
    }
  }

  default <R> R aggregate(final R identity, final IAggregator<R, O, R, E> adder) throws E {
    final IIterator<O, E> iterator = this.iterator();
    R result = identity;
    while (iterator.hasNext()) {
      result = adder.aggregate(result, iterator.next());
    }
    return result;
  }

}
