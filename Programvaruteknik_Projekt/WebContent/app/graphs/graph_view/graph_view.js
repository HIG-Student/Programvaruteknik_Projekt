System.register(["angular2/core", "../data_view/data_view", "../corr_view/corr_view", "../pick_source_view/pick_source_view", "../../data_loader"], function(exports_1, context_1) {
    "use strict";
    var __moduleName = context_1 && context_1.id;
    var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    var __metadata = (this && this.__metadata) || function (k, v) {
        if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
    };
    var core_1, data_view_1, corr_view_1, pick_source_view_1, data_loader_1;
    var GraphView;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (data_view_1_1) {
                data_view_1 = data_view_1_1;
            },
            function (corr_view_1_1) {
                corr_view_1 = corr_view_1_1;
            },
            function (pick_source_view_1_1) {
                pick_source_view_1 = pick_source_view_1_1;
            },
            function (data_loader_1_1) {
                data_loader_1 = data_loader_1_1;
            }],
        execute: function() {
            GraphView = (function () {
                function GraphView(dataLoader) {
                    this.dataLoader = dataLoader;
                    this.sourceAJSON = null;
                    this.sourceBJSON = null;
                }
                GraphView.prototype.setSourceA = function (corr, graph, data) {
                    this.sourceAJSON = data.json;
                    this.dataLoader.getSourceData({}, data.json).subscribe(function (data) { return graph.setData(data); });
                    this.updateCorr(corr);
                };
                GraphView.prototype.setSourceB = function (corr, graph, data) {
                    this.sourceBJSON = data.json;
                    this.dataLoader.getSourceData({}, data.json).subscribe(function (data) { return graph.setData(data); });
                    this.updateCorr(corr);
                };
                GraphView.prototype.updateCorr = function (corr) {
                    corr.clear();
                    if (this.sourceAJSON != null && this.sourceBJSON != null) {
                        this.dataLoader.getCorrData({}, this.sourceAJSON, this.sourceBJSON).subscribe(function (data) { return corr.update(data); });
                    }
                };
                GraphView = __decorate([
                    core_1.Component({
                        selector: "graph-view",
                        templateUrl: "graph_view.html",
                        styleUrls: ["graph_view.css"],
                        directives: [data_view_1.DataView, corr_view_1.CorrView, pick_source_view_1.PickSourceView]
                    }), 
                    __metadata('design:paramtypes', [data_loader_1.DataLoader])
                ], GraphView);
                return GraphView;
            }());
            exports_1("GraphView", GraphView);
        }
    }
});
//# sourceMappingURL=graph_view.js.map