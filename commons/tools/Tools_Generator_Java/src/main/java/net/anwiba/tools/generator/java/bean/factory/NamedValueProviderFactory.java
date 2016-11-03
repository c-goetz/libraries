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
package net.anwiba.tools.generator.java.bean.factory;

import net.anwiba.tools.generator.java.bean.configuration.Annotation;
import net.anwiba.tools.generator.java.bean.configuration.NamedValueProvider;
import net.anwiba.tools.generator.java.bean.configuration.Type;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JVar;

import static net.anwiba.tools.generator.java.bean.JavaConstants.*;
import static net.anwiba.tools.generator.java.bean.configuration.Builders.*;
import static net.anwiba.tools.generator.java.bean.factory.SourceFactoryUtilities.*;

public class NamedValueProviderFactory extends AbstractSourceFactory {

  public NamedValueProviderFactory(final JCodeModel codeModel) {
    super(codeModel);
  }

  public void create(final JDefinedClass instance, final NamedValueProvider configuration, final JFieldVar field) {
    final Type type = configuration.getType();
    if (configuration.isInterfaceEnabled()) {
      instance._implements(_class(NAMED_VALUE_PROVIDER, type.name(), JAVA_LANG_RUNTIME_EXCEPTION));
    }
    if (configuration.isNameGetterEnabled()) {
      nameGetter(
          instance,
          configuration.isInterfaceEnabled(),
          configuration.getNamesMethodName(),
          configuration.getNamesMethodAnnotations(),
          field);
    }
    valueGetter(
        instance,
        configuration.isInterfaceEnabled(),
        configuration.getValueGetterMethodName(),
        configuration.getValueGetterMethodAnnotations(),
        type,
        field);
  }

  private void valueGetter(
      final JDefinedClass instance,
      final boolean isInterfaceEnabled,
      final String methodName,
      final Iterable<Annotation> annotations,
      final Type type,
      final JFieldVar field) {
    final JMethod method = instance.method(JMod.PUBLIC, _class(type, true), methodName);
    final JVar param = method.param(JMod.FINAL, _type(JAVA_LANG_STRING), "name"); //$NON-NLS-1$
    annotate(method, annotations);
    if (isInterfaceEnabled) {
      annotate(method, annotation(JAVA_LANG_OVERRIDE).build());
    }
    createAddIfNullReturnNullClosure(method).execute(param);
    method.body()._return(JExpr.refthis(field.name()).invoke("get").arg(param)); //$NON-NLS-1$
  }

  private void nameGetter(
      final JDefinedClass instance,
      final boolean isInterfaceEnabled,
      final String methodName,
      final Iterable<Annotation> annotations,
      final JFieldVar field) {
    final JMethod method = instance.method(JMod.PUBLIC, _type(JAVA_LANG_ITERABLE, JAVA_LANG_STRING), methodName);
    annotate(method, annotations);
    if (isInterfaceEnabled) {
      annotate(method, annotation(JAVA_LANG_OVERRIDE).build());
    }
    method.body()._return(JExpr.refthis(field.name()).invoke("keySet")); //$NON-NLS-1$
  }

}
