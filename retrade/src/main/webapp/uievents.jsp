<%@page pageEncoding = "UTF-8" contentType = "text/javascript;charset=UTF-8"%>

<%@page import = "com.tverts.endure.msg.GetMsg"%>
<%@page import = "com.tverts.support.SU"%>

<%

GetMsg get   = com.tverts.spring.SpringPoint.bean(GetMsg.class);
String task  = SU.s2s(request.getParameter("task"));
String query = SU.s2s(request.getParameter("query"));

%>

<%

//?: {no task is given} provide the default data
if(task == null) {

%>

ReTrade.desktop.uievents.set({

 numbers: { N: 7, G: 3, O: 1, R: 1 }, newer: null, older: 58, txn: 100, firstid: 81, items: [

 { id: 105, time: '12:58', date: 'сегодня 04 Фев', color: 'N', text: 'Пользователь [ivanov] создал накладную перемещения №102/ПЕ.', script: "ZeT.log('script run 1+1 = ', 1+1)"},
 { id: 98, time: '12:35', date: 'сегодня 04 Фев', color: 'N', text: 'Пользователь [ivanov] создал накладную продажи №101/ПР.' },
 { id: 95, time: '11:20', date: 'сегодня 04 Фев', color: 'G', text: 'Накладная перемещения №100/ПЕ успешно проведена!' },
 { id: 91, time: '10:15', date: 'сегодня 04 Фев', color: 'O', text: 'Система запущена!', script: "ZeT.log('script run 2+4 = ', 2+4)" },
 { id: 87, time: '22:13', date: 'вчера 03 Фев', color: 'R', text: 'Производится остановка системы!' },

 { id: 81, time: '10:15', date: 'вчера 03 Фев', color: 'G', text: 'Добавлен пользователь [ivanov] Иванов Иван Иванович.' },
 { id: 71, time: '12:34', date: '02 Фев', color: 'G', text: 'Добавлен пользователь [navalniy] Навальный Алексей Александрович. Голосуем за Навального! Кому он не нравится: спи, красавица! Крепким вечным сном...' },
 { id: 70, time: '12:15', date: '02 Фев', color: 'N', text: 'Пользователь [System] создал накладную перемещения №98/ПЕ.' },
 { id: 68, time: '12:10', date: '02 Фев', color: 'G', text: 'Пользователь [System] создал накладную продажи №95/ПР.' },
 { id: 67, time: '12:07', date: '02 Фев', color: 'O', text: 'Пользователь [System] удалил накладную закупки №93/ЗК.' },

 { id: 65, time: '10:57', date: '02 Фев', color: 'N', text: 'Пользователь [System] создал накладную перемещения №90/ПЕ.' },
 { id: 60, time: '10:50', date: '02 Фев', color: 'N', text: 'Пользователь [System] создал накладную перемещения №87/ПЕ.' }
]})

<%
} //<-- provide the default data
%>