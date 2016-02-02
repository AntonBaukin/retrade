<%@page session = "false" pageEncoding = "UTF-8" contentType = "text/html;charset=UTF-8" trimDirectiveWhitespaces = "true"%>
<%@page import = "com.tverts.system.SystemConfig"%>
<%@page import = "com.tverts.support.SU"%>
${"<!DOCTYPE html>"}
<html>
<head lang = 'ru'>
  <meta charset    = 'UTF-8'/>
  <meta http-equiv = 'Content-Type'     content = 'text/html;charset=UTF-8'/>
  <meta http-equiv = 'X-UA-Compatible'  content = 'IE=edge'/>
  <meta name       = 'viewport'         content = 'width=device-width, initial-scale=1, maximum-scale=8, user-scalable=yes'/>
  <meta http-equiv = 'Cache-Control'    content = 'no-cache'/>
  <meta http-equiv = 'Pragma'           content = 'no-cache'/>

  <title>ТТС РеТрейд™ :: Вход в систему</title>

  <script type = 'text/javascript' charset = 'UTF-8'
     src = '<%=request.getContextPath()%>/resources/scripts.js'></script>

<% if(SystemConfig.getInstance().isDebug()) { %>

  <script type = 'text/javascript' charset = 'UTF-8'
     src = '<%=request.getContextPath()%>/retrade/resources/login.js'></script>

  <link type = 'text/css' rel = 'stylesheet'
     href = '<%=request.getContextPath()%>/retrade/resources/login.css'/>

<% } else { %>

<% } %>
</head>
<body>

  <!-- [ No JavaScript ] -->
  <table id = "nojs-content" cellpadding = "0" cellspacing = "0" border = "0">
    <tr>
      <td>
        Вход в систему ТТС РеТрейд™ невозможен!<br/>
        Выполнение скриптов в обозревателе<br/>
        заблокировано.
      </td>
    </tr>
  </table>

  <script type = "text/javascript">
  //<![CDATA[

    $('#nojs-content').hide()

    ReTradeLogin.domain = '<%=SU.jss(request.getAttribute("retrade-domain"))%>'
    ReTradeLogin.mobile = '<%=SU.jss(request.getAttribute("retrade-mobile"))%>'
    ReTradeLogin.path   = '<%=SU.jss(request.getContextPath())%>'

  //]]>
  </script>


  <!-- [ No ReTrade Domain ] -->
  <table id = "nodomain-content" cellpadding = "0" cellspacing = "0" border = "0">
    <tr>
      <td>
        Вход в систему ТТС РеТрейд™ невозможен<br/>
        по данной ссылке!<br/>
        Обратитесь к администратору!
      </td>
    </tr>
  </table>


  <!-- [ Login Form ] -->
  <table id = "login-layout" cellpadding = "0" cellspacing = "0" border = "0">
    <tr>
      <td>
        <div id = "login-form">

          <h3 class = "text-center">Вход в систему</h3>

          <!-- [Username] -->
          <div class = "form-group has-feedback has-feedback-left">
            <input id = "login" type = "text" class = "form-control" placeholder = "Пользователь"/>
            <div class = "form-control-feedback">
              <i class = "fa fa-user text-muted"></i>
            </div>
          </div>

          <!-- [Password] -->
          <div class = "form-group has-feedback has-feedback-left">
            <input id = "password" type = "password" class = "form-control" placeholder = "Пароль"/>
            <div class = "form-control-feedback">
              <i class = "fa fa-unlock-alt text-muted"></i>
            </div>
          </div>


          <!-- [Checkbox] -->
          <div class = "form-group">
            <div class = "checkbox">
              <input id = "mobile" type = "checkbox"/>
              <label for = "mobile">мобильная версия</label>
            </div>
          </div>

          <!-- [Button] -->
          <div class = "form-group text-center">
            <button id = "button" type = "submit" class = "btn btn-default">
              Вход <i class="fa fa-arrow-circle-right position-right"></i>
            </button>
          </div>
        </div>
      </td>
    </tr>
  </table>
</body>
</html>