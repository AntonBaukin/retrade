var ZeT  = JsX.once('zet/app.js')
var ZeTS = JsX.once('zet/strings.js')

function genGoodTypes(ctx, gen)
{
	var AttrType = Java.type('com.tverts.endure.core.AttrType')
	var JString  = Java.type('com.tverts.api.core.JString')

	ZeT.each(getGoodTypes(), function(g)
	{
		var type = new AttrType()

		//=: name
		type.setName(ZeT.asserts(g.name))

		//=: name local
		if(!ZeTS.ises(g.nameLo))
			type.setNameLo(g.nameLo)

		//=: is-system
		type.setSystem(!!g.system)

		//~: ox-object
		type.setOx(new JString(ZeT.o2s(g)))

		//!: call back to the generator
		gen.takeGoodType(ctx, type)
	})
}

/**
 * Warning: update Goods class when changing
 *   names of the system attributes!
 */
function getGoodTypes()
{
	return [

		{
			/**
			 * System attributes may not be edited or removed.
			 */
			system:  true,
			name:    'Barсode',
			nameLo:  'Баркод',
			type:    'string',

			/**
			 * Tells that attribute values form an array
			 * (list) that has distinct db-record for each.
			 */
			array:   true
		},

		{
			system:  true,
			name:    'Net Weight',
			nameLo:  'Вес нетто',

			/**
			 * Stored as decimal, tells to validate as volume.
			 */
			type:    'volume'
		},

		{
			system:  true,
			name:    'Gross Weight',
			nameLo:  'Вес брутто',
			type:    'volume'
		},

		{
			system:  true,
			name:    'VAT',
			nameLo:  'НДС',

			/**
			 * Note that decimal values are alsways
			 * stored in JSON as strings, not numbers!
			 */
			type:    'decimal',

			/**
			 * The enumeration of the values from drop-down list.
			 * The optional text is displayed instead of the value.
			 */
			values:  [

				{ value: '18.0', text: '18%' },
				{ value: '10.0', text: '10%' },
				{ value: '0.0',  text: '0%' }
			]
		},

		{
			name:    'Тестовый',
			type:    'string',
			array:   true,

			values:  [

				{ value: '18.0', text: '18%' },
				{ value: '10.0', text: '10%' },
				{ value: '0.0',  text: '0%' }
			]
		}
	]
}