import {Component,Output,EventEmitter} from "angular2/core";

@Component({
	selector: "login-view",
	templateUrl: "app/login_view/login_view.html",
	styleUrls: ["app/login_view/login_view.css"]
})

export class LoginView
{
	public @Output("onLogin") loginEvent: EventEmitter<String> = new EventEmitter();
	
	public login()
	{
		this.loginEvent.emit("gnu");
	}
}