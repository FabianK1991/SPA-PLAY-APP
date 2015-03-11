

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

var markCurrentActivities = function(BpmnViewer, obj) {
    var diagram = $('.process_model-inner', obj);
    var activityNodes = $('g[data-element-id]:gt(1)', obj);
    
    activityNodes.each(function() {
        $(this).attr('class', $(this).attr('class').replace('current-activity', ''));
    })
    
    $('.activity_instance').each(function() {
        var currentActivityNodes = $('g[data-element-id="' + $(this).data('activity_id') + '"]', obj);
        
        identifiers = ($(this).data('next_activities') + $('.content', this).data('business_objects')).split(',');
        
        var sel = '';
        
        for (key in identifiers) {
            if (identifiers[key] != false) {
                sel = sel + ',g[data-element-id="' + identifiers[key] + '"]';
            }
        }
        var focusNodes = $(sel.substring(1), obj).add(currentActivityNodes);
        
        var minX = 1000000;
        var maxX = 0;
        var minY = 1000000;
        var maxY = 0;
        
        currentScale = diagram.get(0).getBoundingClientRect().width / diagram.get(0).offsetWidth;
        
        focusNodes.each(function() {
            if (currentActivityNodes.index($(this)) != -1) {
                $(this).attr('class', $(this).attr('class') + ' current-activity');
            }
            var rect = $(this)[0].getBoundingClientRect();
            var pos = $(this).position();
            
            var l = pos.left / currentScale;
            var t = pos.top / currentScale;
            var w = rect.width / currentScale;
            var h = rect.height / currentScale;
            
            if (l < minX) {
                minX = l;
            }
            if (l + w > maxX) {
                maxX = l + w;
            }
            if (t < minY) {
                minY = t;
            }
            if (t + h > maxY) {
                maxY = t + h;
            }
        });
        var scaleX = 1;
        var scaleY = 1;
        
        maxX = maxX+20;
        minX = minX-20;
        maxY = maxY+20;
        minY = minY-20;
        
        if (obj.width() < maxX-minX) {
            scaleX = obj.width() / (maxX-minX);
        }
        if (obj.height() < maxY-minY) {
            scaleY = obj.height() / (maxY-minY);
        }
        var scale = Math.min(scaleX, scaleY);
        
        var mLeft = ((obj.width() - (maxX-minX)*scale) / 2 - minX*scale) - diagram.data('w')*(1 - scale)/2;
        var mTop = ((obj.height() - (maxY-minY)*scale) / 2 - minY*scale) - diagram.data('h')*(1 - scale)/2;
        
        diagram.css({marginLeft: mLeft + 'px', marginTop: mTop + 'px', transform: 'scale(' + scale + ')'});
    });
};

var jsSet = function() {
    require(['bpmn-viewer'], function(BpmnViewer) {
        
        var getLoadingCallback = function(obj) {
            var bpmnViewer = new BpmnViewer({
                container: $('.process_model-inner', obj)
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
                    /*currently no zooming!
                    canvas.zoom(0.6);
                    
                    $('g.viewport', obj).attr('transform', '');
                    */
                    var viewport = $('g.viewport', obj)[0].getBoundingClientRect();
                    
                    $('.process_model-inner', obj).data('w', viewport.width).data('h', viewport.height).css({width: viewport.width + 'px', height: viewport.height + 'px'});
                    
                    markCurrentActivities(BpmnViewer, obj);
                    
                    $(window).resize(function() {
                        markCurrentActivities(BpmnViewer, obj);
                    });
                });
            };
        };
        
        $('.process_model')
            .each(function() {
                if ($(this).data('set') != 1) {
                    $.get("/process/" + $(this).data('process_model'), getLoadingCallback($(this)), 'text');
                }
                if ($('.loader', this).length == 0) {
                    markCurrentActivities(BpmnViewer, $(this));
                }
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
                        
                        var targets = new Array();
                        
                        var targetNodes = $(this).data('target').split('|');
                        
                        for (key in targetNodes) {
                            var target = $(targetNodes[key]);
                            
                            targets[key] = target;
                            target.prepend('<div class="loader">Receiving data</loader>');
                        }
                        $.ajax({
                            type: $(this).attr('method'),
                            url: $(this).attr('action') + '?contentonly',
                            data: $(this).serialize(),
                            success: function(re) {
                                re = re.split('|');
                                
                                for (key in targets) {
                                    var target = targets[key];
                                    
                                    $('.loader', target).remove();
                                    
                                    if (re[key] !== undefined) {
                                        target.html(re[key]);
                                    }
                                }
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