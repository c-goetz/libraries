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

package net.anwiba.commons.swing.table;

import java.awt.Image;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableCellEditor;

import net.anwiba.commons.lang.collection.IObjectList;
import net.anwiba.commons.lang.comparable.ComparableComparator;
import net.anwiba.commons.lang.comparable.NumberComparator;
import net.anwiba.commons.lang.functional.IAcceptor;
import net.anwiba.commons.lang.functional.IAggregator;
import net.anwiba.commons.lang.functional.IConverter;
import net.anwiba.commons.lang.functional.IFactory;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.model.IObjectDistributor;
import net.anwiba.commons.swing.list.ObjectUiCellRendererConfigurationBuilder;
import net.anwiba.commons.swing.table.action.ITableActionFactory;
import net.anwiba.commons.swing.table.action.ITableCheckActionEnabledValidator;
import net.anwiba.commons.swing.table.action.ITableTextFieldActionFactory;
import net.anwiba.commons.swing.table.action.ITableTextFieldKeyListenerFactory;
import net.anwiba.commons.swing.table.filter.IColumToStringConverter;
import net.anwiba.commons.swing.table.renderer.BooleanRenderer;
import net.anwiba.commons.swing.table.renderer.DurationTableCellRenderer;
import net.anwiba.commons.swing.table.renderer.LocalDateTimeTableCellRenderer;
import net.anwiba.commons.swing.table.renderer.NumberTableCellRenderer;
import net.anwiba.commons.swing.table.renderer.ObjectTableCellRenderer;
import net.anwiba.commons.swing.ui.IObjectUi;
import net.anwiba.commons.swing.ui.IObjectUiCellRendererConfiguration;
import net.anwiba.commons.swing.ui.ObjectUiBuilder;
import net.anwiba.commons.swing.ui.ObjectUiListCellRenderer;
import net.anwiba.commons.swing.ui.ObjectUiTableCellRenderer;

public class ObjectTableBuilder<T> implements IObjectTableBuilder<T> {

  final ObjectListTableConfigurationBuilder<T> builder = new ObjectListTableConfigurationBuilder<>();
  private final List<T> values = new ArrayList<>();

  @Override
  public IObjectTableBuilder<T> setKeyListenerFactory(final IKeyListenerFactory<T> keyListenerFactory) {
    this.builder.setKeyListenerFactory(keyListenerFactory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setSelectionMode(final int selectionMode) {
    this.builder.setSelectionMode(selectionMode);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addColumn(final IObjectListColumnConfiguration<T> columnConfiguration) {
    this.builder.addColumnConfiguration(columnConfiguration);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addStringColumn(final String title,
      final IFunction<T, String, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, new ObjectTableCellRenderer(), size, String.class, false, null));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addSortableStringColumn(final String title,
      final IFunction<T, String, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, new ObjectTableCellRenderer(), size, String.class, true, null));
    return this;
  }

  public <O> IObjectTableBuilder<T> addObjectChooseColumn(final String title,
      final IFunction<T, O, RuntimeException> provider,
      final IAggregator<T, O, T, RuntimeException> aggregator,
      final IConverter<T, String, RuntimeException> toString,
      final List<O> values,
      final Class<O> clazz,
      final int size) {
    IObjectUiCellRendererConfiguration configuration = new ObjectUiCellRendererConfigurationBuilder()
        .setHorizontalAlignmentLeft()
        .build();
    IObjectUi<T> objectUi = new ObjectUiBuilder<T>().text(toString).build();
    final JComboBox<O> comboBox = new JComboBox<>(new Vector<>(values));
    comboBox.setRenderer(new ObjectUiListCellRenderer<>(configuration, objectUi));
    final TableCellEditor editor = new DefaultCellEditor(comboBox);

    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(
        title,
        (IColumnValueProvider<T>) object -> Optional.of(object).convert(o -> provider.execute(o)).get(),
        configuration,
        objectUi,
        (IColumnValueAdaptor<T>) (object, value) -> aggregator.aggregate(object, (O) value),
        editor,
        size,
        clazz,
        false,
        null));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addEditableStringColumn(final String title,
      final IFunction<T, String, RuntimeException> provider,
      final IAggregator<T, String, T, RuntimeException> adaptor,
      final int size) {
    final DefaultCellEditor cellEditor = new DefaultCellEditor(new JTextField());
    cellEditor.setClickCountToStart(2);
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, (IColumnValueProvider<T>) object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, //
        new ObjectTableCellRenderer(),
        (object, value) -> adaptor.aggregate(object, (String) value),
        cellEditor,
        size,
        false,
        null));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addStringChooseColumn(final String title,
      final IFunction<T, String, RuntimeException> provider,
      final IAggregator<T, String, T, RuntimeException> adaptor,
      final List<String> values,
      final int size) {
    final JComboBox<String> comboBox = new JComboBox<>(values.toArray(String[]::new));
    final DefaultCellEditor cellEditor = new DefaultCellEditor(comboBox);
    cellEditor.setClickCountToStart(2);
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(
        title,
        (IColumnValueProvider<T>) object -> {
          if (object == null) {
            return null;
          }
          return provider.execute(object);
        }, //
        new ObjectTableCellRenderer(),
        (object, value) -> adaptor.aggregate(object, (String) value),
        cellEditor,
        size,
        false,
        null));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addSortableDoubleColumn(final String title,
      final IFunction<T, Double, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, new NumberTableCellRenderer("0.0000"), size, Double.class, true, new NumberComparator())); //$NON-NLS-1$
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addDoubleColumn(final String title,
      final IFunction<T, Double, RuntimeException> provider,
      final int size) {
    return addDoubleColumn(title, provider, new NumberTableCellRenderer("0.0000"), size);
  }

  @Override
  public IObjectTableBuilder<T> addDoubleColumn(final String title,
      final IFunction<T, Double, RuntimeException> provider,
      final NumberTableCellRenderer renderer,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, renderer, size, Double.class, false, new NumberComparator())); // $NON-NLS-1$
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addEditableIntegerColumn(final String title,
      final IFunction<T, Integer, RuntimeException> provider,
      final IAggregator<T, Integer, T, RuntimeException> aggregator,
      final JComponent component,
      final int size) {
    final TableCellEditor editor;
    if (component instanceof JComboBox) {
      @SuppressWarnings("unchecked")
      final JComboBox<Integer> comboBox = (JComboBox<Integer>) component;
      editor = new DefaultCellEditor(comboBox);
    } else if (component instanceof JTextField) {
      final JTextField textField = (JTextField) component;
      editor = new DefaultCellEditor(textField);
    } else {
      throw new IllegalArgumentException("Unsupported component implementation"); //$NON-NLS-1$
    }
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(
        title,
        (IColumnValueProvider<T>) object -> Optional.of(object).convert(o -> provider.execute(o)).get(),
        new ObjectUiCellRendererConfigurationBuilder().setHorizontalAlignmentRight().build(),
        new ObjectUiBuilder<Integer>().text(v -> v.toString()).build(),
        (IColumnValueAdaptor<T>) (object, value) -> aggregator.aggregate(object, (Integer) value),
        editor,
        size,
        Integer.class,
        false,
        null));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addIntegerColumn(final String title,
      final IFunction<T, Integer, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, new NumberTableCellRenderer("0"), size, Integer.class, false, new NumberComparator())); //$NON-NLS-1$
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addSortableIntegerColumn(final String title,
      final IFunction<T, Integer, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, new NumberTableCellRenderer("0"), size, Integer.class, true, new NumberComparator())); //$NON-NLS-1$
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addSortableLongColumn(final String title,
      final IFunction<T, Long, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, new NumberTableCellRenderer("0"), size, Long.class, true, new NumberComparator())); //$NON-NLS-1$
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addSortableLocalTimeDateColumn(final String title,
      final IFunction<T, LocalDateTime, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, new LocalDateTimeTableCellRenderer(), size, LocalDateTime.class, true, new ComparableComparator<>()));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addSortableDurationColumn(final String title,
      final IFunction<T, Duration, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, new DurationTableCellRenderer(), size, Duration.class, true, new ComparableComparator<>()));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addObjectColumn(final String title,
      final IFunction<T, Object, RuntimeException> provider,
      final int size) {
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addImageColumn(final String title,
      final IFunction<T, Image, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    },
        new ObjectUiTableCellRenderer<>(new ObjectUiBuilder<Image>().text(o -> null)
            .icon(o -> Optional.of(o).convert(i -> new ImageIcon(i)).get())
            .build()),
        size,
        Image.class,
        false,
        null));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addBooleanColumn(final String title,
      final IFunction<T, Boolean, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, new BooleanRenderer(), size, Boolean.class, false, null));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addSortableBooleanColumn(final String title,
      final IFunction<T, Boolean, RuntimeException> provider,
      final int size) {
    this.builder.addColumnConfiguration(new ObjectListColumnConfiguration<>(title, object -> {
      if (object == null) {
        return null;
      }
      return provider.execute(object);
    }, new BooleanRenderer(), size, Boolean.class, true, null));
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addActionFactory(final ITableActionFactory<T> factory) {
    this.builder.addActionFactory(factory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addActionFactory(final ITableActionFactory<T> factory,
      final ITableCheckActionEnabledValidator<T> validator) {
    this.builder.addActionFactory(factory, validator);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addTextFieldActionFactory(final ITableTextFieldActionFactory<T> factory) {
    this.builder.addTextFieldActionFactory(factory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setPreferredVisibleRowCount(final int preferredVisibleRowCount) {
    this.builder.setPreferredVisibleRowCount(preferredVisibleRowCount);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setHeaderMouseListenerFactory(final IMouseListenerFactory<T> mouseListenerFactory) {
    this.builder.setHeaderMouseListenerFactory(mouseListenerFactory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setTableMouseListenerFactory(final IMouseListenerFactory<T> mouseListenerFactory) {
    this.builder.setTableMouseListenerFactory(mouseListenerFactory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addAddObjectAction(final IColumnObjectFactory<T, T, RuntimeException> factory) {
    this.builder.addAddObjectAction(factory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addEditObjectAction(final IColumnObjectFactory<T, T, RuntimeException> factory) {
    this.builder.addEditObjectAction(factory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addRemoveObjectsAction() {
    this.builder.addRemoveObjectsAction();
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addMoveObjectUpAction() {
    this.builder.addMoveObjectUpAction();
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addMoveObjectDownAction() {
    this.builder.addMoveObjectDownAction();
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setFilterToStringConverter(final IColumToStringConverter columnToStringConverter) {
    this.builder.setFilterToStringConverter(columnToStringConverter);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setRowFilterDistributor(final IObjectDistributor<IAcceptor<T>> rowFilterDistributor) {
    this.builder.setRowFilterDistributor(rowFilterDistributor);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setValues(final List<T> values) {
    this.values.clear();
    this.values.addAll(values);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> addValue(final T value) {
    this.values.add(value);
    return this;
  }

  @Override
  public ObjectListTable<T> build() {
    final IObjectListTableConfiguration<T> configuration = this.builder.build();
    return new ObjectListTable<>(configuration, this.values);
  }

  @Override
  public IObjectTableBuilder<T> setSingleSelectionMode() {
    this.builder.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setAutoResizeModeOff() {
    this.builder.setAutoResizeModeOff();
    return this;
  }

  @Override
  public IObjectTableBuilder<T>
      setTextFieldKeyListenerFactory(final ITableTextFieldKeyListenerFactory<T> textFieldKeyListenerFactory) {
    this.builder.setTextFieldKeyListenerFactory(textFieldKeyListenerFactory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setAccessoryHeaderPanelFactory(
      final IFactory<IObjectTableModel<T>, JComponent, RuntimeException> accessoryHeaderPanelFactory) {
    this.builder.setAccessoryHeaderPanelFactory(accessoryHeaderPanelFactory);
    return this;
  }

  @Override
  public IObjectTableBuilder<T> setAccessoryFooterPanelFactory(
      final IFactory<IObjectTableModel<T>, JComponent, RuntimeException> accessoryFooterPanelFactory) {
    this.builder.setAccessoryFooterPanelFactory(accessoryFooterPanelFactory);
    return this;
  }

  public IObjectTableBuilder<T> setValues(final IObjectList<T> values) {
    setValues(values.stream().asList());
    return this;
  }
}
