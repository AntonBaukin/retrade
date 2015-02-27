<%@page pageEncoding = "UTF-8" contentType = "text/javascript;charset=UTF-8" trimDirectiveWhitespaces = "true"%>

<%@page import = "java.util.Calendar"%>
<%@page import = "java.util.Date"%>
<%@page import = "java.util.HashMap"%>
<%@page import = "java.util.HashSet"%>
<%@page import = "java.util.List"%>
<%@page import = "java.util.Set"%>

<%@page import = "com.tverts.endure.msg.GetMsg"%>
<%@page import = "com.tverts.endure.msg.Message"%>
<%@page import = "com.tverts.endure.msg.MsgAdapters"%>
<%@page import = "com.tverts.endure.msg.MsgBox"%>
<%@page import = "com.tverts.endure.msg.MsgBoxObj"%>
<%@page import = "com.tverts.endure.msg.MsgObj"%>

<%@page import = "com.tverts.support.EX"%>
<%@page import = "com.tverts.support.DU"%>
<%@page import = "com.tverts.support.SU"%>

<%

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

%>

<%!

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
%>

<%
//?: {numbers}
if("numbers".equals(task)) {
%>

ReTrade.desktop.uievents.numbers({
  txn: <%= mbobj.getTxn()%>,
  numbers: { <%= numbers(mbox) %> }
})

<%
  return;
} //<-- numbers
%>


<%
//?: {default | fetch | delete | filter}
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
    //?: {neutral} select the newest message
    if(color == 'N') id = null;

    //~: select the messages around
    msgs = get.loadMsgs(mbobj.getPrimaryKey(), noids, id, 0, color);

    //~: find first position
    first = closest(null, id, msgs);
  }

  //~: reset first id
  if((id != null) && id.equals(first)) first = null;
%>

ReTrade.desktop.uievents.set({ txn: <%= mbobj.getTxn()%>,
 <%= SU.catif(first, " firstid: ", first, ",")%>
 numbers: { <%= numbers(mbox) %> }, filter: '<%= color%>',
 newer: <%= noids[0]%>, older: <%= noids[1]%>,
 items: [ <% for(int i = 0;(i < msgs.size());i++) { %>
   {<%= item(sb, cl, now, msgs.get(i))%>}<%= (i+1 < msgs.size())?(","):("")%>
 <% } %>
]})

<%
  return;
} //<-- default data | fetch | delete | filter
%>


<%
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
%>

ReTrade.desktop.uievents.numbers({
  numbers: { <%= numbers(mbox) %> }
})

<%
  return;
} //<-- color
%>