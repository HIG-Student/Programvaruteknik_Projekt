import {Component,Output,Input,EventEmitter} from "angular2/core";
import {DataLoader} from "app/data_loader";

@Component({
	selector: "pick-source-view",
	templateUrl: "app/graphs/pick_source_view/pick_source_view.html",
	styleUrls: ["app/graphs/pick_source_view/pick_source_view.css"]
})
export class PickSourceView 
{
	@Output("change-view") changeView: EventEmitter<any> = new EventEmitter();
	
	@Input() data: object;
	
	constructor(private dataLoader: DataLoader) { }
	
	showDataSource()
	{
		this.dataLoader.getSourceData({},this.json).subscribe(data =>
		{
			this.changeView.emit(this.json,data);
		});
	}
	
	pickSelect(value)
	{
		this.inputs = this.builders[value];
		
		this.json = { };
		this.json["source-type"] = value;
		
		for(input of this.inputs.inputs)
			this.setValue(input.value,input.values[0].value);
	}
	
	setValue(type,value)
	{
		this.json[type] = value;
	}
	
	ngOnInit()
	{
		this.dataLoader.getSources().subscribe(data =>
		{
			this.builders = data;
			this.builders_keys = Object.keys(this.builders);
			this.pickSelect(this.builders_keys[0]);
		});
	}
}