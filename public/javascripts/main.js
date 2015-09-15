

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
                            var activityId = $(this).attr('data-element-id');
                            
                            if (location.hash == '#activity-designer') {
                                var decisionActivityInput = $('input[name="decision_activity"].selected');
                                
                                if (decisionActivityInput.length == 1) {
                                    decisionActivityInput.prev('input[name="decision_activity_name"]').removeClass('selected').val($(this).text()).data('activity-name', $(this).text());
                                    decisionActivityInput.val(activityId).removeClass('selected').closest("form").submit();
                                }
                                else {
                                    $('g[data-element-id*="Task_"]').each(function() {
                                        $(this).attr('class', $(this).attr('class').replace('current-activity', ''));
                                    });
                                    $(this).attr('class', $(this).attr('class') + ' current-activity');
                                    
                                    $('#activity-designer form').data('target', '').submit();
                                    $('form', this).trigger('submit');
                                }
                            }
                            else {
                                var phase = $('.phase-list .selected');
                                
                                if (phase.length == 1) {
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

ajaxRequest = function(targetData, requestURL, requestMethod, requestData) {
    var targets = new Array();
    
    if (targetData != null) {
        var targetNodes = targetData.split('|');
        
        for (key in targetNodes) {
            var target = $(targetNodes[key]);
            
            targets[key] = target;
            target.prepend('<div class="loader">Receiving data</loader>');
        }
    }

    if (requestURL.indexOf('?') == -1) {
        requestURL = requestURL + '?';
    }
    else {
        requestURL = requestURL + '&';
    }
    requestURL = requestURL + 'contentonly';
    
    $.ajax({
        type: requestMethod,
        url: requestURL,
        data: requestData,
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
                var newViewName = re[i].replace(/(^\/)|([\s])|(#.*)/g, '');
                
                $('#general').attr('class', $('#general').attr('class').replace(/(^|\s)view-([^\s]*)(\s|$)/gi, '$1view-' + newViewName + '$3'));
                
                if (window.history.pushState) {
                    window.history.pushState(null, null, re[i]);
                }
                else {
                    window.location = location.href.substr(0, strpos(location.href, '#')) + '#' + re[i];
                }
            }
            jsSet();
            
            setTimeout(function() {
                for (var i = 0; i < targets.length; i++) {
                    targets[i].css('display', 'none');
                    targets[i].css('display', '');
                }
            }, 10);
        },
        error: function(re) {
            alert('Request error:\n\n' + re.responseText);
            
            for (key in targets) {
                $('.loader', targets[key]).remove();
            }
        }
    });
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
                        if ($('a', this).prop('href') == location.href && $('a', this).prop('href') != location.href + location.hash) {
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
                            
                            ajaxRequest($(this).data('target'), $(this).attr('action'), $(this).attr('method'), $(this).serialize());
                            
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
            
            $('.process_modeler .nav a[href="#phase-designer"]:not(.set)')
                .on('vclick', function() {
                    $('g[data-element-id*="Task_"]').each(function() {
                        $(this).attr('class', $(this).attr('class').replace('current-activity', ''));
                    });
                })
                .addClass('set');
            
            $('input[name="decision_activity_name"]:not(.set)')
                .each(function() {
                    $(this).data('activity-name', $(this).val());
                })
                .on('vclick', function() {
                    $('input[name="decision_activity"]').removeClass('selected').prev('input[name="decision_activity_name"]').removeClass('selected').each(function() {
                        if ($(this).next('input[name="decision_activity"]').val() == '') {
                            $(this).val('');
                        }
                        else if ($(this).data('activity-name') != false) {
                            $(this).val($(this).data('activity-name'));
                        }
                    });
                    $(this).val($(this).data('alt_value')).addClass('selected').next('input[name="decision_activity"]').addClass('selected');
                })
                .addClass('set');
            
            $('.activity-instance tbody tr:not(.set)')
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
                    
                    if ($(this).parents('.activity-operators').length > 0) {
                        ajaxRequest('.related-documents', '/getDocumentExplorer?boType=' + $(this).data('bo-type') + '&sapId=' + $('td:eq(0) input', this).val(), 'get', {});
                    }
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
            
            
            $('a[data-target]:not(.set)')
                .on('vclick', function(e) {
                    e.preventDefault();
                    console.log('link clicked');
                    console.log($(this))
                    ajaxRequest($(this).data('target'), $(this).attr('href'), 'get', {});
                    
                    return false;
                })
                .addClass('set');
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
    
    require(['jquery'], function($) {
        if ($('#general').hasClass('view-managerDashboard')) {
            require(["c3"], function(c3) {
                setTimeout(function() {
                    var defaultType = 'donut';
                    var alternativeType = 'bar';
                    
                    var chart1 = c3.generate({
                        bindto: '.chart.instances-per-process > .diagram',
                        data: {
                            url: '/data/instancesOfPMs',
                            mimeType: 'json',
                            type: defaultType,
                        },
                        color: {
                            pattern: ['#003459', '#0073BF', '#0096FB']
                        }
                    });
                    
                    $('#button').data('currentType', defaultType).on('click', function(e) {
                        if ($(this).data('currentType') == defaultType){
                            $(this).data('currentType', alternativeType);
                        }
                        else {
                            $(this).data('currentType', defaultType);
                        }    
                        
                        chart1.transform($(this).data('currentType'));
                    });
                }, 3500);
                
                var chart2 = c3.generate({
                    bindto: '.chart.duration-per-process > .diagram',
                    data: {
                        columns: [
                            ['best', 34.5, 20, 48],
                            ['worst', 54.3, 31.8, 76],
                            ['average', 40.6, 26.3, 63],
                        ],/*
                        url: '/data/timePerPM',
                        mimeType: 'json',*/
                        type: 'bar',
                    },
                    color: {
                        pattern: ['#0096FB', '#003459', '#0073BF']
                    },
                    axis: {
                        x: {
                            type: 'category',
                            categories: ['Sales Process', 'Procurement Process', 'Customer Inquiry Processing'],
                        }
                    }
                });
                
                
                
                
                
                
                
                var now = new Date();
                var aDay = 1000*60*60*24;

                var chart3 = c3.generate({
                    bindto: '.chart.new-instances-per-process > .diagram',
                    data: {
                        x: 'x',
                        columns: [
                            ['x', new Date((now.getTime()-6*aDay)), new Date(now.getTime()-5*aDay), new Date(now.getTime()-4*aDay), new Date(now.getTime()-3*aDay), new Date(now.getTime()-2*aDay), new Date().setTime(now.getTime()-aDay), now],
                            ['Procurement Process', 3, 4, 2, 6, 1, 4, 5]
                        ]
                    },
                    color: {
                        pattern: ['#003459', '#0073BF', '#0096FB']
                    },
                    axis: {
                        x: {
                            type: 'timeseries',
                            tick: {
                                format: '%d-%m-%Y'
                            }
                        }
                    }
                });

                setTimeout(function() {
                    chart3.load({
                        columns: [
                            ['Procurement Process', 3, 4, 2, 6, 1, 4, 5]
                        ]
                    });
                }, 1000);

                setTimeout(function() {
                    chart3.load({
                        columns: [
                            ['Sales Process', 2, 1, 6, 3, 4, 8, 2]
                        ]
                    });
                }, 2000);

                setTimeout(function() {
                    chart3.load({
                        columns: [
                            ['Customer Inquiry Processing', 14, 8, 18, 21, 28, 17, 8]
                        ]
                    });
                }, 3000);
                
                
                setTimeout(function() {
                    var chart = c3.generate({
                        bindto: '.chart.instances-in-time > .diagram',
                        data: {
                            columns: [
                                ['data', 89.4]
                            ],
                            type: 'gauge'
                        },
                        color: {
                            pattern: ['#0096FB', '#0073BF', '#003459', '#4C7013'],
                            threshold: {
                                values: [30, 60, 90, 100]
                            }
                        }
                    });
                }, 2000);
            });
        }
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
