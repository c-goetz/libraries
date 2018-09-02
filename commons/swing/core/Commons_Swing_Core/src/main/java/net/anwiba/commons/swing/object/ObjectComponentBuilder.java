/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels
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

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLDocument;

import net.anwiba.commons.lang.object.IObjectReceiver;
import net.anwiba.commons.lang.object.IObjectToStringConverter;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.swing.utilities.GuiUtilities;

public class ObjectComponentBuilder<T> {

  private static net.anwiba.commons.logging.ILogger logger = net.anwiba.commons.logging.Logging
      .getLogger(ObjectComponentBuilder.class);

  public static final class ObjectComponent<T> implements IObjectComponent<T> {

    private final IObjectModel<T> model;
    private final IObjectToStringConverter<T> converter;
    private final Color backgroundColor;

    public ObjectComponent(
        final IObjectModel<T> model,
        final IObjectToStringConverter<T> converter,
        final Color backgroundColor) {
      this.model = model;
      this.converter = converter;
      this.backgroundColor = backgroundColor;
    }

    @SuppressWarnings("nls")
    @Override
    public JComponent getComponent() {
      final JEditorPane area = new JEditorPane(
          "text/html", //$NON-NLS-1$
          "<html><body>" + this.converter.toString(this.model.get()) + "<body></html>"); //$NON-NLS-1$ //$NON-NLS-2$
      Optional.of(this.backgroundColor).consume(c -> area.setBackground(c));
      area.setEditable(false);
      area.setCaretPosition(0);
      if (Desktop.isDesktopSupported() && area.getDocument() instanceof HTMLDocument) {
        final Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.OPEN)) {
          area.addHyperlinkListener(hyperlinkEvent -> {
            if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
              final String descriptionString = hyperlinkEvent.getDescription();
              logger.log(ILevel.DEBUG, "href '" + descriptionString + "'"); //$NON-NLS-1$//$NON-NLS-2$
              try {
                final URL url = hyperlinkEvent.getURL();
                if (url != null) {
                  logger.log(ILevel.DEBUG, "href '" + url + "'"); //$NON-NLS-1$//$NON-NLS-2$
                  desktop.browse(url.toURI());
                } else if (descriptionString != null) {
                  final File file = new File(descriptionString);
                  final URI uri = file.getAbsoluteFile().toURI();
                  desktop.browse(uri);
                }
              } catch (final IOException | URISyntaxException exception) {
                logger.log(ILevel.WARNING, "Couldn't browse '" + descriptionString + "'"); //$NON-NLS-1$//$NON-NLS-2$
                logger.log(ILevel.WARNING, exception.getMessage(), exception);
              }
            }
          });
        }
      }
      this.model.addChangeListener(() -> GuiUtilities.invokeLater(() -> {
        area.setText("<html><body>" + this.converter.toString(this.model.get()) + "<body></html>");
        area.setCaretPosition(0);
      }));
      return area;
    }

    @Override
    public IObjectReceiver<T> getReciever() {
      return this.model;
    }
  }

  private IObjectToStringConverter<T> converter = object -> Optional.of(object).convert(o -> o.toString()).getOr(
      () -> ""); //$NON-NLS-1$
  private IObjectModel<T> model = new ObjectModel<>();
  private Color backgroundColor;

  public ObjectComponentBuilder<T> setModel(final IObjectModel<T> model) {
    this.model = model;
    return this;
  }

  public ObjectComponentBuilder<T> setBackgroundColor(final Color backgroundColor) {
    this.backgroundColor = backgroundColor;
    return this;
  }

  public ObjectComponentBuilder<T> setToStringConverter(final IObjectToStringConverter<T> converter) {
    this.converter = converter;
    return this;
  }

  public IObjectComponent<T> build() {
    return new ObjectComponent<>(this.model, this.converter, this.backgroundColor);
  }
}
