package org.basex.query.up.primitives;

import static org.basex.query.up.UpdateFunctions.*;
import org.basex.data.Data;
import org.basex.query.item.DBNode;
import org.basex.query.item.Nod;

/**
 * Delete primitive.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Lukas Kircher
 */
public final class DeletePrimitive extends UpdatePrimitive {
  /** Target node is an attribute. */
  final boolean a;

  /**
   * Constructor.
   * @param n expression target node
   */
  public DeletePrimitive(final Nod n) {
    super(n);
    a = Nod.kind(n.type) == Data.ATTR;
  }

  @Override
  public void apply(final int add) {
    final DBNode n = (DBNode) node;
    final Data d = n.data;
    final int p = n.pre + add;
    d.delete(p);
    mergeTextNodes(d, p - 1, p);
  }

  @Override
  public PrimitiveType type() {
    return PrimitiveType.DELETE;
  }

  @Override
  public byte[][] remAtt() {
    return a ? new byte[][] { node.nname() } : null;
  }
}
