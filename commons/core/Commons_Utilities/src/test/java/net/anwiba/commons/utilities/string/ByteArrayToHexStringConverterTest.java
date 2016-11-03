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

package net.anwiba.commons.utilities.string;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import net.anwiba.commons.lang.functional.ConversionException;

import org.junit.Test;

public class ByteArrayToHexStringConverterTest {

  @Test
  public void test() throws ConversionException {
    final byte[] array = new byte[]{ 1, 1, 0, 0, 0, 0, 0, 0, -64, 9, -28, 74, 65, 0, 0, 0, 64, -17, -17, 84, 65 };
    final String string = "0101000000000000C009E44A4100000040EFEF5441"; //$NON-NLS-1$
    final ByteArrayToHexStringConverter converter = new ByteArrayToHexStringConverter();
    assertThat(converter.convert(array), equalTo(string));
  }

}
