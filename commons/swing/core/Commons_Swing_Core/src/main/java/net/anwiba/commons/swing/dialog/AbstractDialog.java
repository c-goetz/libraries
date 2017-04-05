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
package net.anwiba.commons.swing.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.anwiba.commons.ensure.Ensure;
import net.anwiba.commons.lang.functional.IFunction;
import net.anwiba.commons.logging.ILevel;
import net.anwiba.commons.logging.ILogger;
import net.anwiba.commons.logging.Logging;
import net.anwiba.commons.message.IMessage;
import net.anwiba.commons.model.IObjectModel;
import net.anwiba.commons.model.ObjectModel;
import net.anwiba.commons.preferences.DummyPreferences;
import net.anwiba.commons.swing.preference.IWindowPreferences;
import net.anwiba.commons.swing.preference.WindowPreferences;
import net.anwiba.commons.swing.preference.WindowPrefereneceUpdatingListener;
import net.anwiba.commons.swing.utilities.GuiUtilities;
import net.anwiba.commons.swing.utilities.MessageDialogUtilities;

public abstract class AbstractDialog extends JDialog {

  public static final class ActionEnableRunner implements Runnable {
    private final Action action;
    private final boolean isEnabled;

    public ActionEnableRunner(final Action action, final boolean isEnabled) {
      this.action = action;
      this.isEnabled = isEnabled;
    }

    @Override
    public void run() {
      this.action.setEnabled(this.isEnabled);
    }
  }

  private static ILogger logger = Logging.getLogger(AbstractDialog.class.getName());
  private static final long serialVersionUID = 1L;
  private Action applyAction;
  private final JPanel buttomPanel = new JPanel();
  private final JPanel contentContainerPanel = new JPanel(new BorderLayout());
  private final JPanel detailContainerPanel = new JPanel(new BorderLayout());
  private Container contentPane;
  private final IObjectModel<IDialogResult> dialogResultModel = new ObjectModel<>(DialogResult.NONE);
  private boolean isDetailsVisible;
  private final MessagePanel messagePanel;
  private AbstractAction okAction;
  private Action tryAction;
  private final IWindowPreferences windowPreferences;
  private final WindowPrefereneceUpdatingListener updater;
  private AbstractAction cancelAction;
  private final IFunction<String, String, RuntimeException> actionButtonTextFactory;
  private final Dimension preferdSize;

  public AbstractDialog(
      final Window owner,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType,
      final boolean modal) {
    this(owner, new WindowPreferences(new DummyPreferences()), title, message, icon, dialogType, modal);
  }

  public AbstractDialog(
      final Window owner,
      final IWindowPreferences windowPreferences,
      final String title,
      final IMessage message,
      final Icon icon,
      final DialogType dialogType,
      final boolean modal) {
    this(
        owner,
        windowPreferences,
        null,
        title,
        message,
        icon,
        true,
        dialogType,
        s -> s,
        new ArrayList<>(),
        modal ? DEFAULT_MODALITY_TYPE : ModalityType.MODELESS);
  }

  public AbstractDialog(
      final Window owner,
      final IWindowPreferences windowPreferences,
      final Dimension preferdSize,
      final String title,
      final IMessage message,
      final Icon icon,
      final boolean isMessagePanelEnabled,
      final DialogType dialogType,
      final IFunction<String, String, RuntimeException> actionButtonTextFactory,
      final List<IAdditionalActionFactory> additionalActionFactories,
      final ModalityType modalityType) {
    super(owner, title, modalityType);
    this.preferdSize = preferdSize;
    Ensure.ensureArgumentNotNull(windowPreferences);
    this.windowPreferences = windowPreferences;
    this.updater = new WindowPrefereneceUpdatingListener(this, this.windowPreferences);
    this.messagePanel = isMessagePanelEnabled && message != null ? new MessagePanel(message, icon) : null;
    this.actionButtonTextFactory = actionButtonTextFactory;
    setIcon(icon);
    createView(dialogType, additionalActionFactories);
    setMessage(message);
  }

  public void locate() {
    removeComponentListener(this.updater);
    removeWindowListener(this.updater);
    if (this.windowPreferences.getBounds() == null) {
      if (this.preferdSize == null) {
        pack();
      } else {
        setSize(this.preferdSize);
      }
      GuiUtilities.center(this);
    } else {
      setBounds(this.windowPreferences.getBounds());
    }
    addComponentListener(this.updater);
    addWindowListener(this.updater);
  }

  protected void checkButton(final DataState dataState) {
    dataState.accept(new IDataStateVisitor() {

      @Override
      public void visitInvalide() {
        setChangeButtonsEnabled(false, false);
      }

      @Override
      public void visitModified() {
        setChangeButtonsEnabled(true, true);
      }

      @Override
      public void visitUnknown() {
        setChangeButtonsEnabled(false, false);
      }

      @Override
      public void visitValide() {
        setChangeButtonsEnabled(false, true);
      }
    });
  }

  final protected void createView(
      final DialogType dialogType,
      final List<IAdditionalActionFactory> additionalActionFactories) {

    this.contentPane = new JPanel();
    this.contentContainerPanel.add(this.contentPane);
    this.buttomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    final Action[] actions = getActions(dialogType, this.dialogResultModel, additionalActionFactories);
    for (final Action action : actions) {
      final JButton button = new JButton(action);
      this.buttomPanel.add(button);
    }
    this.buttomPanel.setMinimumSize(new Dimension(0, 30));
    final JPanel panel = new JPanel(new BorderLayout());
    panel.add(this.contentContainerPanel, BorderLayout.CENTER);
    panel.add(this.detailContainerPanel, BorderLayout.SOUTH);
    final JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
    if (this.messagePanel != null) {
      this.messagePanel.setMinimumSize(new Dimension(0, 40));
      mainPanel.add(BorderLayout.NORTH, this.messagePanel);
    }
    mainPanel.add(BorderLayout.CENTER, panel);
    mainPanel.add(BorderLayout.SOUTH, this.buttomPanel);
    super.setMinimumSize(new Dimension(0, 120));
    super.setContentPane(mainPanel);
  }

  protected Action[] getActions(
      final DialogType dialogType,
      @SuppressWarnings("unused") final IObjectModel<IDialogResult> resultModel,
      @SuppressWarnings("unused") final List<IAdditionalActionFactory> additionalActionFactories) {
    final IDialogTypeVisitor<Action[]> visitor = new IDialogTypeVisitor<Action[]>() {

      Action[] actions;

      @Override
      public Action[] getResult() {
        return this.actions;
      }

      @Override
      public void visitCancelApplyOk() {
        this.actions = new Action[3];
        this.actions[0] = getCancelAction();
        this.actions[1] = getApplyAction();
        this.actions[2] = getOkAction();
      }

      @Override
      public void visitCancelOk() {
        this.actions = new Action[2];
        this.actions[0] = getCancelAction();
        this.actions[1] = getOkAction();
      }

      @Override
      public void visitCancelTryOk() {
        this.actions = new Action[3];
        this.actions[0] = getCancelAction();
        this.actions[1] = getTryAction();
        this.actions[2] = getOkAction();
      }

      @Override
      public void visitClose() {
        this.actions = new Action[1];
        this.actions[0] = getCloseAction();
      }

      @Override
      public void visitCloseDetails() {
        this.actions = new Action[2];
        this.actions[0] = getCloseAction();
        this.actions[1] = getDetailsAction();
      }

      @Override
      public void visitNone() {
        this.actions = new Action[0];
      }

      @Override
      public void visitYesNo() {
        this.actions = new Action[2];
        this.actions[0] = getYesAction();
        this.actions[1] = getNoAction();
      }

      @Override
      public void visitCancel() {
        this.actions = new Action[1];
        this.actions[0] = getCancelAction();
      }
    };
    dialogType.accept(visitor);
    return visitor.getResult();
  }

  final public Action getApplyAction() {
    if (this.applyAction == null) {
      this.applyAction = new AbstractAction(this.actionButtonTextFactory.execute(DialogMessages.APPLY)) {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(final ActionEvent event) {
          try {
            apply();
          } catch (final Exception exception) {
            logger.log(ILevel.ERROR, "", exception); //$NON-NLS-1$
          }
        }
      };
      this.applyAction.setEnabled(false);
    }
    return this.applyAction;
  }

  final public Action getCancelAction() {
    if (this.cancelAction == null) {
      this.cancelAction = new AbstractAction(this.actionButtonTextFactory.execute(DialogMessages.CANCEL)) {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(final ActionEvent event) {
          try {
            if (cancel()) {
              AbstractDialog.this.dialogResultModel.set(DialogResult.CANCEL);
              close();
            }
          } catch (final Exception exception) {
            logger.log(ILevel.ERROR, "", exception); //$NON-NLS-1$
          }
        }
      };
    }
    return this.cancelAction;
  }

  final public AbstractAction getCloseAction() {
    return new AbstractAction(this.actionButtonTextFactory.execute(DialogMessages.CLOSE)) {

      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        AbstractDialog.this.dialogResultModel.set(DialogResult.NONE);
        close();
      }
    };
  }

  @Override
  public final Container getContentPane() {
    return this.contentPane;
  }

  final public Action getDetailsAction() {
    return new AbstractAction(DialogMessages.OPEN_DETAILS) {

      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        final Component detailsComponent = getDetailsComponent();
        if (AbstractDialog.this.isDetailsVisible) {
          if (detailsComponent != null) {
            getDetailContainerPanel().removeAll();
            pack();
          }
          ((JButton) e.getSource()).setText(DialogMessages.OPEN_DETAILS);
          AbstractDialog.this.isDetailsVisible = false;
          return;
        }
        if (detailsComponent != null) {
          getDetailContainerPanel().add(detailsComponent);
          pack();
        }
        ((JButton) e.getSource()).setText(DialogMessages.CLOSE_DETAILS);
        AbstractDialog.this.isDetailsVisible = true;
      }
    };
  }

  final public Action getNoAction() {
    return new AbstractAction(this.actionButtonTextFactory.execute(DialogMessages.NO)) {

      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        AbstractDialog.this.dialogResultModel.set(DialogResult.NO);
        close();
      }
    };
  }

  final protected Action getOkAction() {
    return getOkAction(this.actionButtonTextFactory.execute(DialogMessages.OK));
  }

  final protected Action getOkAction(final String title) {
    if (this.okAction == null) {
      this.okAction = new AbstractAction(title) {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(final ActionEvent event) {
          try {
            if (!apply()) {
              return;
            }
          } catch (final Exception exception) {
            logger.log(ILevel.ERROR, "", exception); //$NON-NLS-1$
          }
          AbstractDialog.this.dialogResultModel.set(DialogResult.OK);
          close();
        }
      };
    }
    return this.okAction;
  }

  final public IDialogResult getResult() {
    return this.dialogResultModel.get();
  }

  final public Action getTryAction() {
    if (this.tryAction == null) {
      this.tryAction = new AbstractAction(DialogMessages.TRY) {

        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(final ActionEvent event) {
          try {
            tryOut();
          } catch (final Exception exception) {
            logger.log(ILevel.ERROR, "", exception); //$NON-NLS-1$
          }
        }
      };
      this.tryAction.setEnabled(false);
    }
    return this.tryAction;
  }

  final public Action getYesAction() {
    return new AbstractAction(DialogMessages.YES) {

      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(final ActionEvent e) {
        AbstractDialog.this.dialogResultModel.set(DialogResult.YES);
        close();
      }
    };
  }

  final protected void setApplyEnabled(final boolean isEnabled) {
    if (this.applyAction != null) {
      GuiUtilities.invokeLater(new ActionEnableRunner(this.applyAction, isEnabled));
    }
  }

  final protected void setChangeButtonsEnabled(final boolean isApplyEnabled, final boolean isOkEnabled) {
    setApplyEnabled(isApplyEnabled);
    setOkEnabled(isOkEnabled);
  }

  @Override
  public final void setContentPane(final Container contentPane) {
    if (this.contentPane == contentPane) {
      return;
    }
    this.contentPane = contentPane;
    final JPanel containerPanel = this.contentContainerPanel;
    GuiUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        containerPanel.removeAll();
        containerPanel.add(contentPane);
        if (!containerPanel.isVisible()) {
          return;
        }
        containerPanel.validate();
        containerPanel.repaint();
      }
    });
  }

  final public void setMessage(final IMessage message) {
    if (this.messagePanel == null) {
      if (message != null) {
        GuiUtilities.invokeLater(() -> MessageDialogUtilities.show(this, getTitle(), message));
      }
      return;
    }
    GuiUtilities.invokeLater(() -> this.messagePanel.setMessage(message));
  }

  final public void setIcon(final Icon icon) {
    if (this.messagePanel == null) {
      return;
    }
    GuiUtilities.invokeLater(() -> this.messagePanel.setIcon(icon));
  }

  final public void setOkEnabled(final boolean isEnabled) {
    if (this.okAction != null) {
      GuiUtilities.invokeLater(new ActionEnableRunner(this.okAction, isEnabled));
    }
  }

  final public void setTryEnabled(final boolean isEnabled) {
    if (this.tryAction != null) {
      GuiUtilities.invokeLater(new ActionEnableRunner(this.tryAction, isEnabled));
    }
  }

  protected abstract boolean apply();

  protected abstract boolean tryOut();

  protected abstract boolean cancel();

  protected Component getDetailsComponent() {
    return null;
  }

  protected final JPanel getDetailContainerPanel() {
    return this.detailContainerPanel;
  }

  protected void close() {
    setVisible(false);
    dispose();
  }
}