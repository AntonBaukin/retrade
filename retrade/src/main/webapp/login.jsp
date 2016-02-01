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

<% if(SystemConfig.getInstance().isDebug()) { %>

  <script type = 'text/javascript' charset = 'UTF-8'
     src = '<%=request.getContextPath()%>/resources/sha1.js'></script>

  <script type = 'text/javascript' charset = 'UTF-8'
     src = '<%=request.getContextPath()%>/resources/jquery.js'></script>

  <script type = 'text/javascript' charset = 'UTF-8'
     src = '<%=request.getContextPath()%>/resources/login.js'></script>

  <link type = 'text/css' rel = 'stylesheet'
     href = '<%=request.getContextPath()%>/resources/login.css'/>

<% } else { %>

<% } %>
</head>
<body>

  <!-- [no javascript] -->
  <div id = "nojs-content"></div>
  <script type = "text/javascript">
    $('#nojs-content').hide()
  </script>


  <!-- [no domain image-message -->

  <div id = "nodomain-content" style = "display: none;"></div>

  <script type = "text/javascript">
  //<![CDATA[

    var RETRADE_DOMAIN = '<%=SU.jss(request.getAttribute("retrade-domain-name"))%>';

    if(!RETRADE_DOMAIN.length)
      $('#nodomain-content').show()

  //]]>
  </script>

  <!-- no domain image-message] -->


  <!-- [login form -->

  <div id = "login-layout" style = "display: none;">

    <div id = "login-base"></div>

    <div id = "login-enter-disabled"></div>
    <div id = "login-enter-enabled" style = "display: none;"></div>

    <div id = "login-fields-area">

      <input id = "login" type = "text" tabindex = "1"
        title = "Имя пользователя"/>

      <div id = "password-shader" style = "display: none;"></div>
      <input id = "password" type = "password" tabindex = "2"
        title = "Пароль"/>

      <div id = "login-enter-title" title = "Введите имя пользователя и пароль!"></div>
      <div id = "login-enter" tabindex = "3" style = "display: none;"
        title = "Нажмите, чтобы войти в систему..."></div>

    </div>
  </div>

  <script type = "text/javascript">
  //<![CDATA[

    if(RETRADE_DOMAIN.length)
      $('#login-layout').show()

    function login_password_activate()
    {
      $('#login, #password').each(function()
      {
        var s = $.trim($(this).val());

        if(s.length) $(this).addClass('notempty')
        else $(this).removeClass('notempty')
      })

      enter_activate()
    }

    function enter_activate()
    {
      var enabled = ($('#login.notempty, #password.notempty').length == 2);

      $('#login-enter').toggle(enabled)
      $('#login-enter-title').toggle(!enabled)

      $('#login-enter-disabled').toggle(!enabled)
      $('#login-enter-enabled').toggle(enabled)
    }

    function enter_try()
    {
      var l = $.trim($('#login').val());
      var p = $.trim($('#password').val());

      if(!l.length || !p.length)
      {
        login_password_activate()
        return
      }

      //!: invoke secured login procedure
      var token = ReTradeLogin.login({
        login: l, password: p, domain: RETRADE_DOMAIN
      })

      //?: {not logged in} shake the password
      if(!token)
      {
        $('#password-shader').show()

        $('#password').addClass('shaked').
          effect('shake', { distance: 10, times: 2 }, function()
          {
            $('#password-shader').hide()
            $(this).removeClass('shaked')
          })

        return
      }

      //~: send web-session bind request
      $.ajax({ url: window.location.href.toString(),
        async : false, data: { 'retrade-session-bind': token.bind }
      })

      //~: reload the requested page
      var loc = window.location.toString();

      //?: {had requested login page directly} go to index
      if(loc.indexOf('/go/login/') != 0)
        window.location = '<%=request.getContextPath()%>/go/index';
      else
        window.location.reload()
    }

    //~: activate login, password
    $('#login, #password').on('keydown keyup focus blur', login_password_activate).
      on('cut paste', function() { setTimeout($.proxy(login_password_activate, this, arguments), 100) })

    //~: try enter
    $('#login-enter').on('click', enter_try)
    $('#login, #password, #login-enter').on('keypress', function(e)
    {
      if(e.which == 13) enter_try()
    })

    //~: initial activation
    $(document).ready(login_password_activate)

  //]]>
  </script>

  <!-- login form] -->

</body>
</html>