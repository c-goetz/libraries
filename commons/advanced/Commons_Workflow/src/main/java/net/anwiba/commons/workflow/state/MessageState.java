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

package net.anwiba.commons.workflow.state;

import net.anwiba.commons.message.IMessage;

public class MessageState<T> implements IState<T> {

  private final IState<T> followingState;
  private final IMessage message;
  private final String title;

  public MessageState(final String title, final IMessage message, final IState<T> followingState) {
    this.title = title;
    this.message = message;
    this.followingState = followingState;
  }

  @Override
  public IStateType getStateType() {
    return StateType.MESSAGE;
  }

  @Override
  public T getContext() {
    return null;
  }

  public IMessage getMessage() {
    return this.message;
  }

  public IState<T> followingState() {
    return this.followingState;
  }

  public String getTitle() {
    return this.title;
  }

}
