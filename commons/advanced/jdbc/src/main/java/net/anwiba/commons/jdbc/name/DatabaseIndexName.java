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
package net.anwiba.commons.jdbc.name;

import net.anwiba.commons.lang.object.ObjectUtilities;
import net.anwiba.commons.utilities.string.StringUtilities;

public class DatabaseIndexName implements IDatabaseIndexName {

  private final String catalogName;
  private final String schemaName;
  private final String indexName;

  public DatabaseIndexName(final String schemaName, final String indexName) {
    this(null, schemaName, indexName);
  }

  public DatabaseIndexName(final String indexName) {
    this(null, null, indexName);
  }

  public DatabaseIndexName(final String catalogName, final String schemaName, final String indexName) {
    this.catalogName = catalogName;
    this.schemaName = schemaName;
    this.indexName = indexName;
  }

  @Override
  public String getCatalogName() {
    return this.catalogName;
  }

  @Override
  public String getSchemaName() {
    return this.schemaName;
  }

  @Override
  public String getIndexName() {
    return this.indexName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = prime + ((this.catalogName == null) ? 0 : this.catalogName.hashCode());
    result = prime * result + ((this.schemaName == null) ? 0 : this.schemaName.hashCode());
    return prime * result + ((this.indexName == null) ? 0 : this.indexName.hashCode());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof IDatabaseTriggerName)) {
      return false;
    }
    final IDatabaseTriggerName other = (IDatabaseTriggerName) obj;
    return ObjectUtilities.equals(this.catalogName, other.getCatalogName()) //
        && ObjectUtilities.equals(this.schemaName, other.getSchemaName()) //
        && ObjectUtilities.equals(this.indexName, other.getTriggerName());
  }

  @Override
  public String getName() {
    final StringBuilder builder = new StringBuilder();
    if (!StringUtilities.isNullOrEmpty(this.catalogName)) {
      builder.append(this.catalogName);
      builder.append("."); //$NON-NLS-1$
    }
    if (!StringUtilities.isNullOrEmpty(this.schemaName)) {
      builder.append(this.schemaName);
      builder.append("."); //$NON-NLS-1$
    }
    builder.append(this.indexName);
    return builder.toString();
  }
}