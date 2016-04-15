var ZeT = JsX.once('zet/app.js')

function genTestMeasures(ctx, gen)
{
	var Measure = Java.type('com.tverts.api.retrade.goods.Measure')

	ZeT.each(getTestMeasures(), function(m)
	{
		//~: convert JS object to JSON
		var json = ZeT.o2s(m)

		//~: read JSON to Measure instance
		var measure = ZeT.s2jo(Measure, json)

		//~: report it to the generator
		gen.takeMeasure(ctx, measure)
	})
}

function getTestMeasures()
{
	return [

		{
			code: 'час',
			name: 'час',
			fractional: true,
			'class-code': '356'
		},

		{
			code: 'кг',
			name: 'килограммы',
			fractional: true,
			'class-code': '166'
		},

		{
			code: '100 гр',
			name: '100 грамм',
			fractional: true,
			'class-code': '166',
			'class-unit': '0.1'
		},

		{
			code: 'шт',
			name: 'штука',
			fractional: false,
			'class-code': '796'
		},

		{
			code: 'бух',
			name: 'буханка',
			fractional: false
		},

		{
			code: 'уп',
			name: 'упаковка',
			fractional: false,
			'class-code': '778'
		},

		{
			code: 'л',
			name: 'литры',
			fractional: true,
			'class-code': '112'
		},

		{
			code: '100 мл',
			name: '100 миллилитров',
			fractional: true,
			'class-code': '112',
			'class-unit': '0.1'
		},

		{
			code: 'ст 0.5',
			name: 'стакан 0.5л',
			fractional: false,
			'class-code': '112',
			'class-unit': '0.5'
		},

		{
			code: 'ст 0.33',
			name: 'стакан 0.33л',
			fractional: false,
			'class-code': '112',
			'class-unit': '0.33'
		}
	]
}