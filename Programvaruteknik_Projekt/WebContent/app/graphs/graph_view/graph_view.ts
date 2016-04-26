import {Component} from "angular2/core";
import {DataView} from "app/graphs/data_view/data_view";
import {CorrView} from "app/graphs/corr_view/corr_view";
import {PickSourceView} from "app/graphs/pick_source_view/pick_source_view";
import {DataLoader} from "app/data_loader";
import {DatePickerView} from "app/graphs/date_picker_view/date_picker_view";

@Component({
	selector: "graph-view",
	templateUrl: "app/graphs/graph_view/graph_view.html",
	styleUrls: ["app/graphs/graph_view/graph_view.css"],
	directives: [DataView,CorrView,PickSourceView,DatePickerView]
})
export class GraphView 
{
	constructor(private dataLoader: DataLoader) { }
	
	sourceAJSON:object = null;
	sourceBJSON:object = null;
	
	resolution:object;
	
	setSourceA(corr:object,graph:object,json:object,date_picker:object)
	{
		this.sourceAJSON = json;
		this.dataLoader.getSourceData({},json).subscribe(data=>graph.data = data);
		this.updateCorr(corr,date_picker);
	}
	
	setSourceB(corr:object,graph:object,json:object,date_picker:object)
	{
		this.sourceBJSON = json;
		this.dataLoader.getSourceData({},json).subscribe(data=>graph.data = data);
		this.updateCorr(corr,date_picker);
	}
	
	updateCorr(corr:object,date_picker:object)
	{
		corr.clear();
		console.log("resolution");
		console.log(date_picker.resolution);
		if(this.sourceAJSON != null && this.sourceBJSON != null)
		{
			this.dataLoader.getCorrData(
			{ 
				"resolution": date_picker.resolution
			},
			this.sourceAJSON,this.sourceBJSON).subscribe(data=>corr.update(data));
		}
	}
}