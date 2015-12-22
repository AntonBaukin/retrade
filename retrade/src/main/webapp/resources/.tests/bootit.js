angular.module('bootit', [])

//~: focus-on
angular.module('bootit').directive('focus-on', function()
{
	return function(scope, elem, attr)
	{
		ZeT.log('Set focus?')
		scope.$on(ZeT.asserts(attr['focus-on']), function()
		{
			ZeT.log('focusing')
			elem[0].focus()
		})
	}
})

//~: boot it, root controller
angular.module('bootit').controller('rootCtrl', function($scope)
{
	$scope.setUserName = function(update)
	{
		if(update === true)
		{
			$scope.userNameSet = false
			$scope.$broadcast('force-update-user-name')
		}
		else
			$scope.userNameSet = !ZeTS.ises($scope.userName)
	}
})