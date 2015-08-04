      var erControllers = angular.module('erControllers', []);
      
      erControllers.controller('LoginCtrl', ['$scope', '$http', '$rootScope', '$location',
        function ($scope, $http, $rootScope, $location) {
          $scope.username = "anon";
           $scope.handleLogin = function($event,username) {
              //Check for validity and store the value is the useragent.
              var basicAuthEncodedString = window.btoa("anonymous:anonymous");

             // $http.defaults.headers.post.Authorization = "Basic "+ basicAuthEncodedString;

              $http.post('http://localhost:8080/ers/validate?username='+$scope.username).
                    success(function(data, status, headers, config) {
                    //$scope.expert = data;
                      console.log(data);
                      $scope.item=data;
                  }).
                  error(function(data, status, headers, config) {
                    $scope.$emit('showUnavailableText', true);
                    console.log(data);
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                  });

              $location.path("/search");
           };
      }]);

      erControllers.controller('MainCtrl', ['$scope', '$http', '$rootScope', '$location',
        function ($scope, $http, $rootScope, $location) {

          sigma.classes.graph.addMethod('neighbors', function(nodeId) {
              var k,
              neighbors = {},
              index = this.allNeighborsIndex[nodeId] || {};

              for (k in index)
                neighbors[k] = this.nodesIndex[k];

              return neighbors;
            });


          //Choose dataset controller.
          //alert("MainCtrl....");
          console.log("Main Controller...");
          if($rootScope.dataset == undefined) {
            $location.path("/");
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

      erControllers.controller('SearchCtrl', ['$scope', '$rootScope', '$sce', '$http' , '$routeParams', '$location',
        function ($scope, $rootScope, $sce, $http, $routeParams, $location) {
          //$rootScope.algorithm = "hits";
        	console.log("Search Controller...");
          $rootScope.query={text:""};
          $scope.disableVis = false;
          $scope.showUnavailableText = false;

          $scope.$on('disableVisualization', function(event, args) {
            $scope.disableVis = args;
          });

          $scope.$on('showUnavailableText', function(event, args) {
            $scope.showUnavailableText = args;
          });

          $scope.handleVisTab = function($event) {
            console.log("Handle Vis tab...");
            console.log($rootScope);
            console.log($rootScope.ersIds);

  /*
            $http.get('http://localhost:8080/ers/datasets/'+$rootScope.dataset.id+'/visualizations/'+$rootScope.ersIds.visualizationId).
                success(function(data, status, headers, config) {
                console.log(data);
                //$scope.items=data;
              }).
              error(function(data, status, headers, config) {
                console.log(error);
                // called asynchronously if an error occurs
                // or server returns response with an error status.
            }); */

            
            if($rootScope.sig != undefined) {
              $rootScope.sig.graph.clear();
               $rootScope.sig.refresh();
            }

            sigma.parsers.gexf(
            'http://localhost:8080/ers/download/visgraph.gexf',
            { 
              container: 'sigma-container',
            
            },
            function(s) { 
              $rootScope.sig = s;
              s.settings({
                edgeColor: 'default',
                defaultEdgeColor: 'grey',
                rescaleIgnoreSize: false,
                scalingMode: 'outside',
                autoRescale:true
              });

              s.graph.edges().forEach(function(e) {
                  //e.color = '#D3D3D3';
                  e.color = '#FFFFFF';
              });


              s.graph.nodes().forEach(function(n) {
                s.cameras[0].goTo({x:n['read_cam0:x'],y:n['read_cam0:y'],ratio:0.6});
              });



            /*s.graph.nodes().forEach(function(n) {
                n.color = '#ffcc99';
            });*/

           

            console.log(s);
            // This function will be executed when the
            // graph is displayed, with "s" the related
            // sigma instance.

            var config = {};
            config.gravity = 1;
            config.adjustSizes = true;
            config.linLogMode = true;

            //s.startForceAtlas2(config);
            s.bind('clickNode', function(e) {
              var nodeId = e.data.node.id;
              
              toKeep = s.graph.neighbors(nodeId);
                toKeep[nodeId] = e.data.node;

            s.graph.nodes().forEach(function(n) {
              if (toKeep[n.id])
                n.color = n.originalColor;
              else
                n.color = '#077015';
            });

             s.graph.edges().forEach(function(e) {
              if (toKeep[e.source] && toKeep[e.target])
                e.color = "#FF0000";
              else
                e.color = '#FFFFFF';
            });

              s.refresh();
              



                  $http.get('http://localhost:8080/ers/datasets/'+$rootScope.dataset.id+'/users/'+nodeId).
                    success(function(data, status, headers, config) {
                    //$scope.expert = data;
                      console.log(data);
                      $scope.item=data;
                  }).
                  error(function(data, status, headers, config) {
                    $scope.$emit('showUnavailableText', true);
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
            console.log(status+" : "+data);
          });

        };

        $scope.handleExpertInterestClick = function(expert) {
          var datasetId = $rootScope.dataset.id;
          console.log($rootScope.ersIds);
          expert.hide = true;
          //datasets/{datasetId}/experts/{expertsCollectionId}/expert/{expertId}/tags")
          var url = 'http://localhost:8080/ers/datasets/'+datasetId+'/experts/'+$rootScope.ersIds.expertsId+'/expert/'+expert.userId+'/tags';
          console.log(url);

          $http.get(url).
            success(function(data, status, headers, config) {
                console.log(data)
                expert.interests = data;
                //$scope.items=data;
              }).
              error(function(data, status, headers, config) {
                console.log(error);
                // called asynchronously if an error occurs
                // or server returns response with an error status.
              });
        };

        $scope.handleShowPostClick = function(expert) {
          var datasetId = $rootScope.dataset.id;
          console.log($rootScope.ersIds);
          //datasets/{datasetId}/experts/{expertsCollectionId}/expert/{expertId}/tags")
          var url = 'http://localhost:8080/ers/datasets/'+datasetId+'/experts/'+$rootScope.ersIds.expertsId+'/expert/'+expert.userId+'/posts';
          console.log(url);

          $http.get(url).
            success(function(data, status, headers, config) {
              //console.log(data)
              
              if(expert.showRelatedPosts) {
                expert.relatedPostLabel = "Show Related Posts";
                expert.showRelatedPosts = false;
              }
              else  {
                expert.relatedPostLabel = "Hide Related Posts";
                expert.showRelatedPosts = true;
              }

              $scope.relatedPosts = $sce.trustAsHtml(data);
                //$scope.items=data;
              }).
              error(function(data, status, headers, config) {
                console.log(error);
                // called asynchronously if an error occurs
                // or server returns response with an error status.
              });
        };

        $scope.handleRelevantPostClick = function(expert) {

            var index = $scope.experts.indexOf(expert);
            expert.relevantExpert = true;

/*
            var datasetId = $rootScope.dataset.id;
            String url = "http://localhost:8080/ers/datasets/"+datasetId+"/position?queryId="+$rootScope.ersIds.expertsId+"&position="+position;
            $http.post( url , "").
            success(function(data, status, headers, config) {
              console.log(data)
              }).
              error(function(data, status, headers, config) {
                $scope.showUnavailableText = true;
                console.log(error);
              });*/
        };

          $scope.handleClick = function() {
            //console.log($rootScope.query.text);
            //$location.path("/results");

            console.log("Results Controller..."+$rootScope.query.text);
          $scope.experts = {};
          $rootScope.metrics = undefined;
          $scope.relatedPostLabel = "Show Related Posts";

          var datasetId = $rootScope.dataset.id;

          if($rootScope.algorithm == undefined) {
            $rootScope.algorithm = "hits";
          }

          console.log(datasetId);

          var queryParam = undefined;
          if($rootScope.algorithm === "pagerank" || $rootScope.algorithm == "hits" || $rootScope.algorithm == "datamodeling") {
            //console.log($rootScope.parameter.value);
            if(isNaN($rootScope.parameter) == false) {
              console.log("It is a number...");
              queryParam = "alpha="+$rootScope.parameter;
            } else {
              queryParam = "";
            }
          } else if($rootScope.algorithm === "communityAwarePagerank" || $rootScope.algorithm == "communityAwareHITS") {
            if(isNaN($rootScope.parameter) == false) {
              queryParam = "intraWeight="+$rootScope.parameter;
            } else {
              queryParam = "";
            }
          }
        
          var url = 'http://localhost:8080/ers/datasets/'+datasetId+'/algorithms/'+$rootScope.algorithm+'?evaluation=true&visualization=true&'+queryParam;
          console.log(url);

          $http.post( url , $rootScope.query.text).
            success(function(data, status, headers, config) {
              console.log(data)
              $rootScope.ersIds = data;
              $http.get('http://localhost:8080/ers/datasets/'+datasetId+'/experts/'+data.expertsId).
                success(function(data, status, headers, config) {

                console.log(data);
                if(data.length === 0) {
                  $scope.$emit('showUnavailableText', true);
                } else {
                  $scope.$emit('showUnavailableText', false);
                  $scope.experts = data;  
                }
                
                
                //$scope.items=data;
              }).
              error(function(data, status, headers, config) {
                $scope.showUnavailableText = true;
                console.log(error);
                // called asynchronously if an error occurs
                // or server returns response with an error status.
              });

              
              //$scope.items=data;
            }).
            error(function(data, status, headers, config) {
              console.log(data);
              // called asynchronously if an error occurs
              // or server returns response with an error status.
            });


          }

        }]);

      erControllers.controller('ResultsCtrl', ['$scope', '$rootScope', '$routeParams', '$http',
        function ($scope,  $rootScope, $routeParams, $http) {
          console.log("Results Controller..."+$rootScope.query.text);
          $scope.experts = {};
          $rootScope.metrics = undefined;

          var datasetId = $rootScope.dataset.id;

          console.log(datasetId);
          //$rootScope.algorithm = "pagerank";
          var url = 'http://localhost:8080/ers/datasets/'+datasetId+'/algorithms/'+$rootScope.algorithm+'?evaluation=true&visualization=true';
          console.log(url);

          $http.post( url , $rootScope.query.text).
            success(function(data, status, headers, config) {
              console.log(data)
              $rootScope.ersIds = data;
              $http.get('http://localhost:8080/ers/datasets/'+datasetId+'/experts/'+data.expertsId).
                success(function(data, status, headers, config) {
                console.log(data);
                if(data === undefined || data.length == 0) {
                  alert("No results returned by the system...");
                } else {
                  $scope.experts = data;
                }
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
              console.log(data);
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
            console.log(data);
            $scope.$emit('showUnavailableText', true);
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

      erControllers.controller('settingsCtrl', ['$scope','$rootScope',
        function($scope, $rootScope) {

          $scope.algorithm = { value: '' };

          $scope.choices = [{
              id: 1,
              name: "datamodeling"
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

          //$rootScope.parameter = {};
          
          console.log("settingsCtrl Controller...");
          $scope.options = {        
              from: 0,
              to: 100,
              step: 1,
              skin: 'round',
              dimension: "%",
              callback: function(value, elt) {
                $rootScope.parameter = value / 100;
              }
          };

          $rootScope.parameter = $scope.options;


          $scope.handleSelection = function() {
            
            if($scope.algorithm.value === "datamodeling") {
              $rootScope.showVis = false;
              $scope.$emit('disableVisualization', true);
            } else {
              $scope.$emit('disableVisualization', false);
            }

            console.log($scope.algorithm.value);
            $rootScope.algorithm = $scope.algorithm.value;
            
          }

        }]);