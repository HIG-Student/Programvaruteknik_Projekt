System.register(["angular2/core", "../../data_loader"], function(exports_1, context_1) {
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
    var core_1, data_loader_1;
    var PickSourceView;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (data_loader_1_1) {
                data_loader_1 = data_loader_1_1;
            }],
        execute: function() {
            PickSourceView = (function () {
                function PickSourceView(dataLoader) {
                    this.dataLoader = dataLoader;
                    this.changeView = new core_1.EventEmitter();
                }
                PickSourceView.prototype.showDataSource = function () {
                    var _this = this;
                    this.dataLoader.getSourceData({}, this.json).subscribe(function (data) {
                        _this.changeView.emit({
                            "json": _this.json,
                            "data": data
                        });
                    });
                };
                PickSourceView.prototype.pickSelect = function (value) {
                    this.inputs = this.builders[value];
                    this.json = {};
                    this.json["source-type"] = value;
                    for (var _i = 0, _a = this.inputs["inputs"]; _i < _a.length; _i++) {
                        var input = _a[_i];
                        this.setValue(input.value, input.values[0].value);
                    }
                };
                PickSourceView.prototype.setValue = function (type, value) {
                    this.json[type] = value;
                };
                PickSourceView.prototype.ngOnInit = function () {
                    var _this = this;
                    this.dataLoader.getSources().subscribe(function (data) {
                        _this.builders = data;
                        _this.builders_keys = Object.keys(_this.builders);
                        _this.pickSelect(_this.builders_keys[0]);
                    });
                };
                __decorate([
                    core_1.Output("change-view"), 
                    __metadata('design:type', core_1.EventEmitter)
                ], PickSourceView.prototype, "changeView", void 0);
                PickSourceView = __decorate([
                    core_1.Component({
                        selector: "pick-source-view",
                        templateUrl: "pick_source_view.html",
                        styleUrls: ["pick_source_view.css"]
                    }), 
                    __metadata('design:paramtypes', [data_loader_1.DataLoader])
                ], PickSourceView);
                return PickSourceView;
            }());
            exports_1("PickSourceView", PickSourceView);
        }
    }
});
//# sourceMappingURL=pick_source_view.js.map