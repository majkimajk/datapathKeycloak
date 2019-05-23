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
    $scope.create = !user.id;
    $scope.editUsername = $scope.create || $scope.realm.editUsernameAllowed;


    if ($scope.create) {
        $scope.user = { enabled: true, attributes: {} }
    } else {
        if (!user.attributes) {
            user.attributes = {}
        }
        convertAttributeValuesToString(user);

        addLongerAttributes();

        $scope.user = angular.copy(user);
        $scope.impersonate = function() {
            UserImpersonation.save({realm : realm.realm, user: $scope.user.id}, function (data) {
                if (data.sameRealm) {
                    window.location = data.redirect;
                } else {
                    window.open(data.redirect, "_blank");
                }
            });
        };
        if(user.federationLink) {
            console.log("federationLink is not null. It is " + user.federationLink);

            if ($scope.access.viewRealm) {
                Components.get({realm: realm.realm, componentId: user.federationLink}, function (link) {
                    $scope.federationLinkName = link.name;
                    $scope.federationLink = "#/realms/" + realm.realm + "/user-storage/providers/" + link.providerId + "/" + link.id;
                });
            } else {
                // KEYCLOAK-4328
                UserStorageOperations.simpleName.get({realm: realm.realm, componentId: user.federationLink}, function (link) {
                    $scope.federationLinkName = link.name;
                    $scope.federationLink = $location.absUrl();
                })
            }

        } else {
            console.log("federationLink is null");
        }
        if(user.origin) {
            if ($scope.access.viewRealm) {
                Components.get({realm: realm.realm, componentId: user.origin}, function (link) {
                    $scope.originName = link.name;
                    $scope.originLink = "#/realms/" + realm.realm + "/user-storage/providers/" + link.providerId + "/" + link.id;
                })
            }
            else {
                // KEYCLOAK-4328
                UserStorageOperations.simpleName.get({realm: realm.realm, componentId: user.origin}, function (link) {
                    $scope.originName = link.name;
                    $scope.originLink = $location.absUrl();
                })
             }
        } else {
            console.log("origin is null");
        }
        console.log('realm brute force? ' + realm.bruteForceProtected)
        $scope.temporarilyDisabled = false;
        var isDisabled = function () {
            BruteForceUser.get({realm: realm.realm, userId: user.id}, function(data) {
                console.log('here in isDisabled ' + data.disabled);
                $scope.temporarilyDisabled = data.disabled;
            });
        };

        console.log("check if disabled");
        isDisabled();

        $scope.unlockUser = function() {
            BruteForceUser.delete({realm: realm.realm, userId: user.id}, function(data) {
                isDisabled();
            });
        }
    }

    $scope.changed = false; // $scope.create;
    if (user.requiredActions) {
        for (var i = 0; i < user.requiredActions.length; i++) {
            console.log("user require action: " + user.requiredActions[i]);
        }
    }
    // ID - Name map for required actions. IDs are enum names.
    RequiredActions.query({realm: realm.realm}, function(data) {
        $scope.userReqActionList = [];
        for (var i = 0; i < data.length; i++) {
            console.log("listed required action: " + data[i].name);
            if (data[i].enabled) {
                var item = data[i];
                $scope.userReqActionList.push(item);
            }
        }
    console.log("---------------------");
    console.log("ng-model: user.requiredActions=" + JSON.stringify($scope.user.requiredActions));
    console.log("---------------------");
    console.log("ng-repeat: userReqActionList=" + JSON.stringify($scope.userReqActionList));
    console.log("---------------------");
    });
    $scope.$watch('user', function() {
        if (!angular.equals($scope.user, user)) {
            $scope.changed = true;
        }
    }, true);

    $scope.save = function() {
        convertAttributeValuesToLists();

        if ($scope.create) {
            User.save({
                realm: realm.realm
            }, $scope.user, function (data, headers) {
                $scope.changed = false;
                convertAttributeValuesToString($scope.user);
                user = angular.copy($scope.user);
                var l = headers().location;

                console.debug("Location == " + l);

                var id = l.substring(l.lastIndexOf("/") + 1);


                $location.url("/realms/" + realm.realm + "/users/" + id);
                Notifications.success("The user has been created.");
            });
        } else {
            User.update({
                realm: realm.realm,
                userId: $scope.user.id
            }, $scope.user, function () {
                $scope.changed = false;
                convertAttributeValuesToString($scope.user);
                user = angular.copy($scope.user);
                Notifications.success("Your changes have been saved to the user.");
            });
        }
    };

    function convertAttributeValuesToLists() {
        var attrs = $scope.user.attributes;
        for (var attribute in attrs) {
            if (typeof attrs[attribute] === "string") {
                var attrVals = attrs[attribute].split("##");
                attrs[attribute] = attrVals;
            }
        }
    }

    function convertAttributeValuesToString(user) {
        var attrs = user.attributes;
        for (var attribute in attrs) {
            if (typeof attrs[attribute] === "object") {
                var attrVals = attrs[attribute].join("##");
                attrs[attribute] = attrVals;
            }
        }
    }

    $scope.reset = function() {
        $scope.user = angular.copy(user);
        $scope.changed = false;
    };

    $scope.cancel = function() {
        $location.url("/realms/" + realm.realm + "/users");
    };

    $scope.addAttribute = function() {
        $scope.user.attributes[$scope.newAttribute.key] = $scope.newAttribute.value;
        delete $scope.newAttribute;
    }

    $scope.removeAttribute = function(key) {
        delete $scope.user.attributes[key];
    }

    $scope.saveCurrentLongerAttributes = function() {
        var longAttrUrl = authUrl + "/realms/" + "test" + "/um-authz/" + "95d20c88-68a7-4689-88a9-3f447be22011" + "/update/longAttributes"
        $http.post(longAttrUrl, currentLongerAttributes).then(function(response) {
            var responseData = response.data;
            if (responseData ===  "OK") {
                disableSaveAndResetButtons();
                Notifications.success("Your changes have been saved to the user.");
                longerAttributesFromDatabase = JSON.parse(JSON.stringify(currentLongerAttributes));
            }
        });
    };


    function addLongerAttributes() {

        var longAttrUrl = authUrl + "/realms/" + realm.id + "/um-authz/" + user.id + "/longAttributes";
        $http.get(longAttrUrl).then(function(response) {
            var responseData = response.data;
            longerAttributesFromDatabase = responseData;
            var tbody = document.getElementById('longAttributesTable');
            for (var lAttribute of responseData) {
                fillLongerAttributesTable(tbody, lAttribute.attributeKey, lAttribute.attributeValue)
            }
        });
    }


});

function fillLongerAttributesTable(tbody, key, value) {
     for (var longerAttribute of currentLongerAttributes) {
            if (longerAttribute.attributeKey === key) {
                document.getElementById('newLongAttributeKey').value = "";
                document.getElementById('newLongAttributeValue').value = "";
                return;
            }
    }

    fillCurrentLongerAttributes(key,value);

    var newRow = tbody.insertRow(tbody.children.length);
    newRow.id = "row-" + key;

    var keyCell = newRow.insertCell(0);
    var valueCell = newRow.insertCell(1);
    var funcCell = newRow.insertCell(2);

    var keyValue = document.createTextNode(key);
    var valueInput = document.createElement("input");
    var funcValue = document.createTextNode("Delete");


    valueInput.setAttribute('type', 'text');
    valueInput.value = value;
    valueInput.id = "longAttribute-" + key;
    valueInput.classList.add("form-control");
    valueInput.classList.add("ng-pristine");
    valueInput.classList.add("ng-untouched");
    valueInput.classList.add("ng-valid");
    valueInput.classList.add("ng-not-empty");
    valueInput.onkeyup = function() {fillCurrentLongerAttributes(key, valueInput.value);}

    funcCell.classList.add("kc-action-cell");
    funcCell.onclick = function() {removeLongAttribute(key);};

    keyCell.appendChild(keyValue);
    valueCell.appendChild(valueInput);
    funcCell.appendChild(funcValue);


}

function fillCurrentLongerAttributes(key, value) {
    for (var longerAttribute of currentLongerAttributes) {
        if (longerAttribute.attributeKey === key) {
            longerAttribute.attributeValue = value;
            enableSaveAndResetButtons();
            return;
        }
    }
    var longerAttributePair = new Object();
    longerAttributePair.attributeKey = key;
    longerAttributePair.attributeValue = value;
    currentLongerAttributes.push(longerAttributePair);
}

function addLongAttribute() {
    var newAttrKeyField = document.getElementById('newLongAttributeKey');
    var newAttrValueField = document.getElementById('newLongAttributeValue');
    var tbody = document.getElementById('longAttributesTable');
    fillLongerAttributesTable(tbody, newAttrKeyField.value, newAttrValueField.value);
    newAttrKeyField.value = "";
    newAttrValueField.value = "";
    enableSaveAndResetButtons();
}

function removeLongAttribute(key) {
    for (var i = 0; i < currentLongerAttributes.length; i++) {
        if (currentLongerAttributes[i].attributeKey === key) {
            currentLongerAttributes.splice(i, 1);
            var row = document.getElementById("row-" + key);
            row.parentNode.removeChild(row);
            enableSaveAndResetButtons()
            return;
        }
    }
}



function resetCurrentLongerAttributes() {
    var tbody = document.getElementById('longAttributesTable');
    while (tbody.hasChildNodes()) {
        tbody.removeChild(tbody.firstChild);
    }
    currentLongerAttributes = [];

    for (var lAttribute of longerAttributesFromDatabase) {
        fillLongerAttributesTable(tbody, lAttribute.attributeKey, lAttribute.attributeValue)
    }

    disableSaveAndResetButtons();
}


function enableSaveAndResetButtons() {
    document.getElementById("saveLongerAttributes").disabled = false;
    document.getElementById("resetLongerAttributes").disabled = false;
}


function disableSaveAndResetButtons() {
    document.getElementById("saveLongerAttributes").disabled = true;
    document.getElementById("resetLongerAttributes").disabled = true;
}