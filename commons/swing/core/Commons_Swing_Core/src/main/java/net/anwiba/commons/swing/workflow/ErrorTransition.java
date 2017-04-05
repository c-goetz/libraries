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
package net.anwiba.commons.swing.workflow;

import java.awt.Component;

import net.anwiba.commons.lang.functional.IApplicable;
import net.anwiba.commons.lang.object.IObjectReceiver;
import net.anwiba.commons.swing.dialog.MessageDialogLauncher;
import net.anwiba.commons.workflow.IExecutable;
import net.anwiba.commons.workflow.state.ErrorState;
import net.anwiba.commons.workflow.state.IState;
import net.anwiba.commons.workflow.transition.ITransition;

public class ErrorTransition<T> implements ITransition<T> {

  private final IApplicable<IState<T>> applicable = s -> s != null && s instanceof ErrorState;
  private final Component parentComponent;

  public ErrorTransition(final Component parentComponent) {
    this.parentComponent = parentComponent;
  }

  @Override
  public boolean isApplicable(final IState<T> state) {
    return this.applicable.isApplicable(state);
  }

  @Override
  public IExecutable<T> getExecutable(final IState<T> state) {
    return new IExecutable<T>() {

      @Override
      public void execute(final IObjectReceiver<IState<T>> receiver) throws RuntimeException {
        final ErrorState<T> error = (ErrorState<T>) state;
        new MessageDialogLauncher()
            .title(error.getTitle())
            .text(error.getText())
            .error()
            .throwable(error.getThrowable())
            .launch(ErrorTransition.this.parentComponent);
        receiver.set(error.followingState());
      }
    };
  }

}