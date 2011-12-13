<%@page trimDirectiveWhitespaces = 'true'%>

<%@page import = 'com.tverts.faces.ModelView'%>
<%@page import = 'com.tverts.model.DataSelectModel'%>
<%@page import = 'com.tverts.model.ModelAccessPoint'%>
<%@page import = 'com.tverts.model.ModelBean'%>
<%@page import = 'com.tverts.model.ModelPoint'%>
<%@page import = 'com.tverts.model.ModelRequest'%>
<%@page import = 'com.tverts.objects.XMAPoint'%>
<%@page import = 'static com.tverts.support.SU.s2s'%>
<%@page import = 'com.tverts.support.streams.StringBuilderWriter'%>

<%

ModelPoint point = ModelAccessPoint.model();
String     param = s2s(request.getParameter(ModelView.MODEL_PARAM));
Object     model = (param == null)?(null):(point.readBean(param));

//~: set the model request key
ModelRequest.getInstance().setKey(
  request.getParameter(ModelView.MODEL_REQ_PARAM));

//~: apply the data selection limits
if(model instanceof DataSelectModel)
{
  String  ps = s2s(request.getParameter(DataSelectModel.START_PARAM));
  String  pl = s2s(request.getParameter(DataSelectModel.LIMIT_PARAM));
  Integer s  = (ps == null)?(null):Integer.parseInt(ps);
  Integer l  = (pl == null)?(null):Integer.parseInt(pl);

  if((s != null) && (s < 0)) throw new IllegalArgumentException(
    "Data selection 'start' parameter is illegal!");

  if((l != null) && ((l <= 0) || (l > DataSelectModel.LIMIT_MAX)))
    throw new IllegalArgumentException(
      "Data selection 'limit' parameter is illegal!");

  if(s != null) ((DataSelectModel)model).setDataStart(s);
  if(l != null) ((DataSelectModel)model).setDataLimit(l);
}


//?: {model bean implements main interface} access data bean
if(model instanceof ModelBean)
{
  Object data = ((ModelBean)model).modelData();
  if(data != null) model = data;
}

//?: {no model bean provided}
if(model == null)
{
  if(param == null)
    response.sendError(404, "No model bean was specified!");
  else
    response.sendError(404, "Specified model bean was not found!");

  return;
}


//!: do XML mapping
try
{
  StringBuilderWriter sw = new StringBuilderWriter(4096);

  XMAPoint.writeObject(model, sw);
  sw.close();

  response.setContentType("application/xml;charset=UTF-8");
  out.write(sw.buffer().toString());
}
catch(Exception e)
{
  response.setStatus(500);
  throw new ServletException(e);
}

%>