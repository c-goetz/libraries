/*
 * #%L
 * anwiba commons database
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
package net.anwiba.database.postgresql;

public interface IPostgresqlTypeVisitor<T, E extends Exception> {

  public void visitUnknown() throws E;

  public void visitUnsupportedType() throws E;

  public void visitShort() throws E;

  public void visitInteger() throws E;

  public void visitLong() throws E;

  public void visitFloat() throws E;

  public void visitDouble() throws E;

  public void visitVarchar() throws E;

  public void visitBoolean() throws E;

  public T result();

}
