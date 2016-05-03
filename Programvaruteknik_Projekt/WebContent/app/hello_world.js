System.register(["angular2/core", "./graphs/graph_view/graph_view", "./data_loader", 'angular2/http'], function(exports_1, context_1) {
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
    var core_1, graph_view_1, data_loader_1, http_1;
    var HelloWorld;
    return {
        setters:[
            function (core_1_1) {
                core_1 = core_1_1;
            },
            function (graph_view_1_1) {
                graph_view_1 = graph_view_1_1;
            },
            function (data_loader_1_1) {
                data_loader_1 = data_loader_1_1;
            },
            function (http_1_1) {
                http_1 = http_1_1;
            }],
        execute: function() {
            HelloWorld = (function () {
                function HelloWorld() {
                    this.yourName = '';
                }
                HelloWorld = __decorate([
                    core_1.Component({
                        selector: "hello-world",
                        templateUrl: "hello_world.html",
                        styleUrls: ["hello_world.css"],
                        directives: [graph_view_1.GraphView],
                        providers: [http_1.HTTP_PROVIDERS, data_loader_1.DataLoader]
                    }), 
                    __metadata('design:paramtypes', [])
                ], HelloWorld);
                return HelloWorld;
            }());
            exports_1("HelloWorld", HelloWorld);
        }
    }
});
//# sourceMappingURL=hello_world.js.map