  var erControllers = angular.module('erControllers', []);

  erControllers.controller('MainCtrl', ['$scope', '$http', '$rootScope', '$location',
    function ($scope, $http, $rootScope, $location) {
      //Choose dataset controller.
      //alert("MainCtrl....");
      console.log("Main Controller...");
      if($rootScope.dataset == undefined) {
        $location.path("/datasets");
      } else {
        console.log($rootScope.dataset);
        $location.path("/search");
      }
      //$location.path("/search");
      //$location.path("/results");
    }]);

  erControllers.controller('ChooseDatasetCtrl', ['$scope', '$http',
    function ($scope, $http, $location) {
      //Choose dataset controller.
      console.log("Dataset Controller...");

    }]);

  erControllers.controller('SearchCtrl', ['$scope', '$rootScope', '$http' , '$routeParams', '$location',
    function ($scope, $rootScope, $http, $routeParams, $location) {
      $rootScope.algorithm = "hits";
    	console.log("Search Controller...");
      $rootScope.query={text:""};
      $scope.handleClick = function() {
        //console.log($rootScope.query.text);
        $location.path("/results");
      }

    }]);

  erControllers.controller('ResultsCtrl', ['$scope', '$rootScope', '$routeParams', '$http',
    function ($scope,  $rootScope, $routeParams, $http) {
      console.log("Results Controller..."+$rootScope.query.text);
      $scope.experts = {};
      $rootScope.metrics = undefined;

      var datasetId = $rootScope.dataset.id;

      console.log(datasetId);
      var url = 'http://localhost:8080/ers/datasets/'+datasetId+'/algorithms/'+$rootScope.algorithm+'?evaluation=true&visualization=true';
      console.log(url);

      $http.post( url , $rootScope.query.text).
        success(function(data, status, headers, config) {
          console.log(data)
          $rootScope.ersIds = data;
          $http.get('http://localhost:8080/ers/datasets/'+datasetId+'/experts/'+data.expertsId).
            success(function(data, status, headers, config) {
            console.log(data);
            $scope.experts = data;
            //$scope.items=data;
          }).
          error(function(data, status, headers, config) {
            console.log(error);
            // called asynchronously if an error occurs
            // or server returns response with an error status.
          });

          
          //$scope.items=data;
        }).
        error(function(data, status, headers, config) {
          console.log("ERROR...");
          console.log(error);
          // called asynchronously if an error occurs
          // or server returns response with an error status.
        });

    }]);

  erControllers.controller('DropdownCtrl', function ($scope, $rootScope, $log, $http, $location) {
    $scope.selectedItem = {"name":"default","id":-1};  

    $scope.items = [
      'default'
    ];

    $http.get('http://localhost:8080/ers/datasets').
      success(function(data, status, headers, config) {
        console.log(data);
        $scope.items=data;
      }).
      error(function(data, status, headers, config) {
        console.log(error);
      // called asynchronously if an error occurs
      // or server returns response with an error status.
      });

    $scope.status = {
      isopen: false
    };

    $scope.handleSelection = function($index) {
      $scope.selectedItem = $scope.items[$index];
      $rootScope.dataset = $scope.selectedItem;

      $location.path("/search");
    }

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


  erControllers.controller('ResultTabsCtrl', ['$scope', '$rootScope', '$http',
    function($scope, $rootScope, $http) {
    
    $scope.handleVisTab = function($event) {
      console.log("Handle Vis tab...");
      console.log($rootScope);
      console.log($rootScope.ersIds);

        /*$http.get('http://localhost:8080/ers/datasets/'+$rootScope.dataset.id+'/visualizations/'+$rootScope.ersIds.visualizationId).
            success(function(data, status, headers, config) {
            console.log(data);
            //$scope.items=data;
          }).
          error(function(data, status, headers, config) {
            console.log(error);
            // called asynchronously if an error occurs
            // or server returns response with an error status.
        });*/

      sigma.parsers.gexf(
      'http://localhost/graphvis/visgraph.gexf',
      //'http://localhost:8080/ers/download/visgraph.gexf',
      //'http://localhost:8080/ers/visgraph.gexf',
      { // Here is the ID of the DOM element that
        // will contain the graph:
        container: 'sigma-container',
        settings: {
          "defaultNodeColor": '#ec5148',
          "defaultEdgeColor": 'red',
          
        }
      },
      function(s) {

        s.graph.nodes().forEach(function(n) {
            n.color = '#ffcc99';
        });

        console.log(s);
        // This function will be executed when the
        // graph is displayed, with "s" the related
        // sigma instance.

        var config = {};
        config.gravity = 1;
        config.strongGravityMode  = true;
        config.barnesHutOptimize = true;
        config.barnesHutTheta = 0.5;
        config.edgeWeightInfluence = 0;
        config.adjustSizes = true;
        config.linLogMode = true;
        config.outboundAttractionDistribution = true;

        //s.startForceAtlas2(config);
        s.bind('clickNode', function(e) {
          var nodeId = e.data.node.id;
          

              $http.get('http://localhost:8080/ers/datasets/'+$rootScope.dataset.id+'/users/'+nodeId).
                success(function(data, status, headers, config) {
                $scope.expert = data;
                //$scope.items=data;
              }).
              error(function(data, status, headers, config) {
                console.log(error);
                // called asynchronously if an error occurs
                // or server returns response with an error status.
              });
        });

        s.refresh();
      }
    );

    };

    $scope.handleEvaluationTab = function($event) {
      console.log("Handle Evaluation tab...");
      $http.get('http://localhost:8080/ers/datasets/'+$rootScope.dataset.id+'/evaluations/'+$rootScope.ersIds.evaluationId).
        success(function(data, status, headers, config) {
          var obj = data.metrics;
          $rootScope.metrics = obj;
          console.log($rootScope.metrics);
        
      }).
      error(function(data, status, headers, config) {
        console.log(error);
      });

    };

    }
  ]);

  erControllers.controller('settingsCtrl', ['$scope','$rootScope',
    function($scope, $rootScope) {

      $scope.algorithm = { value: '' };

      $scope.choices = [{
          id: 1,
          name: "modeling"
        }, 
        {
          id: 2,
          name: "pagerank"
        },
        {
          id: 3,
          name: "hits",
          default: true
        },
          {
          id: 4,
          name: "communityAwarePagerank"
        },
          {
          id: 5,
          name: "communityAwareHITS"
        },

      ];
      
      $scope.value = 30;
      $scope.prValue = 0.3;
      $scope.hitsValue = 0.3;
      $scope.CaPrValue = 0.3;
      $scope.CAHitsValue = 0.3;

      console.log("settingsCtrl Controller...");
      $scope.options = {        
          from: 0,
          to: 100,
          step: 1,
          skin: 'round',
          dimension: "%",
      };

      $scope.prOptions = {        
          from: 0,
          to: 100,
          step: 1,
          skin: 'round',
          dimension: "",
          callback: function(value, elt) {
            console.log(value);
          } 
      };

      $scope.handleSelection = function(algorithm) {
        //console.log($scope.algorithm.value);
        $rootScope.algorithm = $scope.algorithm.value;
      }

    }]);