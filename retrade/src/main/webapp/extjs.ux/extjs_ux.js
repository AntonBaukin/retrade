/*===============================================================+
 |                                                               |
 |   Ext JS User Extensions                                      |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/


Ext.define('Ext.ux.picker.Time',
{
	extend            : 'Ext.picker.Time',
	alias             : 'widget.ux.timepicker',

	createStore       : function()
	{
		var me = this,
		    utilDate = Ext.Date,
		    times = [],
		    min = me.absMin,
		    max = me.absMax,
			 time = (!me.pickerField)?(null):(me.pickerField.getValue());

		function fmt(t)
		{
			return utilDate.dateFormat(t, me.format);
		}

		function add(t)
		{
			times.push({date: t, disp: fmt(t)});
		}

		while(min <= max)
		{
			var skip = false;

			//?: {has present time value to insert}
			if(time)
			{
				skip = (fmt(time) == fmt(min));

				//?: {add this time}
				if(skip || (min > time))
				{
					add(time)
					time = null;
				}
			}

			if(!skip) add(min)
			min = utilDate.add(min, 'mi', me.increment);
		}

		return new Ext.data.Store({fields: ['disp', 'date'], data: times});
	}
})


/**
 * This special class is for buttons with render-to in menus.
 * In ExtJS 5.1 mouse pressing on them hides the menu before
 * the click event occurs.
 */
Ext.define('Ext.ux.button.Detached',
{
	extend            : 'Ext.button.Button',
	alias             : 'widget.ux.button-detached',

	onMouseDown       : function(e)
	{
		e.stopEvent()
		return this.callParent(arguments)
	}
})


/**
 * Check Box having three states (in the toggle order):
 *   null-checked, unchecked, and checked.
 */
Ext.define('Ext.ux.form.TriCheckbox',
{
	extend            : 'Ext.form.field.Checkbox',

	alias             : 'widget.ux.tricheckbox',

	xchecked          : false,
	xcheckedClass     : 'ux-form-cb-nullchecked',
	xcheckedVal       : '',
	inputValue        : 'true',
	uncheckedVal      : 'false',

	getSubmitValue    : function()
	{
		if(this.xchecked) return this.xcheckedVal;
		return this.callParent(arguments)
	},

	getValue          : function()
	{
		return (this.xchecked)?(null):(this.callParent(arguments));
	},

	setRawValue       : function(v)
	{
		if(this.xcheckedVal === v)
		{
			this.xchecked = true;
			v = false;
		}

		if(!this.xchecked)
		{
			this.removeCls(this.xcheckedClass)
			return this.callParent([v])
		}
		else
		{
			this.callParent([v])
			this.addCls(this.xcheckedClass)
			return false;
		}
	},

	onBoxClick        : function(e)
	{
		if(this.disabled || this.readOnly) return;

		//?: {checked -> null-checked}
		if(this.checked)
		{
			this.xchecked = true;
			this.setValue(false)
		}
		//?: {null-checked -> unchecked}
		else if(this.xchecked)
		{
			this.xchecked = false;
			this.setValue(false)
		}
		//?: {unchecked -> checked}
		else
			this.setValue(true)
	}
})


Ext.define('Ext.ux.selection.No',
{
	extend            : 'Ext.selection.RowModel',

	doSelect          : function()
	{},

	doMultiSelect     : function()
	{}
})


/**
 * Action column that activates it's first
 * action when user clicks the cell, not the icon.
 */
Ext.define('Ext.ux.column.ActionCell',
{
	extend            : 'Ext.grid.column.Action',

	processEvent      : function(type, view, cell, recordIndex, cellIndex, e, record, row)
	{
		//?: {clicked not in the cell}
		if((type == 'click') && !e.getTarget().className ||
		   !e.getTarget().className.match(this.actionIdRe)
		  )
		{
			var target = Ext.get(e.getTarget()).down('.' + Ext.baseCSSPrefix + 'action-col-0', true);
			if(target) e.target = target;
		}

		return this.callParent(arguments);
	},

	defaultRenderer   : function(v, meta)
	{
		var res = this.callParent(arguments);
		meta.tdCls += ' retrade-action-cell';
		return res;
	}
})