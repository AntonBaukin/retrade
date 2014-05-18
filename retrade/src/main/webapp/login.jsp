<%@page session = "false" pageEncoding = "UTF-8" contentType = "text/html;charset=UTF-8"%>
<%@page import = "com.tverts.system.SystemConfig"%>
<%@page import = "com.tverts.support.SU"%>

<?xml version = '1.0' encoding = 'UTF-8'?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
  <meta http-equiv = 'Content-Type'   content = 'text/html;charset=UTF-8'/>
  <meta http-equiv = 'Cache-Control'  content = 'no-cache'/>
  <meta http-equiv = 'Pragma'         content = 'no-cache'/>

  <title>ТТС РеТрейд™ : Вход в систему</title>

<% if(SystemConfig.getInstance().isDebug()) { %>

  <script type = 'text/javascript' charset = 'UTF-8'
     src = '<%=request.getContextPath()%>/resources/jquery.js'></script>

  <script type = 'text/javascript' charset = 'UTF-8'
     src = '<%=request.getContextPath()%>/resources/jquery-ui.js'></script>

<% } else { %>

  <script type = 'text/javascript' charset = 'UTF-8'
     src = '<%=request.getContextPath()%>/resources/jquery.min.js'></script>

  <script type = 'text/javascript' charset = 'UTF-8'
     src = '<%=request.getContextPath()%>/resources/jquery-ui.min.js'></script>

<% } %>

  <script type = 'text/javascript' charset = 'UTF-8'
     src = '<%=request.getContextPath()%>/resources/sha1.js'></script>

  <script type = 'text/javascript' charset = 'UTF-8'
     src = '<%=request.getContextPath()%>/resources/login.js'></script>

  <style type ="text/css">

body
{
  background-color: #f8f8f8;
  padding: 0; margin: 0;
  width: 100%; height: 100%;
  overflow: hidden;
}

#nojs-content, #nodomain-content, #login-layout,
  #login-enter-disabled, #login-enter-enabled
{
  position: absolute;
  margin: -2.75in 0 0 -2.75in;
  top: 50%; left: 50%;
  width: 5.5in; height: 5.5in;
}

#nojs-content
{
  background: no-repeat url('<%=request.getContextPath()%>/resources/images/login/nojs.svg');
}

#nodomain-content
{
  background: no-repeat url('<%=request.getContextPath()%>/resources/images/login/nodomain.svg');
  display: none;
}

#login-base
{
  position: relative; z-index: 16;
  width: 5.5in; height: 5.5in;
  background: no-repeat url('<%=request.getContextPath()%>/resources/images/login/base.svg');
}

#login-fields-area
{
  position: absolute; z-index: 32;
  left: 0; top: 0;
  width: 5.5in; height: 3in;
}

#login-enter-disabled
{
  background: no-repeat url('<%=request.getContextPath()%>/resources/images/login/enter-disabled.svg');
  z-index: 20;
}

#login-enter-enabled
{
  background: no-repeat url('<%=request.getContextPath()%>/resources/images/login/enter-enabled.svg');
  z-index: 22;
}

#login, #password, #password-shader
{
  position: absolute; left: 1.72in;
  width: 2.04in; height: 0.25in;
  border: none; text-align: center;
  background-color: transparent;
  margin: 0; padding: 0;
  font-family: 'Georgia', serif;
  font-size: 0.208in;
}

#login.notempty, #password.notempty
{
  background-color: #f8f8f8;
}

#login
{
  top: 2.095in;
}

#password, #password-shader
{
  top: 2.535in;
}

#password-shader
{
  background-color: #f8f8f8;
}

#password.shaked
{
  background-color: transparent;
}

#login-enter, #login-enter-title
{
  position: absolute;
  left: 2.4in; top: 3.065in;
  width: 0.68in; height: 0.32in;
}

#login-enter
{
  cursor: pointer;
}

.preload-image
{
  position: absolute; overflow: hidden;
  left: -999in; top: -999in;
  height: 1in; width: 1in;
}

*:focus /* prevent field focus highlights */
{
  outline: 0;
}

::-ms-clear, ::-ms-reveal /* remove IE field actions */
{
  display: none;
}

  </style>

</head>

<body>

  <!-- [no javascript image-message -->

  <div id = "nojs-content"></div>

  <script type = "text/javascript">
    $('#nojs-content').hide()
  </script>

  <!-- no javascript image-message] -->


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


  <!-- [pre-load images] -->
  <img alt = "!" class = "preload-image"
     src = '<%=request.getContextPath()%>/resources/images/login/enter-enabled.svg'/>

</body>
</html>