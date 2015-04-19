var erApp = angular.module('erApp', [
  'ngRoute',
  'erControllers',
  'ui.bootstrap'
]);

erApp.config(['$routeProvider', '$locationProvider', 
  function($routeProvider, $locationProvider) {
    $routeProvider.
      when('/datasets', {
        templateUrl: 'app/views/datasets.html',
        controller: 'ChooseDatasetCtrl'
      }).
      when('/search', {
        templateUrl: 'app/views/search.html',
        controller: 'SearchCtrl'
      }).when('/results', {
        templateUrl: 'app/views/results.html',
        controller: 'ResultsCtrl'
      });

    $locationProvider.html5Mode(true);
  }
]);