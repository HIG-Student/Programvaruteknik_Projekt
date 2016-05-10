import {Component,Output,Input,EventEmitter} from "angular2/core";
import {DataLoader} from "app/data_loader";
import {DataBridgeService} from "app/data_bridge_service";

@Component({
	selector: "pick-source-view",
	templateUrl: "app/graphs/pick_source_view/pick_source_view.html",
	styleUrls: ["app/graphs/pick_source_view/pick_source_view.css"]
})
export class PickSourceView 
{
	@Output() onPick: EventEmitter<any> = new EventEmitter();
	
	private 
	@Input() data:object;
	
	constructor(private dataLoader: DataLoader,private dataBridgeService: DataBridgeService) { }

	pick()
	{
		this.onPick.emit(this.data);
	}
	
	selectedValueChange(value)
	{
			
		console.log("PICKEED",value);
		// dataLoaded
		this.inputs = this.builders[value];
		
		this.data = { };
		this.data["source-type"] = value;
		
		for(input of this.inputs.inputs)
			this.setValue(input.value,input.values[0].value);
	}
	
	setValue(type:string,value:string)
	{
		this.data[type] = value;
	}
	
	ngOnInit()
	{
	
		this.dataLoader.getSources().subscribe(data =>
		{
			this.builders = data;
			this.builders_keys = Object.keys(this.builders);
			this.selectedValueChange(this.builders_keys[0]);		
		});
		
	}
}
