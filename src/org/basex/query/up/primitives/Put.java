package org.basex.query.up.primitives;

import static org.basex.query.QueryText.*;
import java.io.IOException;
import org.basex.data.XMLSerializer;
import org.basex.io.PrintOutput;
import org.basex.query.QueryException;
import org.basex.query.item.Nod;
import org.basex.query.item.Uri;
import org.basex.query.util.Err;
import org.basex.util.Token;

/**
 * Put primitive.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-09, ISC License
 * @author Lukas Kircher
 */
public final class Put extends UpdatePrimitive {
  /** Put location. */
  private final Uri u;

  /**
   * Constructor.
   * @param n node to put
   * @param uri location uri
   */
  public Put(final Nod n, final Uri uri) {
    super(n);
    u = uri;
  }

  @Override
  public void apply(final int add) throws QueryException {
    // [CG] to be checked..
    // - node.pre reference might be invalid after an update
    PrintOutput out = null;
    try {
      out = new PrintOutput(Token.string(path()));
      final XMLSerializer ser = new XMLSerializer(out);
      node.serialize(ser);
      ser.close();
    } catch(IOException ex) {
      Err.or(UPFOURI, path());
    } finally {
      try { if(out != null) out.close(); } catch(final IOException ex) { }
    }
  }

  @Override
  public PrimitiveType type() {
    return PrimitiveType.PUT;
  }

  /**
   * Returns uri as string.
   * @return string uri
   */
  public byte[] path() {
    return u.str();
  }
}