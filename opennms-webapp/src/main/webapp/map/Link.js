// == link.js -- 

Link.prototype = new SVGElement;
Link.superclass = SVGElement.prototype;

function Link(id, mapElement1, mapElement2, stroke, stroke_width, dash_array, flash)
{
	if (arguments.length >= 5) { 
		this.animateTag = null;
		this.id = id;
		this.mapElement1 = mapElement1;
		this.mapElement2 = mapElement2;
		var x1 = this.mapElement1.getCPoint().x;
		var x2 = this.mapElement2.getCPoint().x;
		var y1 = this.mapElement1.getCPoint().y;
		var y2 = this.mapElement2.getCPoint().y;
		//Title.scale  = doc.getDocumentElement().getCurrentScale();     // scaling modified by zooming ..
		//Title.offset = doc.getDocumentElement().getCurrentTranslate(); // offset modified by zooming ..
		this.init(id, x1, x2, y1, y2, stroke, stroke_width, dash_array, flash);
		//doc.getDocumentElement().addEventListener("zoom", Title.Zoom, false);
	}
	else
		alert("Link constructor call error");
}

// <line onclick="*" stroke="*" stroke-width="*"/>
Link.prototype.init = function(id, x1, x2, y1, y2, stroke, stroke_width, dash_array, flash)
{
	this.svgNode = mapSvgDocument.createElement("line");
	this.svgNode.setAttribute("id", id);	
	this.svgNode.setAttribute("x1", x1);	
	this.svgNode.setAttribute("x2", x2);	
	this.svgNode.setAttribute("y1", y1);
	this.svgNode.setAttribute("y2", y2);
	this.svgNode.setAttribute("stroke", stroke);
	this.svgNode.setAttribute("stroke-width", stroke_width);
	if(dash_array!=-1 && dash_array!=0)
		this.svgNode.setAttribute("dash-array", dash_array);
	this.svgNode.setAttribute("style", "z-index:0");
	this.svgNode.addEventListener("click", this.onClick, false);

	this.animateTag = mapSvgDocument.createElement("animate");
	this.animateTag.setAttribute("attributeName", "stroke");	
	this.animateTag.setAttribute("from", stroke);	
	this.animateTag.setAttribute("to", "white");
	this.animateTag.setAttribute("dur", "0ms");
	this.animateTag.setAttribute("repeatCount", "indefinite");
	this.animateTag.addEventListener("repeat", this.onRepeat, false);
	this.svgNode.appendChild(this.animateTag);
	this.flash=false;
	if(flash!=undefined && flash==true)
		this.setFlash(true);
}

Link.prototype.getStroke = function() {
	return this.svgNode.getAttribute("stroke");
}

Link.prototype.getStrokeWidth = function() {
	return this.svgNode.getAttribute("stroke-width");
}

Link.prototype.getDashArray = function() {
	return this.svgNode.getAttribute("dash-array");
}

Link.prototype.getFlash = function() {
	return this.flash;
}

/*
	set flashing of link
*/
Link.prototype.setFlash = function(bool)
{
	this.flash=bool;
	if (this.animateTag != null) 
	{
		var val;
		if (bool)
			val = "400ms";
		else
			val = "0";
		
		this.animateTag.setAttribute("dur", val);
	}
}

Link.prototype.getFirstElementId = function()
{
     var ids = this.id.split('-');
     return ids[0];
}

Link.prototype.getSecondElementId = function()
{
     var ids = this.id.split('-');
     return ids[1];
}

// update link
Link.prototype.update = function()
{
	this.svgNode.setAttribute("x1", this.mapElement1.getCPoint().x);	
	this.svgNode.setAttribute("x2", this.mapElement2.getCPoint().x);	
	this.svgNode.setAttribute("y1", this.mapElement1.getCPoint().y);
	this.svgNode.setAttribute("y2", this.mapElement2.getCPoint().y);	
}

Link.prototype.getInfo = function(evt)
{	
	var str ="<text id=\"TopInfoText\" x=\"3\" y=\"20\">Link "+
		"<tspan x=\"3\" dy=\"30\" font-size=\"9\">from: " + this.mapElement1.label.text + "</tspan>"+
		"<tspan x=\"3\" dy=\"10\" font-size=\"9\"> to: "+this.mapElement2.label.text + "</tspan>" +
		"</text>";
	return str;
}

/*
	OnRepeat event handler
*/
Link.prototype.onRepeat = function(evt)
{
	var from = evt.getTarget().getAttribute("from");
	var to = evt.getTarget().getAttribute("to");
	if (from == "white") {
		from = evt.getTarget().parentNode.getAttribute("stroke");
		to = "white";
	}
	else {
		from = "white";
		to = evt.getTarget().parentNode.getAttribute("stroke");
	}
	evt.getTarget().setAttribute("from", from);
	evt.getTarget().setAttribute("to", to);	
}

Link.prototype.onClick = onMouseDownOnLink;
Link.prototype.onMouseUp = onMouseUp;
