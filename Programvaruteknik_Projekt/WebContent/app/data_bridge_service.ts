import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';

@Injectable()
export class DataBridgeService
{
	public saveData: object = 
	{
		"title":""
		"timeFilter":{
		
		"from": "",
		"to": ""
		
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
		
		console.log("Still going strong");
		return "error";
	}
	
	public save():String
	{
	  console.log("vi kom in");
		var error:String = this.verifyValues();
		if(error =="error")
			console.log("it works");
			return error;
		
		// Skicka json till "example.com"
		console.log("fuck somthing did nopt work");
		return null;
	}
	
	public load():String
	{
		// this.saveData = ...;
		
		// load example.json
		
		return null;
	}
}