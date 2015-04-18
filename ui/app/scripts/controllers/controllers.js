var erControllers = angular.module('erControllers', []);

erControllers.controller('MainCtrl', ['$scope', '$http', '$location',
  function ($scope, $http, $location) {
    //Choose dataset controller.
    //alert("MainCtrl....");
    console.log("Main Controller...");
    $location.path("/datasets");
    //$location.path("/search");
    //$location.path("/results");
  }]);

erControllers.controller('ChooseDatasetCtrl', ['$scope', '$http',
  function ($scope, $http, $location) {
    //Choose dataset controller.
    console.log("Dataset Controller...");
    alert("Choose dataset controller....");
  }]);

erControllers.controller('SearchCtrl', ['$scope', '$routeParams',
  function($scope, $routeParams) {
  	console.log("Search Controller...");
  }]);

erControllers.controller('ResultsCtrl', ['$scope', '$routeParams',
  function($scope, $routeParams) {
    console.log("Results Controller...");
  }]);