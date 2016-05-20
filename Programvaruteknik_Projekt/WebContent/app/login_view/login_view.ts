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
		console.log("Trying to login with", this.username);
		
		this.dataLoader.login(this.username,this.password).subscribe(function(data)
		{
			this.router.navigate(["/Statistics"]);
		},
		function(error)
		{
			this.message = error;
		});
	}
}