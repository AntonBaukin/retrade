angular.module('bootit', [])

//~: focus-on
angular.module('bootit').directive('focusOn', function()
{
	return function(scope, node, attr)
	{
		ZeTS.eachws(attr['focusOn'], function(s)
		{
			scope.$on((ZeTS.first(s) != '?')?(s):(s.substring(1)), function(event)
			{
				if((ZeTS.first(s) == '?') && !ZeTS.ises(node.val()))
					return

				event.preventDefault()
				ZeT.timeout(100, function(){ node.focus() })
			})
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
	$scope.$on('user-set', function(e, user)
	{
		$scope.user = user
	})
})

//~: boot it, user form controller
angular.module('bootit').controller('userFormCtrl', function($scope)
{
	angular.extend($scope,
	{
		setUser  : function()
		{
			ZeT.assert(ZeT.iso($scope.user))

			$scope.user.displayName = ZeTS.cat(
			  ZeTS.catif($scope.user.firstName, ZeTS.first($scope.user.firstName), '. '),
			  $scope.user.lastName
			)

			$scope.$emit('user-set', $scope.user)
		}
	})
})