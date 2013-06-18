<%@page trimDirectiveWhitespaces = 'true'%>

<%@page import = 'static com.tverts.spring.SpringPoint.beanOrNull'%>
<%@page import = 'com.tverts.faces.ModelView'%>
<%@page import = 'com.tverts.model.DataSelectModel'%>
<%@page import = 'com.tverts.model.ModelAccessPoint'%>
<%@page import = 'com.tverts.model.ModelBean'%>
<%@page import = 'com.tverts.model.ModelPoint'%>
<%@page import = 'com.tverts.model.ModelProvider'%>
<%@page import = 'com.tverts.model.ModelRequest'%>
<%@page import = 'com.tverts.objects.XMAPoint'%>
<%@page import = 'static com.tverts.support.SU.s2s'%>
<%@page import = 'com.tverts.support.streams.StringBuilderWriter'%>

<%

//~: set the model request key
ModelRequest.getInstance().setKey(
  request.getParameter(ModelView.MODEL_REQ_PARAM));

String    param = s2s(request.getParameter(ModelView.MODEL_PROVIDER));
ModelBean model = null;
Object    data  = null;

//?: {has model provider}
if(param != null)
{
  Object provider = beanOrNull(param);

  //?: {has provider} get the model
  if(provider instanceof ModelProvider)
    model = ((ModelProvider)provider).provideModel();
}

//?: {model not provided & has model parameter} access via the model point
if(model == null)
{
  param = s2s(request.getParameter(ModelView.MODEL_PARAM));

  if(param != null)
     model = ModelAccessPoint.model().readBean(param);
}


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


//?: {has model} access data bean
if(model != null)
  data = ((ModelBean)model).modelData();

//?: {no model bean provided}
if(data == null)
{
  if(param == null)
    response.sendError(404, "No model bean (or provider) were specified!");
  else
    response.sendError(404, "Specified model bean (or provider) was not found!");

  return;
}


//!: do XML mapping
try
{
  StringBuilderWriter sw = new StringBuilderWriter(4096);

  XMAPoint.writeObject(data, sw);
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