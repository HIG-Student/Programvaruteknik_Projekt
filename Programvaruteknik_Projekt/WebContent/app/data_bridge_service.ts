import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';

@Injectable()
export class DataBridgeService
{
	public saveData: object = 
	{
		"title":""
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
		// Felmedelande och check
	}
	
	public save():String
	{
		var error:String = verifyValues();
		if(error)
			return error;
		
		// Skicka json till "example.com"
		
		return null;
	}
	
	public load():String
	{
		// this.saveData = ...;
		
		// load example.json
		
		return null;
	}
}