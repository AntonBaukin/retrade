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