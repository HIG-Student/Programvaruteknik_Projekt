import {Component} from "angular2/core";
import {DataView} from "app/graphs/data_view/data_view";
import {CorrView} from "app/graphs/corr_view/corr_view";
import {PickSourceView} from "app/graphs/pick_source_view/pick_source_view";
import {DataLoader} from "app/data_loader";

@Component({
	selector: "graph-view",
	templateUrl: "app/graphs/graph_view/graph_view.html",
	styleUrls: ["app/graphs/graph_view/graph_view.css"],
	directives: [DataView,CorrView,PickSourceView]
})
export class GraphView 
{
	constructor(private dataLoader: DataLoader) { }
	
	sourceAJSON:object = null;
	sourceBJSON:object = null;
	
	setSourceA(corr:object,graph:object,json:object)
	{
		this.sourceAJSON = json;
		this.dataLoader.getSourceData({},json).subscribe(data=>graph.setData(data));
		this.updateCorr(corr);
	}
	
	setSourceB(corr:object,graph:object,json:object)
	{
		this.sourceBJSON = json;
		this.dataLoader.getSourceData({},json).subscribe(data=>graph.setData(data));
		this.updateCorr(corr);
	}
	
	updateCorr(corr:object)
	{
		corr.clear();
		if(this.sourceAJSON != null && this.sourceBJSON != null)
		{
			this.dataLoader.getCorrData({},this.sourceAJSON,this.sourceBJSON).subscribe(data=>corr.update(data));
		}
	}
}