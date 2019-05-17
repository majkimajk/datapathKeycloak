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
            controller : 'UserDetailCtrl'
        })
} ]);