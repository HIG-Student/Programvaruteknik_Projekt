import {Component} from "angular2/core";
import {DataView} from "app/graphs/data_view/data_view";
import {CorrView} from "app/graphs/corr_view/corr_view";


@Component({
	selector: "graph-view",
	templateUrl: "app/graphs/graph_view/graph_view.html",
	styleUrls: ["app/graphs/graph_view/graph_view.css"],
	directives: [DataView,CorrView]
})
export class GraphView 
{
	constructor() { }
}