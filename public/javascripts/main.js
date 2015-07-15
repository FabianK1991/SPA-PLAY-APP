

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
     $('.process_model_viewer').each(function() {
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
    var diagram = $('.process_model', obj);
    var activityNodes = $('g[data-element-id]:gt(1)', obj);
    
    activityNodes.each(function() {
        $(this).attr('class', $(this).attr('class').replace('current-activity', ''));
    });
    
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

getLoadingCallback = function(BpmnViewer, obj) {
        try {
            var bpmnViewer = new BpmnViewer({
                container: $('.process_model', obj)
            });
        }
        catch(e) {
            console.log(e);
            return false;
        }
        
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
                
                $('.process_model', obj).data('w', viewport.width).data('h', viewport.height).css({width: viewport.width + 'px', height: viewport.height + 'px'});
                
                markCurrentActivities(BpmnViewer, obj);
                activatePanZoom(obj);
                
                if (obj.parents('.process_modeler').length > 0) {
                    $('g[data-element-id*="Task_"]')
                        .each(function() {
                            var submitForm = $('<form>').attr('method', 'get').attr('action', '/activityDesigner?model=' + obj.data('process_model') + '&activity=' + $(this).data('element-id')).data('target', '#activity-designer');
                            
                            $(this).append(submitForm);
                        })
                        .on('vclick', function() {
                            if (location.hash == '#activity-designer') {
                                $('form', this).trigger('submit');
                            }
                            else {
                                var phase = $('.phase-list .selected');
                                
                                if (phase.length == 1) {
                                    var activityId = $(this).attr('data-element-id');
                                    
                                    if ($(this).attr('class').indexOf('phase-activity') != -1) {
                                        $('.delete-form').attr('method', 'post').attr('action', phase.data('delete-activity').replace('_placeholder_', activityId)).submit();
                                        
                                        phase.data('activities', phase.data('activities').replace(activityId + '|', ''));
                                        
                                        $(this).attr('class', $(this).attr('class').replace('phase-activity', ''));
                                    }
                                    else {
                                        $('.delete-form').attr('method', 'post').attr('action', phase.data('add-activity').replace('_placeholder_', activityId)).submit();
                                        
                                        phase.data('activities', phase.data('activities') + activityId + '|');
                                        
                                        $(this).attr('class', $(this).attr('class') + ' phase-activity');
                                    }
                                }
                            }
                        })
                        .css('cursor', 'pointer');
                    
                    jsSet();
                }
                $(window).resize(function() {
                    markCurrentActivities(BpmnViewer, obj);
                });
            });
        };
    };

loadBPMNviewers = function() {
    
};

var jsSet = function() {
    require(['bpmn-viewer', 'jquery-panzoom'], function(BpmnViewer) {
        $('.process_model_viewer')
            .each(function() {
                if ($(this).data('set') != 1) {
                    callbackFunction = getLoadingCallback(BpmnViewer, $(this));
                    
                    if (callbackFunction == false) {console.log('no callback...');
                        setTimeout(function() {
                            loadBPMNviewers();
                        }, 200);
                        return false;                       
                    }
                    $.get("/process/" + $(this).data('process_model'), callbackFunction, 'text');
                    
                    $(this).data('set', 1);
                }
                if ($('.loader', this).length == 0) {
                    //markCurrentActivities(BpmnViewer, $(this));
                }
            });
    });

    require(['jquery-ui'], function() {
        $(document).bind("mobileinit", function () {
            $.mobile.linkBindingEnabled = false;
            $.mobile.ajaxEnabled = false;
            $.mobile.ajaxLinksEnabled = false;
            $.mobile.ajaxFormsEnabled = false;
            $.extend($.mobile, {autoInitializePage: false});
        });
        
        if ($('body').data('dialog-delete') === undefined) {
            $('body').append($('<form>').addClass('delete-form'));
            
            var dialog = $.ui.dialog({
                resizable: false,
                modal: true,
                autoOpen: false,
                buttons: {
                    "Delete": function() {
                        var obj = $('body').data('delete-object');
                        
                        var deleteForm = $('.delete-form').attr('method', 'post').attr('action', obj.data('delete'));
                        
                        deleteForm.submit();
                        
                        obj.remove();
                        
                        $('body').data('dialog-delete').close();
                    },
                    Cancel: function() {
                        $('body').data('dialog-delete').close();
                    }
                }
            }, $('#dialog-delete'));
            
            $('body').data('dialog-delete', dialog);
        }
        
        require(['jquery-mobile'], function() {
            $('.nav > *')
                .each(function() {
                    if ($(this).data('set') != 1) {
                        if ($('a', this).prop('href') == location.href) {
                            $(this).addClass('active');
                            location.href = $('a', this).prop('href');
                        }
                        $(this).on('vclick', function() {
                            $('> *', $(this).parent()).removeClass('active');
                            $(this).addClass('active');
                        })
                    }
                })
                .data('set', 1);
            
            $('form:not(.upload)')
                .each(function() {
                    if ($(this).data('set') != 1) {
                        if ($(this).hasClass('auto-submit')) {
                            $('select', this).on('change', function() {
                                $(this).parents('form').submit();
                            });
                            $('input', this).on('change', function() {
                                var form = $(this).parents('form');
                                
                                oldCT = form.data('auto-submit-ct');
                                
                                if (oldCT !== undefined) {
                                    clearTimeout(oldCT);
                                }
                                var ct = setTimeout(function() {
                                    form.submit();
                                }, 1000);
                                form.data('auto-submit-ct', ct);
                            });
                        }
                        $(this).on('submit', function(e) {
                            e.preventDefault();
                            
                            var targets = new Array();
                            
                            var targetData = $(this).data('target');
                            
                            if (targetData != null) {
                                var targetNodes = targetData.split('|');
                                
                                for (key in targetNodes) {
                                    var target = $(targetNodes[key]);
                                    
                                    targets[key] = target;
                                    target.prepend('<div class="loader">Receiving data</loader>');
                                }
                            }
                            requestURL = $(this).attr('action');

                            if (requestURL.indexOf('?') == -1) {
                                requestURL = requestURL + '?';
                            }
                            else {
                                requestURL = requestURL + '&';
                            }
                            requestURL = requestURL + 'contentonly';
                            
                            $.ajax({
                                type: $(this).attr('method'),
                                url: requestURL,
                                data: $(this).serialize(),
                                success: function(re) {
                                    re = re.split('||');
                                    
                                    var i = 0;
                                    
                                    while (i < targets.length) {
                                        var target = targets[i];
                                        
                                        $('.loader', target).remove();
                                        
                                        if (re[i] !== undefined) {
                                            target.html(re[i]);
                                        }
                                        i++;
                                    }
                                    
                                    if (re[i] !== undefined && re[i] != '') {
                                        $('body').attr('class', $('body').attr('class').replace(/(^|\s)view-([^\s]*)(\s|$)/gi, '$1view-' + re[i].replace(/(^\/)|([\s])/g, '') + '$3'));
                                        
                                        if (window.history.pushState) {
                                            window.history.pushState(null, null, re[i]);
                                        }
                                        else {
                                            window.location = location.href.substr(0, strpos(location.href, '#')) + '#' + re[i];
                                        }
                                    }
                                    jsSet();
                                },
                                error: function(re) {
                                    alert('Request error:\n\n' + re.responseText);
                                    
                                    for (key in targets) {
                                        $('.loader', targets[key]).remove();
                                    }
                                }
                            });
                            return false;
                        });
                    }
                }).data('set', 1);
            
            $('*[data-delete]:not(.delete-set)')
                .prepend('<div class="delete-button">x</div>')
                .on('tap', function() {
                    var isHover = $(this).hasClass('hover');
                    
                    $('*').removeClass('hover');
                    
                    if (isHover == false) {
                        $(this).addClass('hover');
                    }
                })
                .addClass('delete-set');
            
            $('.delete-button:not(.set)')
                .on('vclick', function() {
                    var obj = $(this).parent();
                    
                    $('body').data('delete-object', obj);
                    
                    $('body').data('dialog-delete').open();
                })
                .addClass('set');
            
            $('.phase-list li:not(.set)')
                .on('vclick', function() {
                    var isSelected = $(this).hasClass('selected');
                    
                    $('.phase-list li').removeClass('selected');
                    
                    $('g[data-element-id*="Task_"]').each(function() {
                        $(this).attr('class', $(this).attr('class').replace('phase-activity', ''));
                    });
                    
                    if (isSelected) {
                        $(this).removeClass('selected');
                    }
                    else {
                        $(this).addClass('selected');
                        
                        phaseActivities = $(this).data('activities').split('|');
                        
                        $('g[data-element-id*="Task_"]')
                            .each(function() {
                                if ($.inArray($(this).attr('data-element-id'), phaseActivities) != -1) {
                                    $(this).attr('class', $(this).attr('class') + ' phase-activity');
                                }
                            });
                    }
                })
                .addClass('set');
            
            $('.process_modeler .nav a[href="#activity-designer"]:not(.set)')
                .on('vclick', function() {
                    $('.phase-list li.selected').trigger('vclick');
                })
                .addClass('set');
            
            $('tbody tr:not(.set)')
                .on('vclick', function() {
                    var checkbox = $('input[type="checkbox"]', this);
                    var checked  = checkbox.prop("checked");
                    
                    if (checked) {
                        $(this).removeClass('selected');
                    }
                    else {
                        $(this).addClass('selected');
                    }
                    checkbox.prop("checked", !checked);
                })
                .addClass('set');
            
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
        });
    });
    
    require(['chosen'], function() {
        $('select:not([name^="DataTables"])')
            .each(function() {
                if ($('option[selected], option.default', this).length == 0 && $(this).is('*:not([multiple])')) {
                    $(this).prepend('<option class="default" selected></option>');
                }
            })
            .chosen({allow_single_deselect: false});
    });
};

activatePanZoom = function(modelViewers) {
    modelViewers
        .each(function() {
            $('.process_model', this)
                .panzoom({
                    $zoomIn: $(".zoom-in", this),
                    $zoomOut: $(".zoom-out", this),
                    $zoomRange: $(".zoom-range", this),
                    $reset: $(".reset", this),
                    contain: 'invert'
                })
                .addClass('zoom-ready');
        });
};

jsSet();
