angular.module('bootit', [])

//~: focus-on
angular.module('bootit').directive('focusOn', function()
{
	return function(scope, node, attr)
	{
		ZeTS.eachws(attr['focusOn'], function(s)
		{
			scope.$on(s, ZeT.timeouted(100, function()
			{
				node.focus()
			}))
		})
	}
})

//~: visible-on
angular.module('bootit').directive('visibleOn', function()
{
	return function(scope, node, attr)
	{
		ZeTS.eachws(attr['visibleOn'], function(s)
		{
			scope.$on((ZeTS.first(s) != '!')?(s):(s.substring(1)), function()
			{
				node.toggle(ZeTS.first(s) != '!')
			})
		})
	}
})

//~: click-broadcast
angular.module('bootit').directive('clickBroadcast', function()
{
	return function(scope, node, attr)
	{
		var events = ZeTS.trim(attr['clickBroadcast'])

		//?: {is simple list of events}
		if(ZeTS.first(events) != '{')
			events = events.split(/\s+/)
		//TODO support {} objects with events data
		//else

		node.click(function()
		{
			if(ZeT.isa(events))
			{
				for(var i = 0;(i < events.length);i++)
					if(!ZeTS.ises(events[i]))
						scope.$broadcast(events[i])
			}
		})
	}
})

//~: boot it, root controller
angular.module('bootit').controller('rootCtrl', function($scope)
{
})