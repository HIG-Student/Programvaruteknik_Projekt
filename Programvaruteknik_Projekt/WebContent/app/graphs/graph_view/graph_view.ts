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
	
	sourceA:object = null;
	sourceA_JSON:object = null;
	
	sourceB:object = null;
	sourceB_JSON:object = null;
	
	sourceCorr:object = null;
	
	resolution:string = "YEAR";
	
	setSourceA(json:object)
	{
		this.sourceA_JSON = json;
		this.dataLoader.getSourceData({},json).subscribe(data=>
		{
			this.sourceA = data;
			this.updateCorr();
		});
	}
	
	setSourceB(json:object)
	{
		this.sourceB_JSON = json;
		this.dataLoader.getSourceData({},json).subscribe(data=>
		{
			this.sourceB = data;
			this.updateCorr();
		});
	}
	
	setResolution(resolution:string)
	{
		this.resolution = resolution;
		this.updateCorr();
	}
	
	updateCorr()
	{
		this.sourceCorr = null;
		
		if(this.sourceA != null && this.sourceB != null)
		{
			this.dataLoader.getCorrData(
			{ 
				"resolution": this.resolution
			},
			this.sourceA_JSON,this.sourceB_JSON).subscribe(data=>
			{
				this.sourceCorr = data;
			});
		}
	}
}