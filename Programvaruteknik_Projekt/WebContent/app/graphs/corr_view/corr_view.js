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
    var zingchart, Chart, ZingChart, CorrView;
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
                            height: _this.chart['height'],
                            events: {
                                load: function () {
                                    $("#chart-corr").drawTrendline();
                                }
                            }
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
            CorrView = (function () {
                function CorrView() {
                }
                CorrView.prototype.calculate = function (xyValues) {
                    var xValues = [xyValues.length];
                    var yValues = [xyValues.length];
                    for (var i = 0; i < xyValues.length; i++) {
                        xValues[i] = xyValues[i][0];
                        yValues[i] = xyValues[i][1];
                    }
                    var nbrOfElements = xyValues.length;
                    var xSum = getSumOf(xValues);
                    var ySum = getSumOf(yValues);
                    var xySum = getSumOfArrayNodeProducts(xValues, yValues);
                    var xSquaredSum = getSumOfArrayNodeProducts(xValues, xValues);
                    var ySquaredSum = getSumOfArrayNodeProducts(yValues, yValues);
                    function getSumOfArrayNodeProducts(aValues, bValues) {
                        var sum = 0;
                        for (var i = 0; i < nbrOfElements; i++) {
                            sum += aValues[i] * bValues[i];
                        }
                        return sum;
                    }
                    function getSumOf(values) {
                        var sum = 0;
                        for (var i = 0; i < values.length; i++) {
                            sum += values[i];
                        }
                        return sum;
                    }
                    function getKValue() {
                        return ((nbrOfElements * xySum) - (xSum * ySum)) /
                            ((nbrOfElements * xSquaredSum) - (xSum * xSum));
                    }
                    function getMValue() {
                        return ((xSquaredSum * ySum) - (xSum * xySum)) /
                            ((nbrOfElements * xSquaredSum) - (xSum * xSum));
                    }
                    function getRValue() {
                        return (Math.pow(((nbrOfElements * xySum) - (xSum * ySum)), 2)) /
                            (((nbrOfElements * xSquaredSum) - (xSum * xSum)) * ((nbrOfElements * ySquaredSum) - (ySum * ySum)));
                    }
                    return {
                        "m": getMValue(),
                        "k": getKValue(),
                        "R": getRValue(),
                    };
                };
                CorrView.prototype.update = function (data) {
                    var info = this.calculate(Object.keys(data.data).map(function (k) { return [data.data[k].a, data.data[k].b]; }));
                    this.charts =
                        [{
                                id: "chart-" + this.id,
                                data: {
                                    type: "scatter",
                                    "scale-x": {
                                        label: {
                                            text: data.a_name + " (" + data.a_unit + ")"
                                        }
                                    },
                                    "scale-y": {
                                        label: {
                                            text: data.b_name + " (" + data.b_unit + ")"
                                        },
                                    },
                                    plotarea: {
                                        margin: "75px"
                                    },
                                    "tooltip": {
                                        "html-mode": true,
                                        "border-width": 2,
                                        "border-radius": 5,
                                        "border-color": "#000",
                                        "text": "<table><tr><td>Datum:</td><td>%data-dates</td></tr><tr><td>%data-name-a:</td><td>%v %data-unit-a</td></tr><tr><td>%data-name-b:</td><td>%k %data-unit-b</td></tr></table>"
                                    },
                                    series: [{
                                            values: Object.keys(data.data).map(function (k) { return [data.data[k].a, data.data[k].b]; }),
                                            "data-dates": Object.keys(data.data),
                                            "data-name-a": data.a_name,
                                            "data-name-b": data.b_name,
                                            "data-unit-a": data.a_unit,
                                            "data-unit-b": data.b_unit
                                        }],
                                },
                                height: 400,
                                width: 600
                            }];
                };
                CorrView.prototype.ngOnInit = function () {
                    this.clear();
                };
                CorrView.prototype.clear = function () {
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
                __decorate([
                    core_1.Input("chart-id"), 
                    __metadata('design:type', String)
                ], CorrView.prototype, "id", void 0);
                CorrView = __decorate([
                    core_1.Component({
                        selector: "corr-view",
                        directives: [ZingChart],
                        templateUrl: "corr_view.html"
                    }), 
                    __metadata('design:paramtypes', [])
                ], CorrView);
                return CorrView;
            }());
            exports_1("CorrView", CorrView);
        }
    }
});
//# sourceMappingURL=corr_view.js.map