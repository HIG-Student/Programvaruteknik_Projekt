import {Injectable,Output,EventEmitter} from 'angular2/core';
import {Http} from 'angular2/http';
import {DataLoader} from "app/data_loader";	

@Injectable({
	providers:[DataLoader]
})
export class DataBridgeService
{
	constructor(private dataLoader: DataLoader) { }
	
	@Output() dataLoaded: EventEmitter<any> = new EventEmitter();
	
	
	public saveData: object = 
	{
		"title":"",
		"sourceA": null,
		"sourceAParam": { "source-type": "Constant" },
		"sourceB": null,
		"sourceBParam": { "source-type": "Constant" },
		"sourceCorr": null,
		"timeFilter":
		{
			"from": "",
			"to": "",
			"resolution": "YEAR"
		}
	};
	
	public setTimeFilter(timeFilter: object)
	{
		this.saveData["timeFilter"]= timeFilter;
	}
	
	public setSourceA(sourceA:object,json:object)
	{
		this.saveData["sourceA"]= sourceA;
		this.saveData["sourceAParam"]= json;
	}
	
	public setSourceB(sourceB:object,json:object)
	{
		this.saveData["sourceB"]= sourceB;
		this.saveData["sourceBParam"]= json;
	}
	
	public setSourceCorr(sourceCorr:object)
	{
		this.saveData["sourceCorr"]= sourceCorr;
	}
	
	private verifyValues():String
	{
		if(this.saveData["sourceA"] == null)
		{
			return "Missing sourceA";
		}
		
		if(this.saveData["sourceB"] == null)
		{
			return "Missing sourceB";
		}
		
		if(this.saveData["sourceCorr"] == null)
		{
			return "Missing sourceCorr";
		}
		
		if(this.saveData["title"] == "")
		{
			return "Missing title";
		}
		
		return null;
	}
	
	public save():String
	{
		var error:String = this.verifyValues();
		
		if(error)
		{
			console.log("Error:",error);	
			return error;
		}
		
		this.dataLoader.sendSaveData(this.saveData).subscribe(data=>
		{
			console.log("Sent data",data);	
		},err=>
		{
			console.log("Error on send",err);
		});
		
		return null;
	}
	
	public load():String
	{
		this.dataLoader.getSavedData().subscribe(data =>
		{
			this.saveData = data;
			this.dataLoaded.emit(data);
		});
		return null;
	}
}