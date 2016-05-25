import {Component,Output,EventEmitter} from "angular2/core";
import {RouteConfig, Router, ROUTER_DIRECTIVES} from 'angular2/router';
import {DataLoader} from "app/data_loader";

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
	
	constructor(private router: Router, private dataLoader: DataLoader) 
	{
	
	}
	
	public @Output("onLogin") loginEvent: EventEmitter<String> = new EventEmitter();
	
	public login()
	{
		this.message = "";
		
		this.dataLoader.login(this.username,this.password).subscribe(data =>
		{
			if(data.success)
				this.router.navigate(["/Statistics"]);
			else
				this.message = data.message || "Authentication failed!";
		},
		error =>
		{
			this.message = error;
		});
	}
	
	
	public register()
	{
		this.message = "";
		
		this.dataLoader.register(this.username,this.password).subscribe(data =>
		{
			if(data.success)
				this.router.navigate(["/Statistics"]);
			else
				this.message = data.message || "Registration failed!";
		},
		error =>
		{
			this.message = error;
		});
	}
}