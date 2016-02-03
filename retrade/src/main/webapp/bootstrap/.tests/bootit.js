angular.module('bootit', [])

//~: focus-on
angular.module('bootit').directive('focusOn', function()
{
	return function(scope, node, attr)
	{
		scope.$on(ZeT.asserts(attr['focusOn']), ZeT.timeouted(100, function()
		{
			node.focus()
		}))
	}
})

//~: boot it, root controller
angular.module('bootit').controller('rootCtrl', function($scope)
{
	var shownContent

	$scope.isShown = function(id)
	{
		ZeT.asserts(id)
		return id == shownContent
	}

	var showContent = $scope.showContent = function(id)
	{
		shownContent = ZeT.asserts(id)
		$scope.$broadcast('show-' + id)
	}

	var hideContent = $scope.hideContent = function(id)
	{
		ZeT.asserts(id)
		if(id == shownContent)
			shownContent = null
	}

	$scope.setUserName = function()
	{
		$scope.userNameSet = !ZeTS.ises($scope.userName)
		if($scope.userNameSet) hideContent('stranger-form')
	}
})