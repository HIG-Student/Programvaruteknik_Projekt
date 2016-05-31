import {Injectable} from 'angular2/core';
import {Http} from 'angular2/http';
import {ProgressService} from "app/progress_service";
import 'rxjs/add/operator/map';
import {Observable} from 'rxjs/Rx';

@Injectable()
export class DataLoader
{
	constructor(private http: Http, private progressor: ProgressService) { }
	
	public getCorrData(data,sourceA,sourceB)
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
	
	public getSourceData(data,source)
	{
		data["type"] = "data-source";
		
		if(!data["data"])
			data["data"] = { };

		if(source)
			data.data.source = source;
		
		return this.post(data);
	}
	
	public getSources()
	{
		return this.post(
		{
			"type": "sources"
		});
	}
	
	public sendSaveData(data:object)
	{
		return this.post(
		{
			"type": "save",
			"data": data
		});
	}
	
	public getSavedData(id:number)
	{	
		return this.post(
		{
			"type": "load",
			"data": id
		});
	}
	
	public deleteData(id:number)
	{	
		return this.post(
		{
			"type": "delete",
			"data": id
		});
	}
	
	public getSaveList()
	{
		return this.post(
		{
			"type": "list"
		});
	}
	
	public login(username:String,password:String):Observable
	{
		return this.post(
		{
			"type": "login",
			"data":
			{
				"username": username,
				"password": password
			}
		});
	}

	public register(username:String,password:String):Observable
	{
		return this.post(
		{
			"type": "register",
			"data":
			{
				"username": username,
				"password": password
			}
		});
	}
	
	private isLoggedIn()
	{
		return this.post(
		{
			"type": "login-status"
		});
	}
	
	private logout()
	{
		console.log("Före");
		return this.post(
		{
			"type": "logout"
		});
		console.log("Efter");
	}
		
	private post(data:object)
	{		
		return Observable.create(observer =>
		{
			this.progressor.addLoading();
			
			var thing = this.http
				.post("http://130.243.14.18:58080/Projekt/StatisticsServlet", JSON.stringify(data))
				.map(res => res.json().data)
				.do(data =>
					{
						observer.next(data);
					},
					error =>
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