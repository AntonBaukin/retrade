var ZeT  = JsX.once('zet/app.js')
var ZeTS = JsX.once('zet/strings.js')

function genGoodTypes(ctx, gen)
{
	ZeT.each(getGoodTypes(), function(g)
	{
		//!: call back to the generator
		gen.takeGoodType(ctx, assignGoodAttr(g))
	})
}

/**
 * Takes JS-object of Good Unit attribute type,
 * creates (if required), fills (except the Domain),
 * and returns resulting AttrType instance.
 */
function assignGoodAttr(g, /* optional AttrType */ type)
{
	var GoodAttr = Java.type('com.tverts.api.retrade.goods.GoodAttr')
	var AttrType = Java.type('com.tverts.endure.core.AttrType')
	var Goods    = Java.type('com.tverts.retrade.domain.goods.Goods')


	//<: initialize good attribute

	var attr = new GoodAttr()

	//=: name
	attr.setName(ZeT.asserts(g.name))

	//=: name local
	if(!ZeTS.ises(g.nameLo))
		attr.setNameLo(g.nameLo)

	//=: is-system
	attr.setSystem(!!g.system)

	//=: is-array
	attr.setArray(!!g.array)

	//=: is-shared
	attr.setShared(!!g.shared)

	//~: ox-object
	attr.setObject(ZeT.o2s(g))

	//>: initialize good attribute


	//<: initialize attribute type

	if(!type) type = new AttrType()
	ZeT.assert(type instanceof AttrType)

	//=: type of attribute
	type.setAttrType(Goods.typeGoodAttr())

	//=: name
	type.setName(attr.getName())

	//=: local name
	type.setNameLo(attr.getNameLo())

	//=: is-system
	type.setSystem(attr.isSystem())

	//=: is-array
	type.setArray(attr.isArray())

	//=: is-shared
	type.setShared(attr.isShared())

	//=: ox-object
	type.setOx(attr)

	//>: initialize attribute type

	return type
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

			/**
			 * Tells that this attribute is shared with sub-goods.
			 */
			shared:  true,

			name:    'Group',
			nameLo:  'Группа',
			type:    'string'
		},

		{
			system:  true,
			shared:  true,
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
			shared:  true,
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
			system:  true,
			shared:  true,
			name:    'Test code',
			nameLo:  'Тестовый код',
			type:    'string'
		},

		{
			name:    'Тестовый список',
			type:    'integer',
			shared:  true,
			array:   true,

			values:  [

				{ value: '10', text: 'А-10' },
				{ value: '20', text: 'Б-20' },
				{ value: '30', text: 'И-30' }
			]
		}
	]
}


ZeT.extend({}, //<-- this resulting module object
{
	assignGoodAttr : assignGoodAttr
})