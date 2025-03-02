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

import java.util.Objects;

@FunctionalInterface
public interface IAcceptor<T> {

  boolean accept(T value);

  static <T> IAcceptor<T> not(IAcceptor<T> acceptor) {
    Objects.requireNonNull(acceptor);
    return acceptor.not();
  }

  default IAcceptor<T> not() {
    return (t) -> !accept(t);
  }

  default IAcceptor<T> and(final IAcceptor<? super T> other) {
    Objects.requireNonNull(other);
    return (t) -> accept(t) && other.accept(t);
  }

  default IAcceptor<T> or(final IAcceptor<? super T> other) {
    Objects.requireNonNull(other);
    return (t) -> accept(t) || other.accept(t);
  }

}
