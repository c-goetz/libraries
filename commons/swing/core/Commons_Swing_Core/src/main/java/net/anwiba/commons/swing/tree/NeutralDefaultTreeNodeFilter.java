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
package net.anwiba.commons.swing.tree;

import javax.swing.tree.TreeNode;

public class NeutralDefaultTreeNodeFilter implements IDefaultTreeNodeFilter {

  @Override
  public TreeNode getChild(final TreeNode parent, final int index) {
    return parent.getChildAt(index);
  }

  @Override
  public int getIndexOfChild(final TreeNode parent, final TreeNode child) {
    return parent.getIndex(child);
  }

  @Override
  public int getChildCount(final TreeNode parent) {
    return parent.getChildCount();
  }
}