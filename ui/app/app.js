var erApp = angular.module('erApp', [
  'ngRoute',
  'erControllers',
  'ui.bootstrap',
  'ngSlider'
]);

erApp.config(['$routeProvider', '$locationProvider', 
  function($routeProvider, $locationProvider) {
    $routeProvider.
      when('/datasets', {
        templateUrl: 'app/views/datasets.html',
        controller: 'ChooseDatasetCtrl'
      }).
      when('/search', {
        templateUrl: 'app/views/search_tabs.html',
        controller: 'SearchCtrl'
      }).when('/results', {
        templateUrl: 'app/views/results_tabs.html',
        controller: 'ResultsCtrl'
      });

    $locationProvider.html5Mode(true);
  }
]);