/*
 * #%L
 * anwiba commons swing
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
package net.anwiba.commons.swing.object;

import java.math.BigDecimal;

import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.commons.utilities.validation.IValidationResult;
import net.anwiba.commons.utilities.validation.IValidator;

public class NumberFieldConfigurationBuilder
    extends
    AbstractObjectFieldConfigurationBuilder<Number, NumberFieldConfigurationBuilder> {

  public NumberFieldConfigurationBuilder() {
    super(new IValidator<String>() {

      @Override
      public IValidationResult validate(final String value) {
        return IValidationResult.valid();
      }
    }, new IConverter<String, Number, RuntimeException>() {

      @Override
      public Number convert(final String number) {
        if (StringUtilities.isNullOrTrimmedEmpty(number)) {
          return null;
        }
        return new BigDecimal(number);
      }
    }, new IConverter<Number, String, RuntimeException>() {

      @Override
      public String convert(final Number number) {
        if (number == null) {
          return null;
        }
        return number.toString();
      }
    });
  }

}
