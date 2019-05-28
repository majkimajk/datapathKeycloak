var currentLongerAttributes = [];
var longerAttributesFromDatabase = "";
module.config([ '$routeProvider', function($routeProvider) {
    $routeProvider
        .when('/realms/:realm/users/:user/user-longer-attributes', {
            templateUrl : resourceUrl + '/partials/user-longer-attributes.html',
            resolve : {
                realm : function(RealmLoader) {
                    return RealmLoader();
                },
                user : function(UserLoader) {
                    return UserLoader();
                }
            },
            controller : 'LongerAttributesCtrl'
        })
} ]);



module.controller('LongerAttributesCtrl', function($scope, realm, user, BruteForceUser, User,
                                             Components,
                                             UserImpersonation, RequiredActions,
                                             UserStorageOperations,
                                             $location, $http, Dialog, Notifications) {
    $scope.realm = realm;


	$scope.init =  function() {

        var longAttrUrl = authUrl + "/realms/" + realm.id + "/usr-ext/" + user.id + "/longAttributes";

		$http.get(longAttrUrl).then(function(response) {
            var responseData = response.data;
			$scope.lAttributesFromDB = convertAttributeValuesToMap(responseData)
			$scope.lAttr = angular.copy($scope.lAttributesFromDB);
			$scope.$watch('lAttr', function() {
				if (!angular.equals($scope.lAttr, $scope.lAttributesFromDB)) {
					$scope.changed = true;
				}
			}, true);
        });

    };
	$scope.user = angular.copy(user);


	$scope.init();

    $scope.save = function() {
		var longAttrUrl = authUrl + "/realms/" + realm.id + "/usr-ext/" + user.id + "/longAttributes"
        $http.put(longAttrUrl, convertAttributeValuesToRequest($scope.lAttr)).then(function(response) {
            var responseData = response.data;
            if (responseData ===  "OK") {
				$scope.changed = false;
                Notifications.success("Your changes have been saved to the user.");

            }
        });

    };


    $scope.reset = function() {
        $scope.lAttr = angular.copy($scope.lAttributesFromDB);
        $scope.changed = false;
    };

    $scope.cancel = function() {
        $location.url("/realms/" + realm.realm + "/users");
    };

    $scope.addLongerAttribute = function() {
        $scope.lAttr[$scope.newLongerAttribute.key] = $scope.newLongerAttribute.value;
        delete $scope.newLongerAttribute;
    }

    $scope.removeLongerAttribute = function(key) {
        delete $scope.lAttr[key];
    }

    function convertAttributeValuesToRequest(attrs) {

		var attrToPut = [];
        for (const [key, value] of Object.entries(attrs)) {
			var newAttribute = {attributeKey:key, attributeValue:value};
			attrToPut.push(newAttribute);
        };
		return attrToPut;

    }

    function convertAttributeValuesToMap(responseData) {
		var lAttributesFromDB = new Map();
			for (var lAttribute of responseData) {
				var key = lAttribute.attributeKey;
                var value = lAttribute.attributeValue;
				lAttributesFromDB[key] = value;
                lAttributesFromDB.set(key, value);
            }
		return lAttributesFromDB;
    }


});
