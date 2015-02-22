if (window.console) {
  console.log($);
}


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
      new Bpmn().renderUrl("/assets/javascripts/camunda-bpmn/test/resources/diagram.bpmn", {
        diagramElement : "diagram",
        overlayHtml : '<div style="position: relative; top:100%"></div>'
      }).then(function (bpmn){
        bpmn.zoom(0.8);
        bpmn.annotation("sid-C7031B1A-7F7E-4846-B046-73C638547449").setHtml('<span class="bluebox"  style="position: relative; top:100%">New Text</span>').addClasses(["highlight"]);
        bpmn.annotation("sid-C7031B1A-7F7E-4846-B046-73C638547449").addDiv("<span>Test Div</span>", ["testDivClass"]);
      });
    });