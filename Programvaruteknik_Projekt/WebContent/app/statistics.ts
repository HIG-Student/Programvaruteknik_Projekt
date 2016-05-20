import {Component,OnInit} from "angular2/core";
import {RouteConfig, Router, ROUTER_DIRECTIVES} from 'angular2/router';
import {GraphView} from "app/graphs/graph_view/graph_view";
import {LoginView} from "app/login_view/login_view";
import {DataLoader} from "app/data_loader";
import {ProgressService} from "app/progress_service";
import {HTTP_PROVIDERS} from 'angular2/http';



@Component((
{
	selector: "statistics",
	templateUrl: "app/statistics.html",
	styleUrls: ["app/statistics.css"],
	directives: [GraphView,LoginView,ROUTER_DIRECTIVES],
	providers: [ProgressService,DataLoader,HTTP_PROVIDERS]
})
@RouteConfig(
[
	{
		path: '/login',  
		component: LoginView,
		name: 'Login'
	},
	{
		path: '/statistics',
		component: GraphView,
		name: 'Statistics'
	}
])
export class Statistics implements OnInit
{
	constructor(private router: Router, private progressor: ProgressService) 
	{
		
	}
	
	ngOnInit() 
	{
		console.log("G");
		this.router.navigate(['/Login']);
	}
}