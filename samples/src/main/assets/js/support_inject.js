function showImage(rawurl, localpath) {
	var elements = document.getElementsByTagName("img");
	for(var i= 0; i< elements.length; i++){
		var url = elements[i].getAttribute("image-attach-src");
		if (rawurl == url) {
			elements[i].setAttribute("src", localpath)
		}

	}
}

function locationLeft(element){
    var offsetTotal = element.offsetLeft;
    scrollTotal = 0;
    if (element.tagName != "BODY"){
       if (element.offsetParent != null)
          return offsetTotal + scrollTotal + locationLeft(element.offsetParent);
    }
    return offsetTotal+scrollTotal;
}

function locationTop(element){
    offsetTotal = element.offsetTop;
    scrollTotal = 0;
    if (element.tagName != "BODY"){
       if (element.offsetParent != null)
          return offsetTotal+scrollTotal+locationTop(element.offsetParent);
    }
    return offsetTotal+scrollTotal;
}

function doInject() {
    var images = document.getElementsByTagName("img");
    for(var i=0; i< images.length; i++){
    	var src = images[i].getAttribute("src");
    	var w = images[i].width;
    	var h = images[i].height;
    	images[i].setAttribute("image-attach-src", src);
    	images[i].setAttribute("src", "hybird://method/image_load?url=" + encodeURIComponent(src) + "&w=" + w + "&h=" + h);
    	images[i].onclick = function() {
    		var w = this.width;
		    var h = this.height;
		    var left = locationLeft(this);
		    var top = locationTop(this);
		    var src = this.getAttribute("image-attach-src");
		    window.location.href = "hybird://method/image_show?url=" + encodeURIComponent(src) + "&w=" + w + "&h=" + h + "&t=" + top + "&l=" + left;
    	}
    }
    alert('success');
}
doInject();
