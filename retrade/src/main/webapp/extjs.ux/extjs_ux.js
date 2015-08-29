/*===============================================================+
 |                                                               |
 |   Ext JS User Extensions                                      |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/


/**
 * Default timeout to 30 minutes.
 */
Ext.Ajax.setTimeout(1000 * 60 * 30)

/**
 * Make window shadow to be on all the sides.
 */
Ext.window.Window.override(
{

	shadow            : 'frame',

	privates          :
	{
		/**
		 * In present implementation we don't align
		 * floating windows to any scroll!
		 */
		onAlignToScroll: function ()
		{
			this.clearAlignEl()
		}
	}
})

/**
 * Override scripts execution function
 * to catch up scripts having errors.
 */

window._exec_script = window._exec_script || window.execScript || window.eval
window.execScript   = function()
{
	try
	{
		window._exec_script.apply(this, arguments)
	}
	catch(e)
	{
		if(extjsf)
			extjsf.catchError(e, this, ZeT.a(arguments))
		else if(console & typeof console.log === 'function')
			console.log('Unhandled script evaluation: ', e, this, arguments)

		throw e
	}
}

/**
 * ExtJS FIX: Component.setHtml()
 */
Ext.Component.override({

	setHtml           : function()
	{
		this.update.apply(this, arguments)
	},

	fireEventArgs     : function()
	{
		try
		{
			return this.callParent(arguments)
		}
		catch(e)
		{
			extjsf.catchError(e, this, ZeT.a(arguments))
		}
	}
})

Ext.mixin.Observable.override(
{
	fireEventArgs     : function()
	{
		try
		{
			return this.callParent(arguments)
		}
		catch(e)
		{
			extjsf.catchError(e, this, ZeT.a(arguments))
		}
	}
})

Ext.form.field.ComboBox.override(
{
	/**
	 * ComboBox default list options.
	 */
	defaultListConfig : {

		loadingHeight  : 64,
		minWidth       : 64,
		maxHeight      : 342,
		shadow         : 'sides'
	}
})

Ext.resizer.Splitter.override(
{
	collapseOnDblClick : false
})


// +----: Time Field + Picker :----------------------------------+

Ext.define('Ext.ux.form.field.Time',
{
	extend            : 'Ext.form.field.Time',
	alias             : 'widget.ux.timefield',

	initComponent     : function()
	{
		this.callParent(arguments)

		//~: create store using the picker
		var store = this.getPicker().
		  createStore(this.format, this.increment)

		this.bindStore(store, true, true)
		this.getPicker().setSelection(
		  this.findRecordByValue(this.getValue()))
	},

	createPicker      : function()
	{
		if(!this.listConfig)
			this.listConfig = {}
		if(!this.listConfig.xtype)
			this.listConfig.xtype = 'ux.timepicker'

		return this.callParent(arguments)
	},

	isEqual           : function(v1, v2)
	{
		v1 = Ext.Array.from(v1)
		v2 = Ext.Array.from(v2)

		if(v1.length != v2.length)
			return false

		var FMT = this.getPicker().format
		function tstr(t)
		{
			if(t instanceof Date)
				t = Ext.Date.dateFormat(t, FMT)

			ZeT.asserts(t)
			return t
		}

		for(var i = 0;(i < v1.length); i++)
			if(tstr(v2[i]) != tstr(v1[i]))
				return false

		return true
	},

	findRecordByValue : function(v)
	{
		if(v instanceof Date)
			v = Ext.Date.dateFormat(v, this.getPicker().format)
		ZeT.asserts(v)

		var m = null; this.store.each(function(x)
		{
			if(x.get('disp') == v) { m = x; return false }
		})

		return m
	}
})


Ext.define('Ext.ux.picker.Time',
{
	extend            : 'Ext.picker.Time',
	alias             : 'widget.ux.timepicker',

	createStore       : function()
	{
		var me   = this, times = []
		var DU   = Ext.Date
		var TP   = Ext.picker.Time.prototype
		var ID   = TP.initDate
		var min  = DU.clearTime(new Date(ID[0], ID[1], ID[2]))
		var max  = DU.add(min, 'mi', (24 * 60) - 1)
		var time = (!me.pickerField)?(null):(me.pickerField.getValue())

		function fmt(t)
		{
			return DU.dateFormat(t, me.format)
		}

		function add(t)
		{
			times.push({date: t, disp: fmt(t)})
		}

		while(min <= max)
		{
			var skip = false

			//?: {has present time value to insert}
			if(time)
			{
				skip = (fmt(time) == fmt(min))

				//?: {add this time}
				if(skip || (min > time))
				{
					add(time)
					time = null
				}
			}

			if(!skip) add(min)
			min = DU.add(min, 'mi', me.increment)
		}

		return new Ext.data.Store({ data: times, model: TP.modelType })
	}
})


// +----: Detached Button :--------------------------------------+

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


// +----: Tri-State Checkbox :-----------------------------------+

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


// +----: Disabled Selection Model :-----------------------------+

Ext.define('Ext.ux.selection.No',
{
	extend            : 'Ext.selection.RowModel',

	doSelect          : function()
	{},

	doMultiSelect     : function()
	{}
})


// +----: Grid Row Action Cell :---------------------------------+

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