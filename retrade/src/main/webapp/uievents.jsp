<%@page pageEncoding = "UTF-8" contentType = "text/javascript;charset=UTF-8" trimDirectiveWhitespaces = "true"%>

<%@page import = "java.util.Calendar"%>
<%@page import = "java.util.Date"%>
<%@page import = "java.util.List"%>

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

Long[]        noids = new Long[2];
List<MsgObj>  msgs;

StringBuilder sb   = new StringBuilder(256);
Calendar      cl   = Calendar.getInstance();
Date          now  = new Date();

%>

<%!

static String numbers(MsgBox mb)
{
  return SU.cats( "N: ", mb.getTotal(), ", R: ", mb.getRed(),
    ", G: ", mb.getGreen(), ", O: ", mb.getRed() + mb.getOrange()
  );
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

//?: {no task is given} provide the default data
if(task == null) {

  //~: select the default messages
  msgs = get.loadMsgs(mbobj.getPrimaryKey(), noids, null, 'N');

%>

ReTrade.desktop.uievents.set({

 txn: <%= mbobj.getTxn()%>,
 numbers: { <%= numbers(mbox) %> },
 newer: <%= noids[0]%>, older: <%= noids[1]%>,
 items: [ <% for(int i = 0;(i < msgs.size());i++) { %>
   {<%= item(sb, cl, now, msgs.get(i))%>}<%= (i+1 < msgs.size())?(","):("")%>
 <% } %>
]})

<%
  return;
} //<-- provide the default data
%>