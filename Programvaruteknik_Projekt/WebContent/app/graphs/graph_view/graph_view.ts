import {Component,Output,Input,EventEmitter} from "angular2/core";
import {DataView} from "app/graphs/data_view/data_view";
import {CorrView} from "app/graphs/corr_view/corr_view";
import {PickSourceView} from "app/graphs/pick_source_view/pick_source_view";
import {DataLoader} from "app/data_loader";
import {DatePickerView} from "app/graphs/date_picker_view/date_picker_view";
import {DataBridgeService} from "app/data_bridge_service";

@Component({
	selector: "graph-view",
	templateUrl: "app/graphs/graph_view/graph_view.html",
	styleUrls: ["app/graphs/graph_view/graph_view.css"],
	directives: [DataView,CorrView,PickSourceView,DatePickerView],
	providers:[DataBridgeService]	
})
export class GraphView 
{
	constructor(private dataLoader: DataLoader, private dataBridgeService: DataBridgeService) 
	{
	
	}
	
	sourceA:object = null;
	sourceA_JSON:object = null;
	
	sourceB:object = null;
	sourceB_JSON:object = null;
	
	sourceCorr:object = null;
	
	public @Output() onPick: EventEmitter<any> = new EventEmitter();
	
	setTimeFilter(json:object)
	{
		this.dataBridgeService.saveData.timeFilter = json;
		this.updateCorr();
	}
	
	setSourceA(json:object)
	{
		this.sourceA_JSON = json;
		this.dataLoader.getSourceData({},json).subscribe(data=>
		{	
			this.sourceA = data;
			this.dataBridgeService.setSourceA(data,json);
			this.updateCorr();
		});
	}
	
	setSourceB(json:object,from:string,to:string)
	{
		this.sourceB_JSON = json;
		this.dataLoader.getSourceData({},json).subscribe(data=>
		{
			this.sourceB = data;
			this.dataBridgeService.setSourceB(data,json);
			this.updateCorr();
		});
	}
	
	updateCorr()
	{
		this.sourceCorr = null;
		
		if(this.sourceA != null && this.sourceB != null)
		{
			this.dataLoader.getCorrData(
			{
				"resolution": this.dataBridgeService.saveData.timeFilter.resolution
			},
			this.sourceA_JSON,this.sourceB_JSON).subscribe(data=>
			{
				this.dataBridgeService.setSourceCorr(data);
				this.sourceCorr = data;		
			});
		}
	}

	clickSave()
	{
		this.dataBridgeService.save();
	}
	
	clickLoad()
	{
		
      	this.dataBridgeService.load();
      	// sourceAParam
      	// sourceBParam
	}
}