function loadDiv() {
    var div = "<div id='_layer_'> <div id='_MaskLayer_' style='filter: alpha(opacity=30); -moz-opacity: 0.3; opacity: 0.3;background-color: #000; width: 100%; height: 100%; z-index: 1000; position: absolute;" +
        "left: 0; top: 0; overflow: hidden; display: none'></div><div id='_wait_' style='z-index: 1005; position: absolute; width:430px;height:218px; display: none'  ><center><h3>" +
        "<img src='./h5files/images/load.gif' /></h3></center></div></div>";
    return div;
}

function LayerShow() {
    var addDiv = loadDiv();
    var element =$('body').append(addDiv);
    $(window).resize(Position);
    var deHeight = $(document).height();
    var deWidth = $(document).width();
    Position();
    $("#_MaskLayer_").show();
    $("#_wait_").show();
}

function Position() {
    $("#_MaskLayer_").width($(document).width());
    var deHeight = $(window).height();
    var deWidth = $(window).width();
    $("#_wait_").css({
        left: (deWidth - $("#_wait_").width()) / 2 + "px",
        top: (deHeight - $("#_wait_").height()) / 2 + "px"
    });
}

function LayerHide() {
    $("#_MaskLayer_").hide();
    $("#_wait_").hide();
    del();
}

function del() {
    var delDiv = document.getElementById("_layer_");
    delDiv.parentNode.removeChild(delDiv);
}