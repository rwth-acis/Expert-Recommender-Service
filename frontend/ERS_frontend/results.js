$(function(){
	//alert(localStorage.getItem("query"));
	var query = localStorage.getItem("query");
	var searchtype = localStorage.getItem("searchtype");
	var url = "";
	var basepath = "http://localhost:8080/recommender_framework/";
	if(searchtype == "hits") {
		console.log("Made request for HITS...");
		url = basepath+"hits";
	} else if(searchtype == "modeling") {
		console.log("Made request for Modeling...");
		url = basepath+"modeling"; 
	}  else if(searchtype == "usermodeling") {
		console.log("Made request for usermodeling...");
		url = basepath+"usermodeling";
	}
	else {
		console.log("Made request for PageRank...");
		url = basepath+"pagerank";
	}

	 $.post( url, query)
	  	.done(function( data ) {
		    		//alert( "Data Loaded: " + data );
	    	
		    //console.log(data);
	    	var result = JSON.parse(data);
	    	console.log(result);
	    	//TODO: Replace this with template engine.
	    	for(i =0 ; i < result.length ; i++) {
	    		var template_node = "<div class='list_item'><div class='profile_pic'></div><div class='user_details'><div class='name'>"+result[i].userName+"</div><div class='email'>"+result[i].websiteUrl+"</div></div><div class='related_post'>"+result[i].title+"</div><div class='related_topics'>Related_topics</div></div>";
	    		//$(temp.get(0)).clone().appendTo('#outer_container');
	    		$('#outer_container').append(template_node);
	    	}

	    	/*
	    	for(i in result) {
	    		$('#outer_container').append($(".list_item").clone());
	    	}*/
	});
});