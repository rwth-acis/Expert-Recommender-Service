$(function(){
	$(".pagerankbtn").click(function() {
		//alert();
		setQueryAndSearchType("pagerank");
		$(location).attr('href','results.html');
	});

	$(".hitsbtn").click(function() {
		//alert();
		setQueryAndSearchType("hits");
		$(location).attr('href','results.html');
	});

	$(".modelbtn").click(function() {
		//alert();
		setQueryAndSearchType("modeling");
		$(location).attr('href','results.html');
	});

	$(".usermodelbtn").click(function() {
		//alert();
		setQueryAndSearchType("usermodeling");
		$(location).attr('href','results.html');
	});

	var setQueryAndSearchType = function(searchtype) {
    	if (typeof(Storage) != "undefined") {
    		localStorage.setItem("query", $(".qbox").val());
    		localStorage.setItem("searchtype", searchtype);
		} else {
			alert("Please use modern browser that supports HTML5...");
		}

	}

});