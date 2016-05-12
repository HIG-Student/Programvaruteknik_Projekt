import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import 'rxjs/add/operator/map';

@Injectable()
export class DataLoader
{
	constructor(private http: Http) { }
	
	getCorrData(data,sourceA,sourceB)
	{

		if(!data)
			data = { };
			
		data["type"] = "data-correlation";
			
		if(!data["resolution"])
			data["resolution"] = "YEAR";
			
		if(!data["merge-type-x"])
			data["merge-type-x"] = "AVERAGE";
			
		if(!data["merge-type-y"])
			data["merge-type-y"] = "AVERAGE";
		
		data["data"] = 			
		{
			"sourceA": sourceA,
			"sourceB": sourceB
		};
		
		return this.http
		.post("SampleServlet", JSON.stringify(data))
		.map(res => res.json().data);
	}
	
	getSourceData(data,source)
	{
		data["type"] = "data-source";
		
		if(!data["data"])
			data["data"] = { };
		
		//data["resolution"] = "YEAR";
		//data["merge-type-x"] = "AVERAGE";
		//data["merge-type-y"] = "AVERAGE";
		
		if(source)
			data.data.source = source;
		
		return this.http
		.post("SampleServlet", JSON.stringify(data))
		.map(res => res.json().data);
	}
	
	getSources()
	{
		return this.http
		.post("SampleServlet", JSON.stringify(
		{
			"type": "sources"
		}))
		.map(res => res.json().data);
	}
	
	sendSaveData(data:object)
	{
		return this.http
		.post("SampleServlet", JSON.stringify(
		{
			"type": "save",
			"data": data
		}))
		.map(res => res.json().data);
	}
	
	getSavedData(id:number)
	{	
		return this.http
		.post("SampleServlet", JSON.stringify(
		{
			"type": "load",
			"data": id
		}))
		.map(res => res.json().data);
	}
	
	getSaveList()
	{	
		return this.http
		.post("SampleServlet", JSON.stringify(
		{
			"type": "list"
		}))
		.map(res => res.json().data);
	}
}