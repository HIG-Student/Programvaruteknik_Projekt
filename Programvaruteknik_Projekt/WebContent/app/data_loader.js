System.register(['angular2/core', 'angular2/http', 'rxjs/add/operator/map'], function(exports_1, context_1) {
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
    var core_1, http_1;
    var DataLoader;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            },
            function (_1) {}],
        execute: function() {
            DataLoader = (function () {
                function DataLoader(http) {
                    this.http = http;
                }
                DataLoader.prototype.getCorrData = function (data, sourceA, sourceB) {
                    return this.http
                        .post("SampleServlet", JSON.stringify({
                        "type": "data-correlation",
                        "resolution": "YEAR",
                        "merge-type-x": "AVERAGE",
                        "merge-type-y": "AVERAGE",
                        "data": {
                            "sourceA": sourceA,
                            "sourceB": sourceB
                        }
                    }))
                        .map(function (res) { return res.json().data; });
                };
                DataLoader.prototype.getSourceData = function (data, source) {
                    console.log("get", data, source);
                    data["type"] = "data-source";
                    if (!data["data"])
                        data["data"] = {};
                    //data["resolution"] = "YEAR";
                    //data["merge-type-x"] = "AVERAGE";
                    //data["merge-type-] = "AVERAGE";
                    if (source)
                        data.data.source = source;
                    return this.http
                        .post("SampleServlet", JSON.stringify(data))
                        .map(function (res) { return res.json().data; });
                };
                DataLoader.prototype.getSources = function () {
                    return this.http
                        .post("SampleServlet", JSON.stringify({
                        "type": "sources"
                    }))
                        .map(function (res) { return res.json().data; });
                };
                DataLoader = __decorate([
                    core_1.Injectable(), 
                    __metadata('design:paramtypes', [http_1.Http])
                ], DataLoader);
                return DataLoader;
            }());
            exports_1("DataLoader", DataLoader);
        }
    }
});
//# sourceMappingURL=data_loader.js.map