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
package net.anwiba.commons.resource.provider.test;

import net.anwiba.commons.resource.reference.ResourceReferenceHandler;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;

import static org.junit.Assert.*;

public class ResourceProvidersTest {

  @Test
  public void string() throws Exception {
    assertThat("Dies ist eine Test datei.\nHallo Welt!", equalTo(TestResourceProviders.text)); //$NON-NLS-1$
  }

  @Test
  public void array() throws Exception {
    assertThat(new String[] { "Dies ist eine Test datei.", "Hallo Welt!" }, equalTo(TestResourceProviders.textArray)); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Test
  public void textResource() throws Exception {
    assertThat("Dies ist eine Test datei.\nHallo Welt!", //$NON-NLS-1$
        equalTo(new ResourceReferenceHandler().getContent(TestResourceProviders.resourceReference)));
  }

  // @Test
  // public void fileResource() throws Exception {
  //    assertThat("Dies ist eine Test datei.\nHallo Welt!", //$NON-NLS-1$
  // equalTo(new ResourceReferenceHandler().getContent(TestResourceProviders.fileResource)));
  // }

  @Test
  public void textResourceProvider() throws Exception {
    assertThat("Dies ist eine Test datei.\nHallo Welt!".getBytes(), //$NON-NLS-1$
        equalTo(TestResourceProviders.textResource.getBytes()));
  }

  @Test
  public void textResourceStatic() throws Exception {
    assertThat("Dies ist eine Test datei.\nHallo Welt!".getBytes(), //$NON-NLS-1$
        equalTo(TestResourceProviders.staticAnnotationTextResource.getBytes()));
  }

  @Test
  public void textResourceNoneStatic() throws Exception {
    assertThat("Dies ist eine Test datei.\nHallo Welt!".getBytes(), //$NON-NLS-1$
        equalTo(TestResourceProviders.noneStaticAnnotationTextResource.getBytes()));
  }
}