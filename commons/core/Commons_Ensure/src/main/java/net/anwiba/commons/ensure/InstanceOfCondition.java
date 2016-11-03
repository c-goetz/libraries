/*
 * #%L
 * anwiba commons core
 * %%
 * Copyright (C) 2007 - 2016 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.commons.ensure;

import java.text.MessageFormat;

class InstanceOfCondition extends AbstractCondition<Object> {

  private final Class<?> clazz;

  InstanceOfCondition(final Class<?> clazz) {
    this.clazz = clazz;
  }

  @Override
  public boolean accept(final Object value) {
    Ensure.ensureArgumentNotNull(value);
    return this.clazz.isAssignableFrom(value.getClass());
  }

  @Override
  public String toText() {
    return MessageFormat.format("instanceof {0}", this.clazz.getName()); //$NON-NLS-1$
  }
}
