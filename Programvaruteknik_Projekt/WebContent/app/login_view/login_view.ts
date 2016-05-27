import {Component,Output,EventEmitter} from "angular2/core";
import {RouteConfig, Router, ROUTER_DIRECTIVES} from 'angular2/router';
import {DataLoader} from "app/data_loader";
import {DataBridgeService} from "app/data_bridge_service";

@Component({
	selector: "login-view",
	templateUrl: "app/login_view/login_view.html",
	styleUrls: ["app/login_view/login_view.css"]
})

export class LoginView
{
	private username:String;
	private password:String;
	private message:String;
	
	constructor(private router: Router, private dataLoader: DataLoader,private dataBridge:DataBridgeService) 
	{
	
	}
	
	public @Output("onLogin") loginEvent: EventEmitter<String> = new EventEmitter();
	
	private callDirector(serverCaller,alternateErrorMessage:String)
	{ 
		this.message = "";
		
		serverCaller.bind(this.dataLoader)(this.username,this.password).subscribe(data =>
		{
			if(data.success)
			{
				this.dataBridge.username = this.username;
				this.router.navigate(["/Statistics"]);
			}
			else
				this.message = data.message || alternateErrorMessage;
		},
		error =>
		{
			this.message = error;
		});
	}
	
	public login()
	{
		this.callDirector(this.dataLoader.login,"Authentication failed!");
	}
	
	
	public register()
	{
		this.callDirector(this.dataLoader.register,"Registration failed!");
	}
}