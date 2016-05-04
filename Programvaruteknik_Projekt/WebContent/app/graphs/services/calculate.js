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
    var CalculateService;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            }],
        execute: function() {
            CalculateService = (function () {
                function CalculateService() {
                }
                CalculateService.prototype.calculate = function (xyValues) {
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
                CalculateService = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [])
                ], CalculateService);
                return CalculateService;
            }());
            exports_1("CalculateService", CalculateService);
        }
    }
});
//# sourceMappingURL=calculate.js.map