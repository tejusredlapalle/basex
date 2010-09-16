package org.basex.test.w3c;

import static org.basex.core.Text.*;
import java.io.File;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.Command;
import org.basex.core.Prop;
import org.basex.core.cmd.Close;
import org.basex.core.cmd.CreateDB;
import org.basex.data.Data;
import org.basex.data.Nodes;
import org.basex.query.QueryProcessor;
import org.basex.util.TokenBuilder;
import org.basex.util.Util;

/**
 * XML Conformance Test Suite wrapper.
 *
 * @author Workgroup DBIS, University of Konstanz 2005-10, ISC License
 * @author Christian Gruen
 */
public final class XMLTS {
  /** Root directory. */
  private static final String ROOT = "/home/dbis/xml/xmlts/";
    // "h:/xmlts/";
  /** Path to the XQuery Test Suite. */
  private static final String FILE = ROOT +
    "oasis/oasis.xml";
    //"sun/sun-not-wf.xml";
    //"ibm/ibm_oasis_not-wf.xml";
    //"xmltest/xmltest.xml";
  /** Path to the XQuery Test Suite. */
  private static final String PATH = FILE.replaceAll("[^/]+$", "");

  /** Verbose flag. */
  private boolean verbose;
  /** Data reference. */
  private Data data;
  /** Context. */
  private Context context;

  /**
   * Main method of the test class.
   * @param args command-line arguments
   * @throws Exception exception
   */
  public static void main(final String[] args) throws Exception {
    new XMLTS(args);
  }

  /**
   * Constructor.
   * @param args command-line arguments
   * @throws Exception exception
   */
  private XMLTS(final String[] args) throws Exception {
    // modifying internal query arguments...
    for(final String arg : args) {
      if(arg.equals("-v")) {
        verbose = true;
      } else {
        Util.outln("\nXML Conformance Tests\n -v verbose output");
        return;
      }
    }

    context = new Context();
    context.prop.set(Prop.TEXTINDEX, false);
    context.prop.set(Prop.ATTRINDEX, false);
    //context.prop.set(MAINMEM, true);

    new CreateDB("oasis", FILE).execute(context);
    data = context.data;

    int ok = 0;
    int wrong = 0;

    final Nodes root = new Nodes(0, data);
    Util.outln("\nXML Conformance Tests\n");
    Util.outln("file = (expected result) -> " + NAME + " result");

    for(final int t : nodes("//*:TEST", root).nodes) {
      final Nodes srcRoot = new Nodes(t, data);
      final String uri = text("@URI", srcRoot);
      final boolean valid = text("@TYPE", srcRoot).equals("valid");

      context.prop.set(Prop.INTPARSE, true);
      Command cmd = new CreateDB(uri, PATH + uri);
      boolean success = true;
      try {
        cmd.execute(context);
      } catch(final BaseXException ex) {
        success = false;
      }
      final boolean correct = valid == success;

      if(verbose || !correct) {
        Util.outln(uri + " = " + (valid ? "correct" : "wrong") + " -> " +
            (success ? "correct" : "wrong") + (correct ? " (OK)" : " (WRONG)"));
        if(verbose) {
          String inf = cmd.info();
          if(!inf.isEmpty()) Util.outln("[BASEX ] " + inf);
          context.prop.set(Prop.INTPARSE, false);
          new Close().execute(context);
          cmd = new CreateDB(uri, PATH + uri);
          cmd.execute(context);
          inf = cmd.info();
          if(!inf.isEmpty()) Util.outln("[XERCES] " + inf);
        }
      }
      if(correct) ++ok;
      else ++wrong;

      new Close().execute(context);
    }

    Util.outln("\nResult of Test \"" + new File(FILE).getName() + "\":");
    Util.outln("Successful: " + ok);
    Util.outln("Wrong: " + wrong);
  }

  /**
   * Returns the resulting query text (text node or attribute value).
   * @param qu query
   * @param root root node
   * @return attribute value
   * @throws Exception exception
   */
  private String text(final String qu, final Nodes root) throws Exception {
    final Nodes n = nodes(qu, root);
    final TokenBuilder tb = new TokenBuilder();
    for(int i = 0; i < n.size(); ++i) {
      if(i != 0) tb.add("/");
      tb.add(data.atom(n.nodes[i]));
    }
    return tb.toString();
  }

  /**
   * Returns the resulting query nodes.
   * @param qu query
   * @param root root node
   * @return attribute value
   * @throws Exception exception
   */
  private Nodes nodes(final String qu, final Nodes root) throws Exception {
    return new QueryProcessor(qu, root, context).queryNodes();
  }
}
