/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.lang.optional;

import net.anwiba.commons.lang.functional.IBlock;
import net.anwiba.commons.lang.functional.ISupplier;

public class If implements IIf {

  public static IIf isTrue(final boolean value) {
    return new If(value);
  }

  public static IIf isTrue(final Boolean value) {
    return new If(value == null ? false : value.booleanValue());
  }

  public static IIf defined(final Object value) {
    return new If(value != null);
  }

  private final boolean value;

  private If(final boolean value) {
    this.value = value;
  }

  @Override
  public <O> IOptional<O, RuntimeException> excecute(final ISupplier<O, RuntimeException> supplier) {
    return excecute(RuntimeException.class, supplier);
  }

  @Override
  public <O, E extends Exception> IOptional<O, E> excecute(
      final Class<E> exceptionClass,
      final ISupplier<O, E> supplier)
      throws E {
    if (this.value) {
      return Optional.of(exceptionClass, supplier.supply());
    }
    return Optional.empty(exceptionClass);
  }

  @Override
  public <E extends Exception> IIf excecute(final IBlock<E> block) throws E {
    if (this.value) {
      block.execute();
    }
    return this;
  }

  @Override
  public <O> IOptional<O, RuntimeException> or(final ISupplier<O, RuntimeException> supplier) {
    return or(RuntimeException.class, supplier);
  }

  @Override
  public <O, E extends Exception> IOptional<O, E> or(final Class<E> exceptionClass, final ISupplier<O, E> supplier)
      throws E {
    if (!this.value) {
      return Optional.of(exceptionClass, supplier.supply());
    }
    return Optional.empty(exceptionClass);
  }

  @Override
  public <E extends Exception> IIf or(final IBlock<E> block) throws E {
    if (!this.value) {
      block.execute();
    }
    return this;
  }

}
