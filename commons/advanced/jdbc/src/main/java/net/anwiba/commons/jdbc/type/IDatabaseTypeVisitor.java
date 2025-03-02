/*
 * #%L
 * anwiba commons advanced
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
package net.anwiba.commons.jdbc.type;

public interface IDatabaseTypeVisitor<T, E extends Exception> {

  public T visitNumeric() throws E;

  public T visitDouble() throws E;

  public T visitFloat() throws E;

  public T visitShort() throws E;

  public T visitInteger() throws E;

  public T visitLong() throws E;

  public T visitBoolean() throws E;

  public T visitDate() throws E;

  public T visitVarchar() throws E;

  public T visitOther() throws E;

  public T visitStrukt() throws E;

  public T visitUnknown() throws E;

  public T visitUnsupportedType() throws E;

  public T visitChar() throws E;

  public T visitTimeStamp() throws E;

  public T visitTime() throws E;

}
