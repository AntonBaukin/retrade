
var tmplt_S0 = new ZeT.Layout.Template(
 {}, //<-- in it's private area

 "<span>The is simple template!</span>"
);

var tmplt_S1 = new ZeT.Layout.Template(
 {}, //<-- in it's private area

 "<br/>Simple template with line break...<br/>"
);

new ZeT.Layout.Template( {id: 'G0', global: true},

"<span>This template is in global area.</span><span> Displayed 2 times...</span>"
);

new ZeT.Layout.Template( {id: 'G1', global: true},

  "<table cellpadding = '2pt' cellspacing = '0' border = '1'>\n"+
  "  <tr>\n"+
  "    <td>this table</td>\n"+
  "    <td>has two columns</td>\n"+
  "  </tr>\n"+
  "  <tr>\n"+
  "    <td colspan = '2'>and two rows!</td>\n"+
  "  </tr>\n"+
  "</table>"
);