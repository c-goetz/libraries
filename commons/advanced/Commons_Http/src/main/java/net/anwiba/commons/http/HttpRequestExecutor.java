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
package net.anwiba.commons.http;

import java.io.IOException;
import java.text.MessageFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

import net.anwiba.commons.http.apache.HttpContextFactory;
import net.anwiba.commons.http.apache.RequestToHttpUriRequestConverter;
import net.anwiba.commons.lang.functional.IWatcher;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.thread.cancel.ICanceler;

@SuppressWarnings({ "nls" })
public class HttpRequestExecutor implements IHttpRequestExecutor {

  private static ILogger logger = Logging.getLogger(HttpRequestExecutor.class.getName());
  private CloseableHttpClient client;
  private IHttpClientFactory httpClientFactory;
  private boolean isClosed = false;
  private final RequestToHttpUriRequestConverter requestToHttpUriRequestConverter;
  private final HttpContextFactory httpContectFactory;

  HttpRequestExecutor(final HttpConnectionMode httpConnectionMode, final IHttpClientFactory httpClientFactory) {
    this.requestToHttpUriRequestConverter = new RequestToHttpUriRequestConverter(httpConnectionMode);
    this.httpContectFactory = new HttpContextFactory();
    this.httpClientFactory = httpClientFactory;
  }

  @Override
  public IResponse execute(final ICanceler canceler, final IRequest request) throws InterruptedException, IOException {
    if (this.isClosed) {
      throw new IOException("executor is closed");
    }
    final HttpUriRequest method = this.requestToHttpUriRequestConverter.convert(request);
    try {
      logger.log(ILevel.DEBUG, () -> MessageFormat.format("request url: <{0}>", method.getURI()));
      canceler.check();
      this.client = this.client == null ? this.httpClientFactory.create() : this.client;
      final HttpResponse httpResponse = query(canceler, this.client, request, method);
      if (HttpConnectionMode.CLOSE.equals(this.httpClientFactory.getClientConfiguration().getMode())) {
        final Response response = new Response(canceler, this.client, method, httpResponse);
        this.client = null;
        return response;
      }
      return new Response(canceler, () -> {
      }, method, httpResponse);
    } catch (final IllegalStateException exception) {
      logger.log(ILevel.ALL, exception.getMessage(), exception);
      throw new InterruptedException(exception.getMessage());
    }
  }

  private HttpResponse query(
      final ICanceler canceler,
      final CloseableHttpClient httpClient,
      final IRequest request,
      final HttpUriRequest method)
      throws IOException,
      ClientProtocolException {
    try (IWatcher watcher = canceler.watcherFactory().create(() -> method.abort())) {
      if (request.getAuthentication() != null) {
        return httpClient.execute(method, this.httpContectFactory.create(request));
      }
      return httpClient.execute(method);
    }
  }

  @Override
  public void close() throws IOException {
    if (this.isClosed) {
      return;
    }
    try {
      if (this.client == null) {
        return;
      }
      this.client.close();
    } finally {
      this.isClosed = true;
      this.httpClientFactory = null;
      this.client = null;
    }
  }
}
