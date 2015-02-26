/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat (TomEE)/7.0.55 (1.7.1)
 * Generated at: 2015-02-26 11:24:32 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.tverts.endure.msg.GetMsg;
import com.tverts.endure.msg.Message;
import com.tverts.endure.msg.MsgAdapters;
import com.tverts.endure.msg.MsgBox;
import com.tverts.endure.msg.MsgBoxObj;
import com.tverts.endure.msg.MsgObj;
import com.tverts.support.EX;
import com.tverts.support.DU;
import com.tverts.support.SU;

public final class uievents_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {



static String numbers(MsgBox mb)
{
  return SU.cats( "N: ", mb.getTotal(), ", R: ", mb.getRed(),
    ", G: ", mb.getGreen(), ", O: ", mb.getOrange()
  );
}

static char   color(String x)
{
  EX.asserts(x);
  EX.assertx(java.util.Arrays.asList("N", "R", "G", "O").indexOf(x) >= 0);
  return x.charAt(0);
}

static Long   closest(Set<Long> ids, Long id, List<MsgObj> msgs)
{
  final int P = com.tverts.system.SystemConfig.INSTANCE.getUserEventsPage();

  //?: {has no messages}
  if(msgs.isEmpty()) return null;

  //?: {given id is in the list}
  if(id != null) for(MsgObj m : msgs)
    if(id.equals(m.getPrimaryKey()))
      return id;

  //~: ids max
  Long M = null; if(ids != null)
    for(Long x : ids) M = Math.max(M, x);

  //~: search for the closest page start
  Long r = null; long D = Long.MAX_VALUE;
  if((id != null) | (M != null))
    for(int i = 0;(i < msgs.size());i += P)
    {
      Long x = msgs.get(i).getPrimaryKey();
      long d = Math.abs(x - ((id != null)?(id):(M)));
      if(d < D) { D = d; r = x; }
    }

  return r;
}


static String item(StringBuilder sb, Calendar cl, Date now, MsgObj msg)
{
  Message m = msg.getOx();

  //~: the time of the message
  cl.setTime(EX.assertn(m.getTime()));

  //~: time
  sb.delete(0, sb.length());
  DU.time2str(sb, cl);
  String t = sb.toString();

  //~: date
  sb.delete(0, sb.length());
  DU.namedDateDiffStrRu(m.getTime(), sb, now, cl);
  String d = sb.toString();

  //~: message text
  String x = SU.jss(m.getTitle());

  //~: message script
  String s = MsgAdapters.msgScript(m);

  //~: encode result
  sb.delete(0, sb.length());
  sb.append("id: ").append(msg.getPrimaryKey());
  sb.append(", time: '").append(t);
  sb.append("', date: '").append(d);
  sb.append("', color: '").append(m.getColor());
  sb.append("', text: '").append(x).append("'");

  //?: {has script}
  if(!SU.sXe(s))
    sb.append(", script: ").append(SU.jss(s)).append("'");

  return sb.toString();
}


  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/javascript;charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;



GetMsg        get   = com.tverts.spring.SpringPoint.bean(GetMsg.class);
MsgBoxObj     mbobj = get.msgBox(); //sec: also, secure check
MsgBox        mbox  = mbobj.getOx();

String        task  = SU.s2s(request.getParameter("task"));
String        query = SU.s2s(request.getParameter("query"));

Long          first = null;
Long[]        noids = new Long[2];
List<MsgObj>  msgs  = null;

StringBuilder sb    = new StringBuilder(256);
Calendar      cl    = Calendar.getInstance();
Date          now   = new Date();

//~: decode the query string
Long id = null; char color = 'N';
if((query != null) && query.startsWith(">"))
{
  //~: >ID
  int    i = query.indexOf(' ');     EX.assertx(i > 0);
  String x = query.substring(1, i);  EX.asserts(x);

  //?: {id != ?}
  if(!"?".equals(x))
  {
    id = Long.parseLong(x);
    EX.assertx(id > 0L);
  }

  //~: >ID C;
  int    j = query.indexOf(';', i);
  EX.assertx((j > 0) && (i + 2 <= j));
  x = query.substring(i + 1, j);
  if(!x.isEmpty()) color = color(x);

  //~: the rest of the query
  query = SU.s2s(query.substring(j + 1));
}



//?: {default | fetch | delete}
if(task == null || "fetch".equals(task) || "delete".equals(task) || "filter".equals(task)) {

  //?: {no task is given} provide the default data
  if(task == null)
    msgs = get.loadMsgs(mbobj.getPrimaryKey(), noids, null, 0, 'N');
  //?: {fetch task} load in the direction
  else if("fetch".equals(task))
  {
    EX.assertx("older".equals(query) || "newer".equals(query));
    int pages = ("older".equals(query))?(+2):(-2);

    //~: select the messages around
    msgs = get.loadMsgs(mbobj.getPrimaryKey(), noids, id, pages, color);
  }
  //?: {delete task} do, them load
  else if("delete".equals(task))
  {
    //~: decode the ids
    String[]      codes = SU.s2aws(EX.asserts(query));
    HashSet<Long> ids   = new HashSet<Long>(codes.length);
    EX.asserte(codes); for(String c : codes)
      ids.add(Long.parseLong(c));

    //~: remove the items
    get.remove(mbobj, ids);

    //~: select the messages around
    msgs = get.loadMsgs(mbobj.getPrimaryKey(), noids, id, 0, color);

    //~: find first position
    first = closest(ids, id, msgs);
  }
  //?: {apply filter}
  else if("filter".equals(task))
  {
    //~: select the messages around
    msgs = get.loadMsgs(mbobj.getPrimaryKey(), noids, id, 0, color);

    //~: find first position
    first = closest(null, id, msgs);
  }

  //~: reset first id
  if((id != null) && id.equals(first)) first = null;

      out.write("\n");
      out.write("\n");
      out.write("ReTrade.desktop.uievents.set({ txn: ");
      out.print( mbobj.getTxn());
      out.write(',');
      out.write('\n');
      out.write(' ');
      out.print( SU.catif(first, " firstid: ", first, ","));
      out.write("\n");
      out.write(" numbers: { ");
      out.print( numbers(mbox) );
      out.write(" }, filter: '");
      out.print( color);
      out.write("',\n");
      out.write(" newer: ");
      out.print( noids[0]);
      out.write(", older: ");
      out.print( noids[1]);
      out.write(",\n");
      out.write(" items: [ ");
 for(int i = 0;(i < msgs.size());i++) { 
      out.write("\n");
      out.write("   {");
      out.print( item(sb, cl, now, msgs.get(i)));
      out.write('}');
      out.print( (i+1 < msgs.size())?(","):(""));
 } 
      out.write("\n");
      out.write("]})\n");
      out.write("\n");

  return;
} //<-- default data | fetch | delete | filter


//?: {color}
if("color".equals(task)) {

  String[] codes = SU.s2aws(EX.asserts(query));
  EX.asserte(codes); EX.assertx(codes.length%2 == 0);

  //~: decode the colors from the query
  HashMap<Long, Character> colors = new HashMap<Long, Character>(7);
  for(int i = 0;(i < codes.length);i+=2)
    colors.put(Long.parseLong(codes[i]), color(codes[i+1]));

  //~: assign the colors
  get.setColors(mbobj, colors);

      out.write("\n");
      out.write("\n");
      out.write("ReTrade.desktop.uievents.numbers({\n");
      out.write("  numbers: { ");
      out.print( numbers(mbox) );
      out.write(" }\n");
      out.write("})\n");
      out.write("\n");

  return;
} //<-- color

    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try {
            if (response.isCommitted()) {
              out.flush();
            } else {
              out.clearBuffer();
            }
          } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }
}
