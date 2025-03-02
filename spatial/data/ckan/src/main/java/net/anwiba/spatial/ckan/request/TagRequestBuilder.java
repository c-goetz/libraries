/*
 * #%L
 *
 * %%
 * Copyright (C) 2007 - 2018 Andreas W. Bartels (bartels@anwiba.de)
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
package net.anwiba.spatial.ckan.request;

import java.text.MessageFormat;

import net.anwiba.commons.http.Authentication;
import net.anwiba.commons.http.IAuthentication;
import net.anwiba.commons.http.IRequest;
import net.anwiba.commons.http.RequestBuilder;
import net.anwiba.commons.lang.exception.CreationException;
import net.anwiba.commons.lang.optional.If;
import net.anwiba.commons.lang.optional.Optional;
import net.anwiba.commons.utilities.string.StringUtilities;
import net.anwiba.spatial.ckan.utilities.CkanUtilities;

public class TagRequestBuilder {

  public static class TagListRequestBuilder {

    private final String url;
    private String key = null;
    private IAuthentication authentication = null;
    private boolean allFields = false;

    TagListRequestBuilder(final String url) {
      this.url = url;
    }

    public TagListRequestBuilder key(@SuppressWarnings("hiding") final String key) {
      this.key = key;
      return this;
    }

    public TagListRequestBuilder authentication(final String userName, final String password) {
      if (StringUtilities.isNullOrTrimmedEmpty(userName) || StringUtilities.isNullOrTrimmedEmpty(password)) {
        return this;
      }
      this.authentication = new Authentication(userName, password);
      return this;
    }

    public TagListRequestBuilder setOnlyNameField() {
      this.allFields = false;
      return this;
    }

    public TagListRequestBuilder setAllFields() {
      this.allFields = true;
      return this;
    }

    public IRequest build() throws CreationException {
      final RequestBuilder builder =
          RequestBuilder.get(MessageFormat.format(CkanUtilities.getBaseUrl(this.url, "tag_search"), this.url)); //$NON-NLS-1$
      If.isTrue(this.allFields).excecute(() -> builder.query("all_fields", "True")); //$NON-NLS-1$ //$NON-NLS-2$
      Optional.of(this.key).convert(k -> builder.header("X-CKAN-API-Key", k)); //$NON-NLS-1$
      Optional.of(this.authentication).consume(a -> builder.authentication(a.getUsername(), a.getPassword()));
      return builder.build();
    }

  }

  public static class TagSearchRequestBuilder {

    private final String url;
    private String key = null;
    private String query = null;
    private IAuthentication authentication = null;

    private TagSearchRequestBuilder(final String url) {
      this.url = url;
    }

    public TagSearchRequestBuilder key(@SuppressWarnings("hiding") final String key) {
      this.key = key;
      return this;
    }

    public TagSearchRequestBuilder authentication(final String userName, final String password) {
      if (StringUtilities.isNullOrTrimmedEmpty(userName) || StringUtilities.isNullOrTrimmedEmpty(password)) {
        return this;
      }
      this.authentication = new Authentication(userName, password);
      return this;
    }

    public TagSearchRequestBuilder query(@SuppressWarnings("hiding") final String query) {
      this.query = query;
      return this;
    }

    public IRequest build() throws CreationException {
      final RequestBuilder builder = RequestBuilder
          .get(CkanUtilities.getBaseUrl(this.url, "tag_search")) //$NON-NLS-1$
          .query("query", this.query); //$NON-NLS-1$
      Optional.of(this.key).convert(k -> builder.header("X-CKAN-API-Key", k)); //$NON-NLS-1$
      Optional.of(this.authentication).consume(a -> builder.authentication(a.getUsername(), a.getPassword()));
      return builder.build();
    }

  }

  public static TagListRequestBuilder list(final String url) {
    return new TagListRequestBuilder(url);
  }

  public static TagSearchRequestBuilder search(final String url) {
    return new TagSearchRequestBuilder(url);
  }

}
