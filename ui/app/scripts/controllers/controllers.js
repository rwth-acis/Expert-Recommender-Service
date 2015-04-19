var erControllers = angular.module('erControllers', []);

erControllers.controller('MainCtrl', ['$scope', '$http', '$location',
  function ($scope, $http, $location) {
    //Choose dataset controller.
    //alert("MainCtrl....");
    console.log("Main Controller...");
    $location.path("/results");
    //$location.path("/search");
    //$location.path("/results");
  }]);

erControllers.controller('ChooseDatasetCtrl', ['$scope', '$http',
  function ($scope, $http, $location) {
    //Choose dataset controller.
    console.log("Dataset Controller...");

    

  }]);

erControllers.controller('SearchCtrl', ['$scope', '$routeParams',
  function($scope, $routeParams) {
  	console.log("Search Controller...");
  }]);

erControllers.controller('ResultsCtrl', ['$scope', '$routeParams',
  function($scope, $routeParams) {
    console.log("Results Controller...");
  }]);

erControllers.controller('DropdownCtrl', function ($scope, $log) {
  $scope.items = [
    'The first choice!',
    'And another choice for you.',
    'but wait! A third!'
  ];

  $scope.status = {
    isopen: false
  };


  $scope.toggled = function(open) {
    console.log('Dropdown is now: '+ open);
    $log.log('Dropdown is now: ', open);
  };

  $scope.toggleDropdown = function($event) {
    $event.preventDefault();
    $event.stopPropagation();
    $scope.status.isopen = !$scope.status.isopen;
  };
});