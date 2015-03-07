/*

require({
	baseUrl: "/assets/javascripts/camunda-bpmn/",
	paths: {
	    'jquery' : 'lib/jquery/jquery-1.7.2.min',
	    'bpmn/Bpmn' : 'build/bpmn.min',
	},
	packages: [
	    { name: "dojo", location: "lib/dojo/dojo" },
	    { name: "dojox", location: "lib/dojo/dojox"},
	    // provided by build/bpmn.min.js
	    // { name: "bpmn", location: "src/bpmn" }
	]
});

require(["bpmn/Bpmn", "dojo/domReady!"], function(Bpmn) {
     $('.process_model').each(function() {
	     new Bpmn().renderUrl("/process/" + $(this).data('process_model'), {
	        diagramElement : $(this).get(0),
	      	overlayHtml : '<div style="position: relative; top:100%"></div>'
	     }).then(function (bpmn){
	     	$('.loader', this).remove();
	        bpmn.zoom(0.8);
	        
	        return false;
	        bpmn.annotation("sid-C7031B1A-7F7E-4846-B046-73C638547449").setHtml('<span class="bluebox"  style="position: relative; top:100%">New Text</span>').addClasses(["highlight"]);
	        bpmn.annotation("sid-C7031B1A-7F7E-4846-B046-73C638547449").addDiv("<span>Test Div</span>", ["testDivClass"]);
	     });
     });
});

*/



/**
 * bpmn-js-seed - async
 *
 * This is an example script that loads a bpmn diagram <diagram.bpmn> and opens
 * it using the bpmn-js viewer.
 *
 * YOU NEED TO SERVE THIS FOLDER VIA A WEB SERVER (i.e. Apache) FOR THE EXAMPLE TO WORK.
 * The reason for this is that most modern web browsers do not allow AJAX requests ($.get and the like)
 * of file system resources.
 */
(function(BpmnViewer, $) {
	
	var getLoadingCallback = function(obj) {
        var bpmnViewer = new BpmnViewer({
            container: obj
        });
        
        // import function
        return function(xml) {
            // import diagram
            bpmnViewer.importXML(xml, function(err) {
            
                if (err) {
                    return console.error('could not import BPMN 2.0 diagram', err);
                }
                
                $('.loader', obj).remove();
                
                var canvas = bpmnViewer.get('canvas'),
                  overlays = bpmnViewer.get('overlays');
                
                
                // zoom to fit full viewport
                canvas.zoom(0.6);
                
                $('.activity_instance').each(function() {
                    var currentActivityNode = $('g[data-element-id="' + $(this).data('activity_id') + '"]', obj);
                    
                    currentActivityNode.attr('class', currentActivityNode.attr('class') + ' current-activity');
                });
                
                var activityNodes = $('g[data-element-id]:gt(1)', obj);
                
                activityNodes.on('click', function(e) {
                    if ($.inArray($(this).data('element-id'), obj.data('next_activities').split(',')) != -1) {
                        $.ajax({
                            url: '/setCurrentActivity',
                            type: 'POST',
                            complete: function(re) {
                                console.log(re);
                            },
                            error: function() {
                                console.log('error');
                            },
                            data: {process_instance: obj.data('process_instance'), activity_id: $(this).data('element-id')}
                        });
                    }
                });
            });
        };
	};
    
	$('.process_model').each(function() {
	  // create viewer
	  
	
	
	  // load external diagram file via AJAX and import it
	  $.get("/process/" + $(this).data('process_model'), getLoadingCallback($(this)), 'text');
  });


})(window.BpmnJS, window.jQuery);
