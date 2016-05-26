import {Component,Output,Input,EventEmitter} from "angular2/core";
import {DataView} from "app/graphs/data_view/data_view";
import {CorrView} from "app/graphs/corr_view/corr_view";
import {PickSourceView} from "app/graphs/pick_source_view/pick_source_view";
import {DataLoader} from "app/data_loader";
import {DatePickerView} from "app/graphs/date_picker_view/date_picker_view";
import {DataBridgeService} from "app/data_bridge_service";
import {DateView} from "app/date_view/date_view";

@Component({
	selector: "graph-view",
	templateUrl: "app/graphs/graph_view/graph_view.html",
	styleUrls: ["app/graphs/graph_view/graph_view.css"],
	directives: [DataView,CorrView,PickSourceView,DatePickerView,DateView]
})
export class GraphView 
{
	constructor(private dataLoader: DataLoader, private dataBridgeService: DataBridgeService) 
	{
		this.updateSaveList();
		
		dataBridgeService.dataLoaded.subscribe(data =>
		{
			this.sourceA_JSON = JSON.parse(JSON.stringify(data.sourceAParam));
			this.sourceB_JSON = JSON.parse(JSON.stringify(data.sourceBParam));
		});
	}
	
	updateSaveList()
	{
		this.dataLoader.getSaveList().subscribe(data =>
		{
			this.savesList = data;
			this.pickedSave = -1;
		});
	}
	
	pickedSave:number = -1;
	savesList:object;
	
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
		this.sourceA_JSON = JSON.parse(JSON.stringify(json));
		this.dataLoader.getSourceData({},json).subscribe(data=>
		{	
			this.sourceA = data;
			this.dataBridgeService.setSourceA(data,json);
			this.updateCorr();
		});
	}
	
	setSourceB(json:object,from:string,to:string)
	{
		this.sourceB_JSON = JSON.parse(JSON.stringify(json));
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
		this.dataBridgeService.save(savedEntry=>this.updateSaveList());
	}
	
	clickLoad()
	{
      	this.dataBridgeService.load(this.pickedSave);
	}
	
	clickDelete()
	{
      	this.dataBridgeService.delete(this.pickedSave,savedEntry=>this.updateSaveList());
	}
}