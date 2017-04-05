/*
 * #%L
 * anwiba commons tools
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

package net.anwiba.tools.icons.configuration;

public final class IconSizesConfiguration implements IIconSizesConfiguration {

  private final IconSizeConfiguration small;
  private final IconSizeConfiguration medium;
  private final IconSizeConfiguration large;
  private final String folder;

  public IconSizesConfiguration(
      final String folder,
      final IconSizeConfiguration small,
      final IconSizeConfiguration medium,
      final IconSizeConfiguration large) {
    this.folder = folder;
    this.small = small;
    this.medium = medium;
    this.large = large;
  }

  @Override
  public String getFolder() {
    return this.folder;
  }

  @Override
  public IIconSizeConfiguration small() {
    return this.small;
  }

  @Override
  public IIconSizeConfiguration medium() {
    return this.medium;
  }

  @Override
  public IIconSizeConfiguration large() {
    return this.large;
  }
}
