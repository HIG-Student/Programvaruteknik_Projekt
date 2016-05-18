import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {ProgressService} from "app/progress_service";
import 'rxjs/add/operator/map';
import {Observable} from 'rxjs/Rx';

@Injectable()
export class DataLoader
{
	constructor(private http: Http, private progressor: ProgressService) { }
	
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
		
		return this.post(data);
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
		
		return this.post(data);
	}
	
	getSources()
	{
		return this.post(
		{
			"type": "sources"
		});
	}
	
	sendSaveData(data:object)
	{
		return this.post(
		{
			"type": "save",
			"data": data
		});
	}
	
	getSavedData(id:number)
	{	
		return this.post(
		{
			"type": "load",
			"data": id
		});
	}
	
	deleteData(id:number)
	{	
		return this.post(
		{
			"type": "delete",
			"data": id
		});
	}
	
	getSaveList()
	{
		return this.post(
		{
			"type": "list"
		});
	}
	
	private post(data:object)
	{		
		return Observable.create(observer =>
		{
			this.progressor.addLoading();
			
			window.obs = observer;
			
			var thing = this.http
				.post("SampleServlet", JSON.stringify(data))
				.map(res => res.json().data)
				.do(data =>
					{
						observer.next(data);
					},
					data =>
					{
						observer.error(error);
						this.progressor.removeLoading();
					},
					data =>
					{
						observer.complete();
						this.progressor.removeLoading();
					})
				.subscribe();
		});
	}
}