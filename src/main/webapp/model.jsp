<%@page trimDirectiveWhitespaces = 'true'%>

<%@page import = 'com.tverts.faces.ModelView'%>
<%@page import = 'com.tverts.model.DataSelectModel'%>
<%@page import = 'com.tverts.model.ModelAccessPoint'%>
<%@page import = 'com.tverts.model.ModelBean'%>
<%@page import = 'com.tverts.model.ModelPoint'%>
<%@page import = 'com.tverts.objects.XMAPoint'%>
<%@page import = 'static com.tverts.support.SU.s2s'%>
<%@page import = 'com.tverts.support.streams.StringBuilderWriter'%>

<%

ModelPoint point = ModelAccessPoint.model();
String     param = s2s(request.getParameter(ModelView.MODEL_PARAM));
Object     model = (param == null)?(null):(point.readBean(param));


//~: apply the data selection limits
if(model instanceof DataSelectModel)
{
  int start = Integer.parseInt(s2s(request.getParameter(DataSelectModel.START_PARAM)));
  int limit = Integer.parseInt(s2s(request.getParameter(DataSelectModel.LIMIT_PARAM)));

  if(start < 0) throw new IllegalArgumentException(
    "Data selection START parameter is illegal!");

  if((limit <= 0) || (limit > DataSelectModel.LIMIT_MAX))
    throw new IllegalArgumentException(
      "Data selection LIMIT parameter is illegal!");

  ((DataSelectModel)model).setDataStart(start);
  ((DataSelectModel)model).setDataLimit(limit);
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