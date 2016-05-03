System.register(["angular2/core"], function(exports_1, context_1) {
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
    var core_1;
    var zingchart, Chart, ZingChart, DataView;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            Chart = (function () {
                function Chart(config) {
                    this.id = config['id'];
                    this.data = config['data'];
                    this.height = config['height'] || 300;
                    this.width = config['width'] || 600;
                }
                return Chart;
            }());
            ZingChart = (function () {
                function ZingChart(zone) {
                    this.zone = zone;
                }
                ZingChart.prototype.ngAfterViewInit = function () {
                    var _this = this;
                    this.zone.runOutsideAngular(function () {
                        zingchart.render({
                            id: _this.chart['id'],
                            data: _this.chart['data'],
                            width: _this.chart['width'],
                            height: _this.chart['height']
                        });
                    });
                };
                ZingChart.prototype.ngOnDestroy = function () {
                    zingchart.exec(this.chart['id'], 'destroy');
                };
                ZingChart = __decorate([
                    core_1.Component({
                        selector: 'zingchart',
                        inputs: ['chart'],
                        template: "<div id='{{chart.id}}'></div>"
                    }), 
                    __metadata('design:paramtypes', [core_1.NgZone])
                ], ZingChart);
                return ZingChart;
            }());
            DataView = (function () {
                function DataView() {
                    this.json = null;
                }
                DataView.prototype.setData = function (data) {
                    if (data == null) {
                        this.clear();
                    }
                    else {
                        var values = Object.keys(data.data).map(function (k) { return [k, Date.parse(k), data.data[k]]; });
                        if (values.length == 0) {
                            this.clear();
                        }
                        else {
                            var first = values[0];
                            this.charts =
                                [{
                                        id: "chart-" + this.id,
                                        data: {
                                            type: "line",
                                            plot: {
                                                aspect: "spline"
                                            },
                                            "scale-x": {
                                                "min-value": first[1],
                                                "step": "day",
                                                "transform": {
                                                    "type": "date",
                                                    "all": "%Y-%mm-%dd"
                                                }
                                            },
                                            series: [{
                                                    values: Object.keys(values).map(function (k) { return [values[k][1], values[k][2]]; })
                                                }],
                                        },
                                        height: 400,
                                        width: 600
                                    }];
                        }
                    }
                };
                DataView.prototype.clear = function () {
                    this.charts =
                        [{
                                id: "chart-" + this.id,
                                data: {
                                    "labels": [{
                                            "text": "No values found",
                                            "font-family": "Georgia",
                                            "font-size": "50",
                                            "font-color": "red",
                                            "x": "20%",
                                            "y": "40%"
                                        }]
                                },
                                height: 400,
                                width: 600
                            }];
                };
                DataView.prototype.ngOnInit = function () {
                    this.clear();
                };
                __decorate([
                    core_1.Input("chart-id"), 
                    __metadata('design:type', String)
                ], DataView.prototype, "id", void 0);
                DataView = __decorate([
                    core_1.Component({
                        selector: "data-view",
                        directives: [ZingChart],
                        templateUrl: "data_view.html"
                    }), 
                    __metadata('design:paramtypes', [])
                ], DataView);
                return DataView;
            }());
            exports_1("DataView", DataView);
        }
    }
});
//# sourceMappingURL=data_view.js.map