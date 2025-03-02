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
package net.anwiba.commons.datasource.connection;

import net.anwiba.commons.datasource.DataSourceType;
import net.anwiba.commons.datasource.DataSourceVersion;

public abstract class AbstractConnectionDescription implements IConnectionDescription {

  private static final long serialVersionUID = 4526701113430062810L;
  private DataSourceVersion version;
  final DataSourceType dataSourceType;

  public AbstractConnectionDescription(final DataSourceType dataSourceType) {
    this.dataSourceType = dataSourceType;
  }

  public void setVersion(final DataSourceVersion version) {
    this.version = version;
  }

  @Override
  public DataSourceVersion getVersion() {
    return this.version;
  }

  @Override
  public DataSourceType getDataSourceType() {
    return this.dataSourceType;
  }

}