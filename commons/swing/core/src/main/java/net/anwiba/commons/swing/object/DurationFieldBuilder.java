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

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.function.Function;

import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.icons.gnome.contrast.high.ContrastHightIcons;
import net.anwiba.commons.utilities.validation.IValidationResult;

public class DurationFieldBuilder extends
    AbstractAlgebraicObjectFieldBuilder<Duration, DurationFieldConfigurationBuilder, DurationFieldBuilder> {

  public DurationFieldBuilder() {
    super(new DurationFieldConfigurationBuilder());
  }

  @Override
  protected AbstractObjectTextField<Duration> create(final IObjectFieldConfiguration<Duration> configuration) {
    return new DurationField(configuration);
  }

  public DurationFieldBuilder addSliderActions(
      final Duration minimum,
      final Duration maximum,
      final long step,
      final TemporalUnit unit) {
    return addSliderActions(minimum, maximum, step, new ObjectModel<>(unit));
  }

  public DurationFieldBuilder addSliderActions(
      final Duration minimum,
      final Duration maximum,
      final long step,
      final IObjectModel<TemporalUnit> unit) {
    addValidatorFactory(
        converter -> value -> Optional
            .of(value) //
            .convert(v1 -> converter.convert(v1))
            .convert(v2 -> isValid(v2, minimum, maximum))
            .getOr(() -> IValidationResult.valid()));

    final Function<Duration, Duration> add = input -> Optional.of(input).convert(v1 -> {
      return v1.plus(step, unit.get());
    })
        .convert(
            v2 -> v2.compareTo(minimum) < 0
                ? minimum
                : v2)
        .convert(
            v3 -> v3.compareTo(maximum) > 0
                ? maximum
                : v3)
        .getOr(() -> minimum);

    final Function<Duration, Duration> minus = input -> Optional.of(input).convert(v1 -> {
      return v1.minus(step, unit.get());
    })
        .convert(
            v2 -> v2.compareTo(minimum) < 0
                ? minimum
                : v2)
        .convert(
            v3 -> v3.compareTo(maximum) > 0
                ? maximum
                : v3)
        .getOr(() -> maximum);

    final Function<Duration, Boolean> minusEnabler = input -> Optional
        .of(input)
        .convert(v4 -> v4.compareTo(minimum) > 0 && minimum.compareTo(maximum) < 0)
        .getOr(() -> true);

    addButtonFactory(createButton(ContrastHightIcons.MINUS, minus, minusEnabler, 250, 100));

    final Function<Duration, Boolean> addEnabler = input -> Optional
        .of(input)
        .convert(v4 -> v4.compareTo(maximum) < 0 && minimum.compareTo(maximum) < 0)
        .getOr(() -> true);

    addButtonFactory(createButton(ContrastHightIcons.ADD, add, addEnabler, 250, 100));
    return this;
  }

  public IValidationResult isValid(final Duration value, final Duration minimum, final Duration maximum) {
    return value.compareTo(minimum) < 0 //
        ? IValidationResult.inValid(value + " < " + minimum) //$NON-NLS-1$
        : value.compareTo(maximum) > 0
            ? //
            IValidationResult.inValid(value + " > " + maximum) //$NON-NLS-1$
            : //
            IValidationResult.valid();
  }
}
