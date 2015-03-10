

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

markCurrentActivities = function(BpmnViewer, obj) {
    var activityNodes = $('g[data-element-id]:gt(1)', obj);
    
    activityNodes.each(function() {
        $(this).attr('class', $(this).attr('class').replace('current-activity', ''));
    })
    
    $('.activity_instance').each(function() {
        var currentActivityNode = $('g[data-element-id="' + $(this).data('activity_id') + '"]', obj);
        
        currentActivityNode.attr('class', currentActivityNode.attr('class') + ' current-activity');
    });
};

var jsSet = function() {
    require(['bpmn-viewer'], function(BpmnViewer) {
        
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
                    
                    markCurrentActivities(BpmnViewer, obj);
                });
            };
        };
        
        $('.process_model')
            .each(function() {
                if ($(this).data('set') != 1) {
                    $.get("/process/" + $(this).data('process_model'), getLoadingCallback($(this)), 'text');
                }
                markCurrentActivities(BpmnViewer, $(this));
            }).data('set', 1);
    });

    require(['datatables'], function(DataTables) {
        $('table.dataTable')
            .each(function() {
                if ($(this).data('set') != 1) {
                    $(this).DataTable({
                        "lengthMenu": [ [10, 25, 50], [10, 25, 50] ]
                    });
                }
            }).data('set', 1);
    });

    require(['jquery-ui'], function() {
        $('form:not(.upload)')
            .each(function() {
                if ($(this).data('set') != 1) {
                    $(this).on('submit', function(e) {
                        e.preventDefault();
                        
                        var target = $($(this).data('target'));
                        
                        target.prepend('<div class="loader">Receiving data</loader>');
                        
                        $.ajax({
                            type: $(this).attr('method'),
                            url: $(this).attr('action') + '?contentonly',
                            data: $(this).serialize(),
                            success: function(re) {
                                $('.loader', target).remove();
                                target.html(re);
                                
                                jsSet();
                            },
                            error: function(re) {
                                alert('Request error:\n\n' + re.responseText);
                                $('.loader', target).remove();
                            }
                        });
                        return false;
                    });
                }
            }).data('set', 1);
    });
};

jsSet();