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

package net.anwiba.commons.lang.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ObjectList<T> implements IMutableObjectCollection<T>, IObjectList<T> {

  private final List<T> objects = new ArrayList<>();

  public ObjectList(final List<T> objects) {
    this.objects.addAll(objects);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void add(final T... factory) {
    this.objects.addAll(Arrays.asList(factory));
  }

  @Override
  public Iterator<T> iterator() {
    return this.objects.iterator();
  }

  @Override
  public int size() {
    return this.objects.size();
  }

  @Override
  public T get(final int index) {
    return this.objects.get(index);
  }

  @Override
  public Stream<T> stream() {
    return this.objects.stream();
  }

}
