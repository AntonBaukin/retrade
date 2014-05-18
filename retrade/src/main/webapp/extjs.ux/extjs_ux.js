/*===============================================================+
 |                                                               |
 |   Ext JS User Extensions                                      |
 |                                   / anton.baukin@gmail.com /  |
 +===============================================================*/


Ext.define('Ext.ux.layout.HTMLCenter',
{
	extend            : 'Ext.layout.container.Container',
	alias             : 'layout.ux.html-center',

	calculate         : function(ctx)
	{
		//~: get the component to center
		var items = this.getVisibleItems();
		if(!items || (items.length != 1)) return;
		var item  = items[0];
		var ibox  = item.getSize(true);
		if(!ibox.width || !ibox.height) return;

		//~: find the outer DOM node (to center in)
		var xbox, x = this.getTarget().dom;

		while(!xbox && x)
		{
			var w = x.offsetWidth, h = x.offsetHeight;
			if(!w || !h) continue;

			if((w > ibox.width) && (h > ibox.height))
			{ xbox = { width: w, height: h }; break; }

			x = x.parentNode;
		}

		if(!xbox) return;

		//~: center the item
		var ix = 0, iy = 0;
		if(ibox.width  < xbox.width ) ix = (xbox.width  - ibox.width ) / 2;
		if(ibox.height < xbox.height) iy = (xbox.height - ibox.height) / 2;

		item.setPosition(ix, iy)
	}
})


Ext.define('Ext.ux.layout.component.field.ComboBox100p',
{
	extend            : 'Ext.layout.component.field.ComboBox',
	alias             : 'layout.ux.dropbox100p',
	type              : 'combobox',

	beginLayoutFixed  : function(ownerContext, width, suffix)
	{
		this.callParent(arguments)

		//~: set width 100%
		ownerContext.target.triggerWrap.setStyle({width: '100%'})
	}
})


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
